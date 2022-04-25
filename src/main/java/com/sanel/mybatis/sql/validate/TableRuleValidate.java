package com.sanel.mybatis.sql.validate;

import com.alibaba.druid.util.StringUtils;
import com.sanel.mybatis.sql.validate.domain.PredicateBase;
import com.sanel.mybatis.sql.validate.domain.Result;
import com.sanel.mybatis.sql.validate.domain.Table;
import com.sanel.mybatis.sql.validate.enums.ValidateFailureMessageEnum;
import com.sanel.mybatis.sql.validate.util.RegexUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author suzhiwen
 * @description 表校验
 * @date 2022/4/16  上午2:08
 */
public class TableRuleValidate implements Validate {

    private final List<PredicateBase<Table>> predicates = new ArrayList<>();

    {
        predicates.add(new PredicateBase<>(tableNameRule(), ValidateFailureMessageEnum.TABLE_NAME.getMsg()));
        predicates.add(new PredicateBase<>(tableChartSetRule(), ValidateFailureMessageEnum.TABLE_CHARSET.getMsg()));
        predicates.add(new PredicateBase<>(tableAutoIncrementRule(), ValidateFailureMessageEnum.TABLE_AUTO_INCREMENT.getMsg()));
        predicates.add(new PredicateBase<>(tableCommentRule(), ValidateFailureMessageEnum.TABLE_COMMENT.getMsg()));
        predicates.add(new PredicateBase<>(tableEngineRule(), ValidateFailureMessageEnum.TABLE_ENGINE.getMsg()));
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

    public Predicate<Table> tableNameRule() {
        return l -> RegexUtil.MYSQL_NAME_PATTERN.matcher(l.getTableName()).matches() && l.getTableName().length() > 32;
    }

    public Predicate<Table> tableChartSetRule() {
        return l -> !StringUtils.isEmpty(l.getCharset()) && !Arrays.asList("utf8", "utf8mb4").contains(l.getCharset());
    }

    public Predicate<Table> tableAutoIncrementRule() {
        return l -> Objects.nonNull(l.getAutoIncrement()) && l.getAutoIncrement() < 0;
    }

    public Predicate<Table> tableCommentRule() {
        return l -> Objects.isNull(l.getComment());
    }

    public Predicate<Table> tableEngineRule() {
        return l -> !StringUtils.isEmpty(l.getEngine()) && !"innodb".equalsIgnoreCase(l.getEngine());
    }
}
