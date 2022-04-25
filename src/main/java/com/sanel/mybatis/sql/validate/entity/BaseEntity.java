package com.sanel.mybatis.sql.validate.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author suzhiwen
 * @description 继承的实体
 * @date 2022/4/14  上午7:35
 */
@Data
@Accessors(chain = true)
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = -516869146497896162L;

    private Long id;

    private Date createTime;
    private String creater;
    private Date updateTime;
    private String updater;
}
