package com.sanel.mybatis.sql.validate;

import com.sanel.mybatis.sql.validate.domain.PredicateBase;
import com.sanel.mybatis.sql.validate.domain.Result;
import com.sanel.mybatis.sql.validate.domain.Table;
import com.sanel.mybatis.sql.validate.enums.ValidateFailureMessageEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author suzhiwen
 * @description 索引校验
 * @date 2022/4/16  上午3:07
 */
public class IndexRuleValidate implements Validate {

    private final List<PredicateBase<Table>> predicates = new ArrayList<>();

    {
        predicates.add(new PredicateBase<>(indexCountRule(), ValidateFailureMessageEnum.TABLE_NAME.getMsg()));
        predicates.add(new PredicateBase<>(indexNotNullRule(), ValidateFailureMessageEnum.INDEX_NOT_NULL.getMsg()));
    }

    @Override
    public Result doValidate(Table table) {
        for (PredicateBase<Table> p : predicates) {
            if (p.getPredicate().test(table)) {
                return Result.fail(p.getMsg());
            }
        }
        return Result.pass();
    }

    public Predicate<Table> indexCountRule() {
        return l -> l.getIndexList().size() > 5;
    }

    public Predicate<Table> indexNotNullRule() {
        return l -> l.getIndexList().stream().anyMatch(o -> o.getColumnList().stream().noneMatch(x -> x.isNotNull() || x.isAutoincrement()));
    }
}
