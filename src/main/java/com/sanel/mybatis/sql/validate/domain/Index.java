package com.sanel.mybatis.sql.validate.domain;

/**
 * @author: suzhiwen
 * @description: 索引信息
 * @date: 2022/2/16
 */

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: suzhiwen
 * @description: 索引信息
 * @date: 2022/2/16
 */
@Data
@Accessors(chain = true)
public class Index {

    // 索引名
    private String name;
    // 索引类型
    private String indexType;
    // 索引列
    private List<Column> columnList;

    public String getName() {
        return name;
    }

    public Index setName(String name) {
        this.name = name;
        return this;
    }
}
