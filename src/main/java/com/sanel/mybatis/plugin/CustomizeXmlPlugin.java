package com.sanel.mybatis.plugin;



import com.sanel.mybatis.generator.api.IntrospectedTable;
import com.sanel.mybatis.generator.api.PluginAdapter;
import com.sanel.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.Properties;
/**
 * @author suzhiwen
 * @description 不生成不常用的方法
 * @date 2022/3/10  下午4:11
 */
public class CustomizeXmlPlugin extends PluginAdapter {

    private final Properties systemPro;

    public CustomizeXmlPlugin() {
        super();
        systemPro = System.getProperties();
    }


    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element,
                                                IntrospectedTable introspectedTable) {
        return false;
    }


    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }


    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}