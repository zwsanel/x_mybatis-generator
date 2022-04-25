package com.sanel.mybatis.sql.validate.domain;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author: suzhiwen
 * @description: 表信息
 * @date: 2022/1/11
 */
@Data
@Accessors(chain = true)
public class Table {
    /**
     * 表名 小写 长度不超过32
     */
    private String tableName;

    /**
     * 引擎 INNODB
     */
    private String engine;

    /**
     * 编码 utf-8/utf8mb4
     */
    private String charset;

    /**
     * 备注 自动增长ID
     */
    private Integer autoIncrement;

    /**
     * 备注 长度50
     */
    private String comment;

    private Map<String, Column> columnMap;

    private List<Index> indexList;
}
