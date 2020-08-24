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
	private String message;

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

	public String getMessage() {

		return message;
	}

	public void setMessage(String message) {

		this.message = message;
	}

	public ResponeWrapper() {

	}

	public ResponeWrapper(int code, T data, String message) {

		this.code = code;
		this.data = data;
		this.message = message;
	}

	public static final ResponeWrapper<String> RESPONE_SUCCESS = new ResponeWrapper<>(HTTP_OK, null,
			"操作成功");
	public static final ResponeWrapper<String> RESPONE_FAIL = new ResponeWrapper<>(10001, null,
			"操作失败");

}
