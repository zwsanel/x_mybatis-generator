package com.sanel.mybatis.plugin;


import com.sanel.mybatis.generator.api.IntrospectedTable;
import com.sanel.mybatis.generator.api.PluginAdapter;
import com.sanel.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import com.sanel.mybatis.generator.api.dom.java.Interface;
import com.sanel.mybatis.generator.api.dom.java.Method;
import com.sanel.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Properties;

/**
 * @author suzhiwen
 * @description 自定义mapper 插件
 * @date 2022/3/10  下午4:14
 */
public class CustomizeMapperPlugin extends PluginAdapter {

    private final Properties systemPro;

    public CustomizeMapperPlugin() {
        super();
        systemPro = System.getProperties();
    }


    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {

        // Mapper注解引入
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        interfaze.addAnnotation("@Mapper");
        return true;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze,
                                               IntrospectedTable introspectedTable) {
        // 不生成insert方法
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method,
                                                                    Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(
            Method method, Interface interfaze,
            IntrospectedTable introspectedTable) {
        return false;
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}