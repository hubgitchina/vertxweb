package com.demo.vertx;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.validation.annotation.Validated;

import com.demo.enums.RequestMethod;

import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * @ClassName: VerticleUtils
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:15
 * @Version 1.0
 */
@Validated
public class VerticleUtils {

	/**
	 * build api path
	 *
	 * @param superPath
	 *            class api path
	 * @param methodPath
	 *            method api path
	 * @return
	 */
	public static String buildApiPath(@NotNull String superPath, @NotNull String methodPath) {

		if (!superPath.startsWith("/")) {
			superPath = "/" + superPath;
		}
		if (!superPath.endsWith("/")) {
			superPath += "/";
		}
		if (methodPath.startsWith("/")) {
			methodPath = methodPath.substring(1);
		}
		return superPath + methodPath;
	}

	/**
	 * build route api path method
	 *
	 * @param url
	 *            api path
	 * @param router
	 *            router
	 * @param requestMethod
	 *            method enum
	 * @return
	 */
	public static Route buildRouterUrl(String url, Router router, RequestMethod requestMethod) {

		// 路由
		Route route;
		switch (requestMethod) {
		case POST:
			route = router.post(url);
			break;
		case PUT:
			route = router.put(url);
			break;
		case DELETE:
			route = router.delete(url);
			break;
		case ROUTE:
			route = router.route(url);
			break;
		case GET: // fall through
		default:
			route = router.get(url);
			break;
		}
		return route;
	}

	/**
	 * scanner controller class array
	 *
	 * @param packagePath
	 *            controller package
	 * @param resourceLoader
	 *            SpringBoot resourceLoader
	 * @return
	 */
	public static Resource[] scannerControllerClass(String packagePath,
			ResourceLoader resourceLoader) {

		ResourcePatternResolver resolver = ResourcePatternUtils
				.getResourcePatternResolver(resourceLoader);
		String controllerBasePackagePath = packagePath.replace(".", "/");
		try {
			Resource[] resources = resolver.getResources(
					String.format("classpath*:%s/**/*.class", controllerBasePackagePath));
			return resources;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Resource[] {};
	}

}
