package com.sanel.mybatis.sql.validate.util;

import java.util.regex.Pattern;

/**
 * @author: suzhiwen
 * @description: 正则
 * @date: 2022/2/18
 */
public class RegexUtil {

    // 表名列名规则
    public static Pattern MYSQL_NAME_PATTERN = Pattern.compile("^[a-z]+[_a-z0-9]*[a-z0-9]+");

    public static void main(String[] args) {
        System.out.println(MYSQL_NAME_PATTERN.matcher("ts_dictionary1").matches());
    }
}
