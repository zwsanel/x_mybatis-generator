package com.sanel.mybatis.plugin;

import com.sanel.mybatis.generator.api.IntrospectedColumn;
import com.sanel.mybatis.generator.api.IntrospectedTable;
import com.sanel.mybatis.generator.api.PluginAdapter;
import com.sanel.mybatis.generator.api.dom.java.*;


import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * @author suzhiwen
 * @description 自定义实体类信息 插件
 * @date 2022/3/10  下午4:14
 */
public class CustomizeEntityPlugin extends PluginAdapter {

    private final FullyQualifiedJavaType baseEntity;

    public CustomizeEntityPlugin() {
        super();
        // entity集成的父类
        baseEntity = new FullyQualifiedJavaType("com.sanel.mybatis.sql.validate.entity.BaseEntity"); //$NON-NLS-1$
    }

    @Override
    public boolean validate(List<String> warnings) {
        // this plugin is always valid
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    protected void makeSerializable(TopLevelClass topLevelClass,
                                    IntrospectedTable introspectedTable) {

        // 继承类
        topLevelClass.addImportedType(baseEntity);
        topLevelClass.setSuperClass(baseEntity);

        // 声明注解
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("lombok.experimental.Accessors");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@Accessors(chain = true)");

        // serialVersionUID字段生成
        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString(Math.abs(new Random().nextLong()) + "L"); //$NON-NLS-1$
        field.setName("serialVersionUID"); //$NON-NLS-1$
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long")); //$NON-NLS-1$
        field.setVisibility(JavaVisibility.PRIVATE);
        context.getCommentGenerator().addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
    }


    // 不生成getter setter方法
    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }
}
