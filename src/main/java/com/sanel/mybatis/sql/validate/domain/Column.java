package com.sanel.mybatis.sql.validate.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: suzhiwen
 * @description: 列信息
 * @date: 2022/2/16
 */
@Data
@Accessors(chain = true)
public class Column {
    // 是否主键
    private boolean isPrimaryKey;
    // 是否索引键
    private boolean isIndexKey;
    // 列名
    private String name;
    // 类型
    private String type;
    // 长度
    private Integer length;
    // 是否有符号  仅判断数值类型
    private boolean isUnsigned;
    // 是否自增
    private boolean isAutoincrement;
    // 不为空
    private boolean isNotNull;
    // 默认值
    private String defaultValue;
    // 注释
    private String comment;
}
