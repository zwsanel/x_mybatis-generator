package com.sanel.mybatis.sql.validate;

import com.sanel.mybatis.sql.validate.domain.Result;
import com.sanel.mybatis.sql.validate.domain.Table;

/**
 * @author suzhiwen
 * @description TODO
 * @date 2022/4/16  上午12:18
 */
public interface Validate {


    Result doValidate(Table table);

}
