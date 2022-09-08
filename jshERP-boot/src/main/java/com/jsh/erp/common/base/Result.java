package com.jsh.erp.common.base;

import com.jsh.erp.common.annotation.FieldDescription;
import com.jsh.erp.common.exception.ErpErrorCode;
import lombok.Getter;
import lombok.ToString;


/**
 * 通用返回值
 *
 * @param <T> 返回业务数据的类型
 * @author predecessor
 */
@Getter
@ToString
public class Result<T> {
    public static final int SUCCESS = 200;
    /**
     * 前端统一框架处理，0表示成功
     */
    public static final int FRONT_SUCCESS = 0;

    @FieldDescription(value = "状态码，200代表正确，非200代表错误")
    private final Integer code;
    @FieldDescription(value = "状态码不等于200时，记录发生错误的具体信息")
    private String msg;
    @FieldDescription(value = "状态码等于200时，返回真正的业务数据")
    private T data;

    private Result() {
        this(SUCCESS);
    }

    public Result(T data) {
        this();
        this.data = data;
    }

    private Result(int code) {
        this.code = code;
    }

    private Result(int code, String msg) {
        this(code);
        this.msg = msg;
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 正确带业务数据的返回值
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(data);
    }

    /**
     * 返回给前端的成功，code为0
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 正确带业务数据的返回值
     */
    public static <T> Result<T> frontOk(T data) {
        return new Result<>(FRONT_SUCCESS, null, data);
    }

    /**
     * @param code 错误码
     * @param msg  错误信息
     * @return 错误返回值
     */
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg);
    }

    /**
     * @param code   错误码
     * @param format 错误信息模板
     * @param args   错误信息参数
     * @return 错误返回值
     */
    public static <T> Result<T> fail(int code, String format, Object... args) {
        return new Result<>(code, String.format(format, args));
    }

    /**
     * @param code 错误码枚举类
     * @return 错误返回值
     */
    public static <T> Result<T> fail(ErpErrorCode code) {
        return fail(code.getCode(), code.getMsg());
    }

    /**
     * @param code 错误码枚举类
     * @param args 错误信息或匹配缺省错误信息模板的参数
     * @return 错误返回值
     */
    public static <T> Result<T> fail(ErpErrorCode code, Object... args) {
        return fail(code.getCode(), code.getMsg(), args);
    }

    /**
     * @param error 错误码
     * @return 错误返回值
     */
    public static <T> Result<T> failWithData(ErpErrorCode error, T data) {
        Result<T> result = new Result<>(error.getCode(), error.getMsg());
        result.data = data;
        return result;
    }

    /**
     * @return 是否成功
     */
    public boolean success() {
        return code == SUCCESS || code == FRONT_SUCCESS;
    }
}
