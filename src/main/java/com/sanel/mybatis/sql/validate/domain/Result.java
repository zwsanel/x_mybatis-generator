package com.sanel.mybatis.sql.validate.domain;

/**
 * @author suzhiwen
 * @description 结果
 * @date 2022/4/16  上午2:00
 */
public class Result {

    private boolean pass;
    private String msg;

    public Result(boolean pass, String msg) {
        this.pass = pass;
        this.msg = msg;
    }

    public Result() {
    }

    public boolean isPass() {
        return pass;
    }

    public void setStatus(boolean status) {
        this.pass = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static Result pass() {
        return new Result(true, "成功");
    }

    public static Result fail(String msg) {
        return new Result(false, msg);
    }
}
