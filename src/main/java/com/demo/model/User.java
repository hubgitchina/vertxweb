package com.demo.model;

/**
 * @ClassName: LoginModel
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 15:54
 * @Version 1.0
 */
public class User {

	private String id;

	private String userName;

	private Integer age;

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public String getUserName() {

		return userName;
	}

	public void setUserName(String userName) {

		this.userName = userName;
	}

	public Integer getAge() {

		return age;
	}

	public void setAge(Integer age) {

		this.age = age;
	}
}
