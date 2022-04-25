package com.sanel.mybatis.sql.validate;

import com.sanel.mybatis.sql.validate.domain.Column;
import com.sanel.mybatis.sql.validate.domain.PredicateBase;
import com.sanel.mybatis.sql.validate.domain.Result;
import com.sanel.mybatis.sql.validate.domain.Table;
import com.sanel.mybatis.sql.validate.enums.ValidateFailureMessageEnum;
import com.sanel.mybatis.sql.validate.util.RegexUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author suzhiwen
 * @description 列额外字段校验
 * @date 2022/4/16  上午3:21
 */
public class ColumnExtendsRuleValidate implements Validate {

    private final List<PredicateBase<Table>> predicates = new ArrayList<>();

    {
        predicates.add(new PredicateBase<>(columnCreatedByAndUpdatedByFliedRule(), ValidateFailureMessageEnum.COLUMN_REMARK_USER_FIELD.getMsg()));
        predicates.add(new PredicateBase<>(columnDateCreatedAndDateUpdatedFliedRule(), ValidateFailureMessageEnum.COLUMN_REMARK_TIME_FIELD.getMsg()));

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

    private Predicate<Table> columnCreatedByAndUpdatedByFliedRule() {
        return l -> {
            Column createdBy = l.getColumnMap().get("updater");
            Column updatedBy = l.getColumnMap().get("creater");
            return Objects.isNull(createdBy) || Objects.isNull(updatedBy) || !createdBy.isNotNull() || !updatedBy.isNotNull();
        };
    }

    private Predicate<Table> columnDateCreatedAndDateUpdatedFliedRule() {
        return l -> {
            Column dateCreated = l.getColumnMap().get("create_time");
            Column dateUpdated = l.getColumnMap().get("update_time");
            return (Objects.isNull(dateCreated) || !"timestamp".equalsIgnoreCase(dateCreated.getType()) || !dateCreated.isNotNull() || !"CURRENT_TIMESTAMP".equalsIgnoreCase(dateCreated.getDefaultValue()))
                    || (Objects.isNull(dateUpdated) || !"timestamp".equalsIgnoreCase(dateUpdated.getType()) || !dateUpdated.isNotNull() || !"CURRENT_TIMESTAMP".equalsIgnoreCase(dateUpdated.getDefaultValue()));
        };
    }
}
