package com.sanel.mybatis.sql.validate.util;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.sanel.mybatis.sql.validate.domain.Column;
import com.sanel.mybatis.sql.validate.domain.Index;
import com.sanel.mybatis.sql.validate.domain.Result;
import com.sanel.mybatis.sql.validate.domain.Table;
import com.sanel.mybatis.sql.validate.*;

import java.sql.*;
import java.util.*;

/**
 * @author suzhiwen
 * @description SQL校验工具
 * @date 2022/4/14  下午9:38
 * <p>
 * 建表规则:
 * 表名必须为小写,不含特殊字符,长度小于32
 * 表引擎必须为INNODB
 * 表必须有注释comment，不建议唱过50个字
 * 表字符集必须为utf8/utf8mb4
 * 表自增键必须是整数开始
 * 表名长度不能超过32位
 * <p>
 * 字段:
 * 必须有类型为bigint unsigned 自增 长度大于等于10 名称为id的主键, 主键不关联业务(数据迁移整合/分库分表/索引分页 引起不必要的麻烦)
 * 表字段名必须为小写,不含特殊字符,长度小于32
 * int类型必须是 unsigned 长度不能大于10
 * varchar长度小于3000
 * text字段个数不能超过3个
 * 索引字段必须为not null/或者自增
 * not null字段必须要有默认值
 * 必须有注释comment，不建议唱过50个字
 * 必须包含id、date_created、created_by、date_updated、updated_by五个字段。 修改溯源
 * `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '物理主键'`date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 * `created_by` varchar(100)  NOT NULL DEFAULT 'sys' COMMENT '创建者',
 * `date_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',`updated_by` varchar(100) NOT NULL DEFAULT 'sys' COMMENT '更新人',PRIMARY KEY (`id`),
 * <p>
 * 索引:
 * 索引个数不能超过5个（包括主键）
 * 索引不能有重复
 * <p>
 * 其他：
 * ddl脚本修改表结构时，对同一个表的操作需要写在同一个语句内，同一个表的操作分成多个alter语句，有可能会导致锁表。
 */
public class SQLValidate {

    private static final List<Validate> validateChain = new ArrayList<>();

    static {
        validateChain.add(new TableRuleValidate());
        validateChain.add(new IndexRuleValidate());
        validateChain.add(new ColumnRuleValidate());
        validateChain.add(new ColumnExtendsRuleValidate());
    }


    public static void doValidate(String sql) {
        Table table = parserToTable(sql);

        for (Validate validate : validateChain) {
            Result result = validate.doValidate(table);
            if (!result.isPass()) {
                throw new RuntimeException(result.getMsg());
            }
        }
    }


    public static String getCreateTableSql(Connection connection, String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("show create table " + tableName);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        Map<String, Object> objectMap = new HashMap<>();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                objectMap.put(resultSetMetaData.getColumnName(i),
                        resultSet.getObject(i));
            }
        }
        return String.valueOf(objectMap.get("Create Table"));
    }

    public static Table parserToTable(String sql) {
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, "mysql");
        List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();

        Table table = null;
        // 这里只有一条数据
        for (SQLStatement sqlStatement : sqlStatements) {
            table = parserToTable(sqlStatement);
            Map<String, Column> columnMap = parserToColumns(sqlStatement);
            List<Index> indexList = parserToIndex(sqlStatement, columnMap);
            table.setColumnMap(columnMap);
            table.setIndexList(indexList);
        }
        return table;
    }


    private static Table parserToTable(SQLStatement sqlStatement) {
        MySqlCreateTableStatement cs = (MySqlCreateTableStatement) sqlStatement;
        Table table = new Table();
        table.setTableName(cs.getTableName().replace("`", ""));
        table.setComment(Objects.isNull(cs.getComment()) ? null : cs.getComment().toString());
        SQLExpr ENGINE = cs.getOption("ENGINE");
        if (Objects.nonNull(ENGINE)) {
            table.setEngine(ENGINE.toString());
        }
        SQLExpr AUTO_INCREMENT = cs.getOption("AUTO_INCREMENT");
        if (Objects.nonNull(AUTO_INCREMENT)) {
            table.setAutoIncrement(Integer.valueOf(AUTO_INCREMENT.toString()));
        }
        SQLExpr CHARSET = cs.getOption("CHARSET");
        if (Objects.nonNull(CHARSET)) {
            table.setCharset(CHARSET.toString());
        }
        return table;
    }

    private static List<Index> parserToIndex(SQLStatement sqlStatement, Map<String, Column> columnMap) {
        MySqlCreateTableStatement cs = (MySqlCreateTableStatement) sqlStatement;
        List<Index> indexList = new ArrayList<>();
        for (MySqlKey mysqlKey : cs.getMysqlKeys()) {
            SQLIndexDefinition indexDefinition = mysqlKey.getIndexDefinition();

            Index index = new Index();
            index.setName(String.valueOf(indexDefinition.getName()));
            index.setIndexType(Objects.isNull(indexDefinition.getType()) ? mysqlKey.getIndexType() : indexDefinition.getType());
            List<Column> indexColumn = new ArrayList<>();
            index.setColumnList(indexColumn);

            for (SQLSelectOrderByItem columnItem : indexDefinition.getColumns()) {
                Column column = columnMap.get(String.valueOf(columnItem.getExpr()).replace("`", ""));
                column.setIndexKey(true);
                indexColumn.add(column);
            }
            indexList.add(index);
        }
        return indexList;
    }

    private static Map<String, Column> parserToColumns(SQLStatement sqlStatement) {
        MySqlCreateTableStatement cs = (MySqlCreateTableStatement) sqlStatement;
        Map<String, Column> columnMap = new HashMap<>();
        // 组装列信息
        for (SQLColumnDefinition columnDefinition : cs.getColumnDefinitions()) {
            SQLDataTypeImpl dataType = (SQLDataTypeImpl) columnDefinition.getDataType();
            Column column = new Column();
            column.setDefaultValue(String.valueOf(columnDefinition.getDefaultExpr()));
            column.setComment(Objects.isNull(columnDefinition.getComment()) ? null : columnDefinition.getComment().toString());
            column.setName(columnDefinition.getColumnName().replace("`", ""));
            column.setType(dataType.getName());

            column.setLength(dataType.getArguments().size() < 1 ? null : Integer.valueOf(String.valueOf(dataType.getArguments().get(0))));

            column.setUnsigned(dataType.isUnsigned());
            column.setPrimaryKey(columnDefinition.isPrimaryKey());
            column.setAutoincrement(columnDefinition.isAutoIncrement());
            column.setNotNull(columnDefinition.containsNotNullConstaint());
            column.setIndexKey(false);

            columnMap.put(column.getName(), column);
        }
        return columnMap;
    }
}
