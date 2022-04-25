package com.sanel.mybatis.sql.validate.enums;

/**
 * @author suzhiwen
 * @description 校验失败注解
 * @date 2022/4/16  上午2:26
 */
public enum ValidateFailureMessageEnum {
    TABLE_NAME("表名必须为小写,不包含特殊字符, 长度不能超过32位"),
    TABLE_CHARSET("表字符集必须为utf8/utf8mb4"),
    TABLE_AUTO_INCREMENT("表自增键必须是整数开始"),
    TABLE_COMMENT("建表语句必须有注释"),
    TABLE_ENGINE("表引擎必须为INNODB"),
    INDEX_COUNT("索引超过5个, 请检查表结构。"),
    INDEX_NOT_NULL("索引字段必须要有默认值"),
    COLUMN_TEXT_COUNT("Text字段不能超过3个"),
    COLUMN_NAME("列名必须为小写,不包含特殊字符, 长度不能超过32位"),
    COLUMN_COMMENT("列必须要有注释"),
    COLUMN_NOT_NULL("非空列必须要有默认值或自增键"),
    COLUMN_INT_TYPE("int类型必须为unsigned，且长度不能超过10"),
    COLUMN_BIGINT_TYPE("bit类型长度不能小于11"),
    COLUMN_VARCHAR_TYPE("varchar类型长度不能大于3000"),
    COLUMN_REMARK_USER_FIELD("建表语句必须包含非空的created_by,updated_by字段"),
    COLUMN_REMARK_TIME_FIELD("缺少date_created/date_updated字段,timestamp非空,默认CURRENT_TIMESTAMP"),
    COLUMN_PRIMARY_KEY("必须有名称为id,类型为bigint的自增主键"),
    ;


    private String msg;

    ValidateFailureMessageEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
