package com.sanel.mybatis.sql.validate.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.function.Predicate;

/**
 * @author suzhiwen
 * @description 校验基类
 * @date 2022/4/16  上午2:43
 */
@Data
@Accessors(chain = true)
public class PredicateBase<T> {

    private Predicate<T> predicate;

    private String msg;

    public PredicateBase() {
    }

    public PredicateBase(Predicate<T> predicate, String msg) {
        this.predicate = predicate;
        this.msg = msg;
    }
}
