package com.demo.service;

/**
 * @ClassName: BaseAsyncService
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-19 17:37
 * @Version 1.0
 */
public interface BaseAsyncService {

	default String getAddress() {

		String className = this.getClass().getName();
		return className.substring(0, className.lastIndexOf("Impl")).replace(".impl", "");
	}

	default Class getAsyncInterfaceClass() throws ClassNotFoundException {

		String className = this.getClass().getName();
		return Class.forName(
				className.substring(0, className.lastIndexOf("Impl")).replace(".impl", ""));
	}
}