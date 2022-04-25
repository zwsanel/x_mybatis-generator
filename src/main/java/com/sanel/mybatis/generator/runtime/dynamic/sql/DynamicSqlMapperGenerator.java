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

import com.sanel.mybatis.generator.api.dom.java.*;
import com.sanel.mybatis.generator.codegen.AbstractJavaClientGenerator;
import com.sanel.mybatis.generator.codegen.AbstractXmlGenerator;
import com.sanel.mybatis.generator.config.PropertyRegistry;
import com.sanel.mybatis.generator.internal.util.JavaBeansUtil;
import com.sanel.mybatis.generator.runtime.dynamic.sql.elements.*;

import java.util.ArrayList;
import java.util.List;

import static com.sanel.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static com.sanel.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Jeff Butler
 * 
 */
public class DynamicSqlMapperGenerator extends AbstractJavaClientGenerator {
    // record type for insert, select, update
    private FullyQualifiedJavaType recordType;
    
    // id to use for the common result map
    private String resultMapId;
    
    // name of the field containing the table in the support class
    private String tableFieldName;
    
    private FragmentGenerator fragmentGenerator;

    public DynamicSqlMapperGenerator() {
        super(false);
    }
    
    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        preCalculate();
        
        Interface interfaze = createBasicInterface();

        TopLevelClass supportClass = getSupportClass();
        String staticImportString = supportClass.getType().getFullyQualifiedNameWithoutTypeParameters() + ".*"; //$NON-NLS-1$
        interfaze.addStaticImport(staticImportString);

        addBasicCountMethod(interfaze);
        addBasicDeleteMethod(interfaze);
        addBasicInsertMethod(interfaze);
        addBasicSelectOneMethod(interfaze);
        addBasicSelectManyMethod(interfaze);
        addBasicUpdateMethod(interfaze);
        
        addCountByExampleMethod(interfaze);
        addDeleteByExampleMethod(interfaze);
        addDeleteByPrimaryKeyMethod(interfaze);
        addInsertMethod(interfaze);
        addInsertSelectiveMethod(interfaze);
        addSelectByExampleMethod(interfaze);
        addSelectDistinctByExampleMethod(interfaze);
        addSelectByPrimaryKeyMethod(interfaze);
        addUpdateByExampleMethod(interfaze);
        addUpdateByExampleSelectiveMethod(interfaze);
        addUpdateByPrimaryKeyMethod(interfaze);
        addUpdateByPrimaryKeySelectiveMethod(interfaze);
        
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().clientGenerated(interfaze, null, introspectedTable)) {
            answer.add(interfaze);
            answer.add(supportClass);
        }

        return answer;
    }

    private void preCalculate() {
        recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        resultMapId = recordType.getShortNameWithoutTypeArguments() + "Result"; //$NON-NLS-1$
        tableFieldName =
                JavaBeansUtil.getValidPropertyName(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        fragmentGenerator = new FragmentGenerator.Builder()
                .withIntrospectedTable(introspectedTable)
                .withResultMapId(resultMapId)
                .build();
    }

    private Interface createBasicInterface() {
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(interfaze);
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper")); //$NON-NLS-1$
        interfaze.addAnnotation("@Mapper"); //$NON-NLS-1$

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }
        return interfaze;
    }

    private void addBasicCountMethod(Interface interfaze) {
        BasicCountMethodGenerator generator = new BasicCountMethodGenerator.Builder()
                .withContext(context)
                .withIntrospectedTable(introspectedTable)
                .build();
        
        generate(interfaze, generator);
    }
    
    private void addBasicDeleteMethod(Interface interfaze) {
        BasicDeleteMethodGenerator generator = new BasicDeleteMethodGenerator.Builder()
                .withContext(context)
                .withIntrospectedTable(introspectedTable)
                .build();
        
        generate(interfaze, generator);
    }

    private void addBasicInsertMethod(Interface interfaze) {
        BasicInsertMethodGenerator generator = new BasicInsertMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private void addBasicSelectOneMethod(Interface interfaze) {
        BasicSelectOneMethodGenerator generator = new BasicSelectOneMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withRecordType(recordType)
                .withResultMapId(resultMapId)
                .build();
        
        generate(interfaze, generator);
    }

    private void addBasicSelectManyMethod(Interface interfaze) {
        BasicSelectManyMethodGenerator generator = new BasicSelectManyMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }
    
    private void addBasicUpdateMethod(Interface interfaze) {
        BasicUpdateMethodGenerator generator = new BasicUpdateMethodGenerator.Builder()
                .withContext(context)
                .withIntrospectedTable(introspectedTable)
                .build();
        
        generate(interfaze, generator);
    }

    private void addCountByExampleMethod(Interface interfaze) {
        CountByExampleMethodGenerator generator = new CountByExampleMethodGenerator.Builder()
                .withContext(context)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .build();
        
        generate(interfaze, generator);
    }
    
    private void addDeleteByExampleMethod(Interface interfaze) {
        DeleteByExampleMethodGenerator generator = new DeleteByExampleMethodGenerator.Builder()
                .withContext(context)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .build();
        
        generate(interfaze, generator);
    }

    private void addDeleteByPrimaryKeyMethod(Interface interfaze) {
        DeleteByPrimaryKeyMethodGenerator generator = new DeleteByPrimaryKeyMethodGenerator.Builder()
                .withContext(context)
                .withIntrospectedTable(introspectedTable)
                .withFragmentGenerator(fragmentGenerator)
                .withTableFieldName(tableFieldName)
                .build();
        
        generate(interfaze, generator);
    }

    private void addInsertMethod(Interface interfaze) {
        InsertMethodGenerator generator = new InsertMethodGenerator.Builder()
                .withContext(context)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private void addInsertSelectiveMethod(Interface interfaze) {
        InsertSelectiveMethodGenerator generator = new InsertSelectiveMethodGenerator.Builder()
                .withContext(context)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private void addSelectByExampleMethod(Interface interfaze) {
        SelectByExampleMethodGenerator generator = new SelectByExampleMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private void addSelectDistinctByExampleMethod(Interface interfaze) {
        SelectDistinctByExampleMethodGenerator generator = new SelectDistinctByExampleMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private void addSelectByPrimaryKeyMethod(Interface interfaze) {
        SelectByPrimaryKeyMethodGenerator generator = new SelectByPrimaryKeyMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private void addUpdateByExampleMethod(Interface interfaze) {
        UpdateByExampleMethodGenerator generator = new UpdateByExampleMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private void addUpdateByExampleSelectiveMethod(Interface interfaze) {
        UpdateByExampleSelectiveMethodGenerator generator = new UpdateByExampleSelectiveMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private void addUpdateByPrimaryKeyMethod(Interface interfaze) {
        UpdateByPrimaryKeyMethodGenerator generator = new UpdateByPrimaryKeyMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
        UpdateByPrimaryKeySelectiveMethodGenerator generator = new UpdateByPrimaryKeySelectiveMethodGenerator.Builder()
                .withContext(context)
                .withFragmentGenerator(fragmentGenerator)
                .withIntrospectedTable(introspectedTable)
                .withTableFieldName(tableFieldName)
                .withRecordType(recordType)
                .build();
        
        generate(interfaze, generator);
    }

    private TopLevelClass getSupportClass() {
        return DynamicSqlSupportClassGenerator.of(introspectedTable, context.getCommentGenerator()).generate();
    }

    private void generate(Interface interfaze, AbstractMethodGenerator generator) {
        MethodAndImports mi = generator.generateMethodAndImports();
        if (mi != null && generator.callPlugins(mi.getMethod(), interfaze)) {
            interfaze.addMethod(mi.getMethod());
            interfaze.addImportedTypes(mi.getImports());
            interfaze.addStaticImports(mi.getStaticImports());
        }
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return null;
    }
}
