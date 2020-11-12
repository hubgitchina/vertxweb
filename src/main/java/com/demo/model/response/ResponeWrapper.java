package com.demo.model.response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @ClassName: ResponeWrapper
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 11:20
 * @Version 1.0
 */
public class ResponeWrapper<T> {

	private int code;
	private T data;
	private String msg;

	public int getCode() {

		return code;
	}

	public void setCode(int code) {

		this.code = code;
	}

	public T getData() {

		return data;
	}

	public void setData(T data) {

		this.data = data;
	}

	public String getMsg() {

		return msg;
	}

	public void setMsg(String msg) {

		this.msg = msg;
	}

	public ResponeWrapper() {

	}

	public ResponeWrapper(T data) {

		super();

		this.data = data;
	}

	public ResponeWrapper(int code, T data, String msg) {

		this.code = code;
		this.data = data;
		this.msg = msg;
	}

	public static final ResponeWrapper<String> RESPONE_SUCCESS = new ResponeWrapper<>(HTTP_OK, null,
			"操作成功");
	public static final ResponeWrapper<String> RESPONE_FAIL = new ResponeWrapper<>(10001, null,
			"操作失败");

}
