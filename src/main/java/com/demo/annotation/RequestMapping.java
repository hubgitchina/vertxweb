package com.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.demo.enums.RequestMethod;

/**
 * @ClassName: RequestMapping
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:14
 * @Version 1.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
	/**
	 * api path
	 *
	 * @return
	 */
	String[] value() default {};

	/**
	 * request method
	 *
	 * @return
	 */
	RequestMethod method() default RequestMethod.GET;
}
