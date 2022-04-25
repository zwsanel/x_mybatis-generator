package com.sanel.mybatis;


import com.sanel.mybatis.generator.api.MyBatisGenerator;
import com.sanel.mybatis.generator.api.ProgressCallback;
import com.sanel.mybatis.generator.config.Configuration;
import com.sanel.mybatis.generator.config.xml.ConfigurationParser;
import com.sanel.mybatis.generator.exception.InvalidConfigurationException;
import com.sanel.mybatis.generator.exception.XMLParserException;
import com.sanel.mybatis.generator.internal.DefaultShellCallback;
import com.sanel.mybatis.generator.internal.NullProgressCallback;
import com.sanel.mybatis.sql.validate.util.SQLValidate;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class GeneratorApplication {
    public static void main(String[] args) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        String path = Objects.requireNonNull(
                GeneratorApplication.class.getClassLoader().getResource("generationConfig.xml")
        ).getPath();

        List<String> warnings = new ArrayList<String>();
        File configFile = new File(path);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        // myBatisGenerator.generate(new NullProgressCallback());
        // 调用DDL规则校验
        myBatisGenerator.generate(getDDLValidateCallback());
    }

    private static ProgressCallback getDDLValidateCallback() {
        return new ProgressCallback() {
            @Override
            public void ddlRulesValidate(Connection connection, String tableName) throws SQLException {
                String createTableSql = SQLValidate.getCreateTableSql(connection, tableName);
                SQLValidate.doValidate(createTableSql);
            }


            @Override
            public void introspectionStarted(int totalTasks) {

            }

            @Override
            public void generationStarted(int totalTasks) {

            }

            @Override
            public void saveStarted(int totalTasks) {

            }

            @Override
            public void startTask(String taskName) {

            }

            @Override
            public void done() {

            }

            @Override
            public void checkCancel() throws InterruptedException {

            }
        };
    }
}
