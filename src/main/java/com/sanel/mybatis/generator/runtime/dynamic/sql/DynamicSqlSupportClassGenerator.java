/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sanel.mybatis.generator.runtime.dynamic.sql;

import com.sanel.mybatis.generator.api.CommentGenerator;
import com.sanel.mybatis.generator.api.IntrospectedColumn;
import com.sanel.mybatis.generator.api.IntrospectedTable;
import com.sanel.mybatis.generator.api.dom.java.*;
import com.sanel.mybatis.generator.internal.util.JavaBeansUtil;
import com.sanel.mybatis.generator.internal.util.StringUtility;

import java.util.List;

import static com.sanel.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.getEscapedColumnName;
import static com.sanel.mybatis.generator.internal.util.StringUtility.escapeStringForJava;

public class DynamicSqlSupportClassGenerator {
    private IntrospectedTable introspectedTable;
    private CommentGenerator commentGenerator;
    
    private DynamicSqlSupportClassGenerator() {
        super();
    }
    
    public TopLevelClass generate() {
        TopLevelClass topLevelClass = buildBasicClass();
        Field tableField = calculateTableDefinition(topLevelClass);
        topLevelClass.addImportedType(tableField.getType());
        topLevelClass.addField(tableField);

        InnerClass innerClass = buildInnerTableClass(topLevelClass);
        topLevelClass.addInnerClass(innerClass);

        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (IntrospectedColumn column : columns) {
            handleColumn(topLevelClass, innerClass, column, tableField.getName());
        }

        return topLevelClass;
    }

    private String calculateClassName() {
        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        
        return mapperType.getPackageName() + "." + recordType.getShortNameWithoutTypeArguments() + "DynamicSqlSupport"; //$NON-NLS-1$ //$NON-NLS-2$
        
    }
    private TopLevelClass buildBasicClass() {
        TopLevelClass topLevelClass = new TopLevelClass(calculateClassName());
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.setFinal(true);
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlColumn")); //$NON-NLS-1$
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlTable")); //$NON-NLS-1$
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.sql.JDBCType")); //$NON-NLS-1$
        return topLevelClass;
    }
    
    private InnerClass buildInnerTableClass(TopLevelClass topLevelClass) {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        InnerClass innerClass = new InnerClass(fqjt.getShortName());
        innerClass.setVisibility(JavaVisibility.PUBLIC);
        innerClass.setStatic(true);
        innerClass.setFinal(true);
        innerClass.setSuperClass(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlTable")); //$NON-NLS-1$
        
        Method method = new Method(fqjt.getShortName());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.addBodyLine("super(\"" //$NON-NLS-1$
                + escapeStringForJava(introspectedTable.getFullyQualifiedTableNameAtRuntime())
                + "\");"); //$NON-NLS-1$
        innerClass.addMethod(method);
        
        commentGenerator.addClassAnnotation(innerClass, introspectedTable, topLevelClass.getImportedTypes());
        
        return innerClass;
    }

    private Field calculateTableDefinition(TopLevelClass topLevelClass) {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        String fieldName =
                JavaBeansUtil.getValidPropertyName(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        Field field = new Field(fieldName, fqjt);
        commentGenerator.addFieldAnnotation(field, introspectedTable, topLevelClass.getImportedTypes());
        field.setVisibility(JavaVisibility.PUBLIC);
        field.setStatic(true);
        field.setFinal(true);
        
        StringBuilder initializationString = new StringBuilder();
        initializationString.append(String.format("new %s()", //$NON-NLS-1$
                escapeStringForJava(introspectedTable.getFullyQualifiedTable().getDomainObjectName())));
        field.setInitializationString(initializationString.toString());
        return field;
    }
    
    private void handleColumn(TopLevelClass topLevelClass, InnerClass innerClass, IntrospectedColumn column, String tableFieldName) {
        topLevelClass.addImportedType(column.getFullyQualifiedJavaType());
        FullyQualifiedJavaType fieldType = calculateFieldType(column);
        String fieldName = column.getJavaProperty();
        
        // tlc field
        Field field = new Field(fieldName, fieldType);
        field.setVisibility(JavaVisibility.PUBLIC);
        field.setStatic(true);
        field.setFinal(true);
        field.setInitializationString(tableFieldName + "." + fieldName); //$NON-NLS-1$
        commentGenerator.addFieldAnnotation(field, introspectedTable, column, topLevelClass.getImportedTypes());
        topLevelClass.addField(field);
        
        // inner class field
        field = new Field(fieldName, fieldType);
        field.setVisibility(JavaVisibility.PUBLIC);
        field.setFinal(true);
        field.setInitializationString(calculateInnerInitializationString(column));
        innerClass.addField(field);
    }

    private FullyQualifiedJavaType calculateFieldType(IntrospectedColumn column) {
        FullyQualifiedJavaType typeParameter;
        if (column.getFullyQualifiedJavaType().isPrimitive()) {
            typeParameter = column.getFullyQualifiedJavaType().getPrimitiveTypeWrapper();
        } else {
            typeParameter = column.getFullyQualifiedJavaType();
        }
        return new FullyQualifiedJavaType(String.format("SqlColumn<%s>", typeParameter.getShortName())); //$NON-NLS-1$
    }

    private String calculateInnerInitializationString(IntrospectedColumn column) {
        StringBuilder initializationString = new StringBuilder();
        
        initializationString.append(String.format("column(\"%s\", JDBCType.%s", //$NON-NLS-1$ //$NON-NLS-2$
                escapeStringForJava(getEscapedColumnName(column)),
            column.getJdbcTypeName()));
        
        if (StringUtility.stringHasValue(column.getTypeHandler())) {
            initializationString.append(String.format(", \"%s\")", column.getTypeHandler())); //$NON-NLS-1$
        } else {
            initializationString.append(')');
        }
        
        return initializationString.toString();
    }
    
    public static DynamicSqlSupportClassGenerator of(IntrospectedTable introspectedTable, CommentGenerator commentGenerator) {
        DynamicSqlSupportClassGenerator generator = new DynamicSqlSupportClassGenerator();
        generator.introspectedTable = introspectedTable;
        generator.commentGenerator = commentGenerator;
        return generator;
    }
}
