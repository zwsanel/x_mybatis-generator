package com.sanel.mybatis.plugin;

import com.sanel.mybatis.generator.api.IntrospectedTable;
import com.sanel.mybatis.generator.api.PluginAdapter;
import com.sanel.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Properties;


/**
 * @author suzhiwen
 * @description 自定义toString方法
 * @date 2022/3/10  下午4:11
 */
public class CustomizeToStringPlugin extends PluginAdapter {
    private boolean useToStringFromRoot;
    private String toStringType;

    private final String JSON_TYPE = "json";

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        useToStringFromRoot = Boolean.getBoolean(properties.getProperty("useToStringFromRoot")); //$NON-NLS-1$
        toStringType = properties.getProperty("toStringType"); //$NON-NLS-1$
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
                                                      IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private void generateToString(IntrospectedTable introspectedTable,
                                  TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("toString"); //$NON-NLS-1$
        if (introspectedTable.isJava5Targeted()) {
            method.addAnnotation("@Override"); //$NON-NLS-1$
        }

        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        if (JSON_TYPE.equals(toStringType)) {
            // toString改写使用alibaba fastjson
            topLevelClass.addImportedType("com.alibaba.fastjson.JSON");
            method.addBodyLine("return JSON.toJSONString(this);");
        } else {
            method.addBodyLine("StringBuilder sb = new StringBuilder();"); //$NON-NLS-1$
            method.addBodyLine("sb.append(getClass().getSimpleName());"); //$NON-NLS-1$
            method.addBodyLine("sb.append(\" [\");"); //$NON-NLS-1$
            method.addBodyLine("sb.append(\"Hash = \").append(hashCode());"); //$NON-NLS-1$
            StringBuilder sb = new StringBuilder();
            for (Field field : topLevelClass.getFields()) {
                String property = field.getName();
                sb.setLength(0);
                sb.append("sb.append(\"").append(", ").append(property) //$NON-NLS-1$ //$NON-NLS-2$
                        .append("=\")").append(".append(").append(property) //$NON-NLS-1$ //$NON-NLS-2$
                        .append(");"); //$NON-NLS-1$
                method.addBodyLine(sb.toString());
            }

            method.addBodyLine("sb.append(\"]\");"); //$NON-NLS-1$
            if (useToStringFromRoot && topLevelClass.getSuperClass() != null) {
                method.addBodyLine("sb.append(\", from super class \");"); //$NON-NLS-1$
                method.addBodyLine("sb.append(super.toString());"); //$NON-NLS-1$
            }
            method.addBodyLine("return sb.toString();"); //$NON-NLS-1$
        }
        topLevelClass.addMethod(method);
    }
}
