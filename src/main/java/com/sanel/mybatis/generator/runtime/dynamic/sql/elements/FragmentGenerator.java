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
package com.sanel.mybatis.generator.runtime.dynamic.sql.elements;

import com.sanel.mybatis.generator.api.IntrospectedColumn;
import com.sanel.mybatis.generator.api.IntrospectedTable;
import com.sanel.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import com.sanel.mybatis.generator.api.dom.java.Parameter;
import com.sanel.mybatis.generator.codegen.mybatis3.ListUtilities;
import com.sanel.mybatis.generator.config.GeneratedKey;
import com.sanel.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.*;

import static com.sanel.mybatis.generator.api.dom.OutputUtilities.javaIndent;
import static com.sanel.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class FragmentGenerator {

    private IntrospectedTable introspectedTable;
    private String resultMapId;
    
    private FragmentGenerator(Builder builder) {
        this.introspectedTable = builder.introspectedTable;
        this.resultMapId = builder.resultMapId;
    }
    
    public String getSelectList() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            if (first) {
                first = false;
            } else {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(column.getJavaProperty());
        }
        
        return sb.toString();
    }
    
    public MethodParts getPrimaryKeyWhereClauseAndParameters() {
        MethodParts.Builder builder = new MethodParts.Builder();
        
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            builder.withImport(column.getFullyQualifiedJavaType());
            builder.withParameter(new Parameter(column.getFullyQualifiedJavaType(), column.getJavaProperty() + "_")); //$NON-NLS-1$
            if (first) {
                builder.withBodyLine("        .where(" + column.getJavaProperty() //$NON-NLS-1$
                + ", isEqualTo(" + column.getJavaProperty() //$NON-NLS-1$
                + "_))"); //$NON-NLS-1$
                first = false;
            } else {
                builder.withBodyLine("        .and(" + column.getJavaProperty() //$NON-NLS-1$
                + ", isEqualTo(" + column.getJavaProperty() //$NON-NLS-1$
                + "_))"); //$NON-NLS-1$
            }
        }
        
        return builder.build();
    }

    public List<String> getPrimaryKeyWhereClauseForUpdate() {
        List<String> lines = new ArrayList<String>();
        
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            String methodName = JavaBeansUtil.getGetterMethodName(column.getJavaProperty(), column.getFullyQualifiedJavaType());
            if (first) {
                lines.add("        .where(" + column.getJavaProperty() //$NON-NLS-1$
                + ", isEqualTo(record::" + methodName //$NON-NLS-1$
                + "))"); //$NON-NLS-1$
                first = false;
            } else {
                lines.add("        .and(" + column.getJavaProperty() //$NON-NLS-1$
                + ", isEqualTo(record::" + methodName //$NON-NLS-1$
                + "))"); //$NON-NLS-1$
            }
        }
        
        return lines;
    }
    
    public MethodParts getAnnotatedConstructorArgs() {
        MethodParts.Builder builder = new MethodParts.Builder();

        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType")); //$NON-NLS-1$
        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs")); //$NON-NLS-1$
        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg")); //$NON-NLS-1$

        builder.withAnnotation("@ConstructorArgs({"); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();

        Set<FullyQualifiedJavaType> imports = new HashSet<FullyQualifiedJavaType>();
        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
        while (iterPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getArgAnnotation(imports, introspectedColumn, true));
            
            if (iterPk.hasNext() || iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        while (iterNonPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterNonPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getArgAnnotation(imports, introspectedColumn, false));
            
            if (iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        builder.withAnnotation("})") //$NON-NLS-1$
            .withImports(imports);
        
        return builder.build();
    }

    private String getArgAnnotation(Set<FullyQualifiedJavaType> imports, IntrospectedColumn introspectedColumn, boolean idColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("@Arg(column=\""); //$NON-NLS-1$
        sb.append(introspectedColumn.getActualColumnName());
        
        imports.add(introspectedColumn.getFullyQualifiedJavaType());
        sb.append("\", javaType="); //$NON-NLS-1$
        sb.append(introspectedColumn.getFullyQualifiedJavaType().getShortName());
        sb.append(".class"); //$NON-NLS-1$

        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            FullyQualifiedJavaType fqjt =
                    new FullyQualifiedJavaType(introspectedColumn.getTypeHandler());
            imports.add(fqjt);
            sb.append(", typeHandler="); //$NON-NLS-1$
            sb.append(fqjt.getShortName());
            sb.append(".class"); //$NON-NLS-1$
        }

        sb.append(", jdbcType=JdbcType."); //$NON-NLS-1$
        sb.append(introspectedColumn.getJdbcTypeName());
        if (idColumn) {
            sb.append(", id=true"); //$NON-NLS-1$
        }
        sb.append(')');

        return sb.toString();
    }

    public MethodParts getAnnotatedResults() {
        MethodParts.Builder builder = new MethodParts.Builder();

        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType")); //$NON-NLS-1$
        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result")); //$NON-NLS-1$
        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results")); //$NON-NLS-1$

        builder.withAnnotation("@Results(id=\"" + resultMapId + "\", value = {"); //$NON-NLS-1$ //$NON-NLS-2$

        StringBuilder sb = new StringBuilder();

        Set<FullyQualifiedJavaType> imports = new HashSet<FullyQualifiedJavaType>();
        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
        while (iterPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(imports, introspectedColumn, true));
            
            if (iterPk.hasNext() || iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        while (iterNonPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterNonPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(imports, introspectedColumn, false));
            
            if (iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        builder.withAnnotation("})") //$NON-NLS-1$
            .withImports(imports);
        
        return builder.build();
    }
    
    private String getResultAnnotation(Set<FullyQualifiedJavaType> imports, IntrospectedColumn introspectedColumn, boolean idColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("@Result(column=\""); //$NON-NLS-1$
        sb.append(introspectedColumn.getActualColumnName());
        sb.append("\", property=\""); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty());
        sb.append('\"');

        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            FullyQualifiedJavaType fqjt =
                    new FullyQualifiedJavaType(introspectedColumn.getTypeHandler());
            imports.add(fqjt);
            sb.append(", typeHandler="); //$NON-NLS-1$
            sb.append(fqjt.getShortName());
            sb.append(".class"); //$NON-NLS-1$
        }

        sb.append(", jdbcType=JdbcType."); //$NON-NLS-1$
        sb.append(introspectedColumn.getJdbcTypeName());
        if (idColumn) {
            sb.append(", id=true"); //$NON-NLS-1$
        }
        sb.append(')');

        return sb.toString();
    }
    
    public MethodParts getGeneratedKeyAnnotation(GeneratedKey gk) {
        MethodParts.Builder builder = new MethodParts.Builder();
        
        StringBuilder sb = new StringBuilder();
        IntrospectedColumn introspectedColumn = introspectedTable.getColumn(gk.getColumn());
        if (introspectedColumn != null) {
            if (gk.isJdbcStandard()) {
                builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Options")); //$NON-NLS-1$
                sb.append("@Options(useGeneratedKeys=true,keyProperty=\"record."); //$NON-NLS-1$
                sb.append(introspectedColumn.getJavaProperty());
                sb.append("\")"); //$NON-NLS-1$
                builder.withAnnotation(sb.toString());
            } else {
                builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectKey")); //$NON-NLS-1$
                FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
                sb.append("@SelectKey(statement=\""); //$NON-NLS-1$
                sb.append(gk.getRuntimeSqlStatement());
                sb.append("\", keyProperty=\"record."); //$NON-NLS-1$
                sb.append(introspectedColumn.getJavaProperty());
                sb.append("\", before="); //$NON-NLS-1$
                sb.append(gk.isIdentity() ? "false" : "true"); //$NON-NLS-1$ //$NON-NLS-2$
                sb.append(", resultType="); //$NON-NLS-1$
                sb.append(fqjt.getShortName());
                sb.append(".class)"); //$NON-NLS-1$
                builder.withAnnotation(sb.toString());
            }
        }
        
        return builder.build();
    }
    
    public List<String> getSetEqualLines(List<IntrospectedColumn> columnList, boolean terminate) {
        List<String> lines = new ArrayList<String>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(columnList);
        Iterator<IntrospectedColumn> iter = columns.iterator();
        while (iter.hasNext()) {
            IntrospectedColumn column = iter.next();
            String methodName = JavaBeansUtil.getGetterMethodName(column.getJavaProperty(), column.getFullyQualifiedJavaType());
            String line = "        .set(" + column.getJavaProperty() //$NON-NLS-1$
            + ").equalTo(record::" + methodName //$NON-NLS-1$
            + ")"; //$NON-NLS-1$
            if (terminate && !iter.hasNext()) {
                line += ";"; //$NON-NLS-1$
            }
            lines.add(line);
        }
        
        return lines;
    }
    
    public List<String> getSetEqualWhenPresentLines(List<IntrospectedColumn> columnList, boolean terminate) {
        List<String> lines = new ArrayList<String>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(columnList);
        Iterator<IntrospectedColumn> iter = columns.iterator();
        while (iter.hasNext()) {
            IntrospectedColumn column = iter.next();
            String methodName = JavaBeansUtil.getGetterMethodName(column.getJavaProperty(), column.getFullyQualifiedJavaType());
            String line = "        .set(" + column.getJavaProperty() //$NON-NLS-1$
            + ").equalToWhenPresent(record::" //$NON-NLS-1$
            + methodName + ")"; //$NON-NLS-1$
            if (terminate && !iter.hasNext()) {
                line += ";"; //$NON-NLS-1$
            }
            lines.add(line);
        }
        return lines;
    }

    public static class Builder {
        private IntrospectedTable introspectedTable;
        private String resultMapId;
        
        public Builder withIntrospectedTable(IntrospectedTable introspectedTable) {
            this.introspectedTable = introspectedTable;
            return this;
        }
        
        public Builder withResultMapId(String resultMapId) {
            this.resultMapId = resultMapId;
            return this;
        }
        
        public FragmentGenerator build() {
            return new FragmentGenerator(this);
        }
    }
}
