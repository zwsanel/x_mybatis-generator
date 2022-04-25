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
 * @description 列基础信息校验
 * @date 2022/4/16  上午3:21
 */
public class ColumnRuleValidate implements Validate {

    private final List<PredicateBase<Table>> predicates = new ArrayList<>();

    {
        predicates.add(new PredicateBase<>(columnTextCount(), ValidateFailureMessageEnum.COLUMN_TEXT_COUNT.getMsg()));
        predicates.add(new PredicateBase<>(columnNameRule(), ValidateFailureMessageEnum.COLUMN_NAME.getMsg()));
        predicates.add(new PredicateBase<>(columnCommentRule(), ValidateFailureMessageEnum.COLUMN_COMMENT.getMsg()));
        predicates.add(new PredicateBase<>(columnNotNullRule(), ValidateFailureMessageEnum.COLUMN_NOT_NULL.getMsg()));
        predicates.add(new PredicateBase<>(columnIntTypeRule(), ValidateFailureMessageEnum.COLUMN_INT_TYPE.getMsg()));
        predicates.add(new PredicateBase<>(columnBigintTypeRule(), ValidateFailureMessageEnum.COLUMN_BIGINT_TYPE.getMsg()));
        predicates.add(new PredicateBase<>(columnVarcharTypeRule(), ValidateFailureMessageEnum.COLUMN_VARCHAR_TYPE.getMsg()));
        predicates.add(new PredicateBase<>(columnPrimaryKeyRule(), ValidateFailureMessageEnum.COLUMN_PRIMARY_KEY.getMsg()));
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

    private Predicate<Table> columnTextCount() {
        return l -> l.getColumnMap().values().stream().filter(o -> "text".equals(o.getType())).count() > 3;
    }


    private Predicate<Table> columnNameRule() {
        return l -> l.getColumnMap().values().stream().anyMatch(c -> !RegexUtil.MYSQL_NAME_PATTERN.matcher(c.getName()).matches()
                || c.getName().length() > 32);
    }

    private Predicate<Table> columnCommentRule() {
        return l -> l.getColumnMap().values().stream().anyMatch(c -> Objects.isNull(c.getComment()));
    }

    private Predicate<Table> columnNotNullRule() {
        return l -> l.getColumnMap().values().stream().anyMatch(c ->
                c.isNotNull() && (!c.isAutoincrement() && ("NULL".equalsIgnoreCase(c.getDefaultValue()) || Objects.isNull(c.getDefaultValue())))
        );
    }

    private Predicate<Table> columnIntTypeRule() {
        return l -> l.getColumnMap().values().stream().anyMatch(c -> "int".equals(c.getType()) && (!c.isUnsigned() || c.getLength() > 10));
    }

    private Predicate<Table> columnBigintTypeRule() {
        return l -> l.getColumnMap().values().stream().anyMatch(c -> "bigint".equals(c.getType()) && c.getLength() < 10);
    }

    private Predicate<Table> columnVarcharTypeRule() {
        return l -> l.getColumnMap().values().stream().anyMatch(c -> "varchar".equals(c.getType()) && c.getLength() > 3000);
    }

    private Predicate<Table> columnPrimaryKeyRule() {
        return t -> {
            Column primaryKey = t.getColumnMap().get("id");
            return Objects.nonNull(primaryKey) && !"bigint".equals(primaryKey.getType()) && !primaryKey.isAutoincrement();
        };
    }
}
