package com.sanel.mybatis.plugin.generator;

import com.mysql.jdbc.StringUtils;
import com.sanel.mybatis.generator.api.IntrospectedColumn;
import com.sanel.mybatis.generator.api.IntrospectedTable;
import com.sanel.mybatis.generator.api.dom.java.Field;
import com.sanel.mybatis.generator.api.dom.java.TopLevelClass;
import com.sanel.mybatis.generator.internal.DefaultCommentGenerator;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author suzhiwen
 * @description 自定义类注释
 * @date 2022/3/10  下午4:15
 */
public class CustomizeCommentGenerator extends DefaultCommentGenerator {
    // properties = new Properties();
    // private Properties properties;
    private final Properties systemPro;

    public CustomizeCommentGenerator() {
        super();
        systemPro = System.getProperties();
    }

    /**
     * 对类的注解
     *
     * @param topLevelClass
     * @param introspectedTable
     */
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addJavaDocLine("/**");
        // topLevelClass.addJavaDocLine(" * 这是MyBatis Generator自动生成的Model Class.");

        StringBuilder sb = new StringBuilder();
        sb.append(" * 对应的数据表 : ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        topLevelClass.addJavaDocLine(sb.toString());

        String tableRemarks = introspectedTable.getRemarks();
        if (StringUtils.isNullOrEmpty(tableRemarks)) {
            throw new RuntimeException("请添加表注释");
        }

        sb.setLength(0);
        sb.append(" * 数据表注释 : ");
        sb.append(tableRemarks.replace("'",""));
        topLevelClass.addJavaDocLine(sb.toString());
        sb.setLength(0);
        sb.append(" * @author ");
        sb.append(systemPro.getProperty("user.name"));
        topLevelClass.addJavaDocLine(sb.toString());

        String curDateString = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        sb.setLength(0);
        sb.append(" * @date ");
        sb.append(curDateString);
        topLevelClass.addJavaDocLine(sb.toString());

        topLevelClass.addJavaDocLine(" */");
    }

    /**
     * 生成的实体增加字段的中文注释
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        if (StringUtils.isNullOrEmpty(introspectedColumn.getRemarks())) {
            throw new RuntimeException("请添加字段注释");
        }
        sb.append(introspectedColumn.getRemarks());
        field.addJavaDocLine(sb.toString().replace("\n", " "));
        field.addJavaDocLine(" */");
    }

}