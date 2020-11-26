package com.demo.verticle;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.demo.annotation.RequestBlockingHandler;
import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.config.SpringBootContext;
import com.demo.handler.SessionCheckHandler;
import com.demo.handler.TokenCheckHandler;
import com.demo.util.ClazzUtils;
import com.demo.vertx.VerticleUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * @ClassName: VerticleMain
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 11:06
 * @Version 1.0
 */
@Component
public class VerticleMain extends AbstractVerticle {

	private final Logger logger = LoggerFactory.getLogger(VerticleMain.class);

	@Autowired
	private ResourceLoader resourceLoader;

	/**
	 * Controller 所在的包
	 */
	private final String controllerBasePackage[] = { "com.demo.controller" };

	/**
	 * 文件分隔符：Windows下为\\，Linux下为/
	 */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		super.start();

		// 路由
		Router router = Router.router(vertx);

		router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

		// AuthenticationProvider myJDBCAuth = SpringBootContext.getApplicationContext()
		// .getBean(MyJDBCAuth.class);

		// 当请求中没有session时，自动跳转到登录页
		// RedirectAuthHandler authHandler = RedirectAuthHandler.create(myJDBCAuth,
		// "/freeMarker/login");
		// router.route().path("/user/*").handler(authHandler);

		// vertx内置登录处理器
		// FormLoginHandler formLoginHandler =
		// FormLoginHandler.create(myJDBCAuth).setDirectLoggedInOKURL("/index.html");
		// router.route("/login").handler(formLoginHandler);

		/** 将静态资源放到session验证前面，防止页面依赖css,js等文件被拦截 */
		// CSS，IMAGE，JS等静态资源设置
		router.route("/static/*").handler(StaticHandler.create("static"));

		SessionCheckHandler sessionCheckHandler = SpringBootContext.getApplicationContext()
				.getBean(SessionCheckHandler.class);

		// 添加session拦截器
		router.route().path("/*").handler(sessionCheckHandler);

		// router.route().handler(routingContext -> {
		// Session session = routingContext.session();
		// Integer cnt = session.get("hitcount");
		// cnt = (cnt == null ? 0 : cnt) + 1;
		// session.put("hitcount", cnt);
		// routingContext.response().putHeader("content-type", "text/html")
		// .end("<html><body><h1>Hitcount: " + cnt + "</h1></body></html>");
		// });

		TokenCheckHandler tokenCheckHandler = SpringBootContext.getApplicationContext()
				.getBean(TokenCheckHandler.class);

		// 添加token拦截器
		router.route().path("/user1/*").handler(tokenCheckHandler);

		ConfigurableEnvironment environment = SpringBootContext.getApplicationContext()
				.getEnvironment();
		int port = Integer.parseInt(environment.getProperty("server.port"));

		/**
		 * 默认上传文件时会在服务器生成file-uploads文件夹<br/>
		 * setUploadsDirectory可改变缓存文件的存放路径<br/>
		 * setDeleteUploadedFilesOnEnd可设置上传成功后删除缓存文件
		 */
		router.route()
				.handler(BodyHandler.create()
						.setUploadsDirectory(System.getProperty("java.io.tmpdir"))
						.setDeleteUploadedFilesOnEnd(true));

		// favicon.ico图标设置
		router.route("/favicon.ico").handler(FaviconHandler.create("static/images/favicon.ico"));

		/** 映射服务器文件上传目录 */
		String uploadFolder = environment.getProperty("file.uploadFolder");
		router.route("/upload/file/*").handler(StaticHandler.create(uploadFolder));

		// Linux下部署时会提示【root cannot start with /】，可使用该方式解决
		// router.route("/upload/file/*").handler(StaticHandler.create().setAllowRootFileSystemAccess(true).setWebRoot(uploadFolder));

		// 编写一个get方法
		for (String packagePath : controllerBasePackage) {
			registerController(router, packagePath);
		}

		// 最后一个Route，请求URL没有匹配的Route, 则返回404
		router.route().last().handler(context -> {
			logger.error("请求URL【{}】未找到匹配的Route", context.request().path());
			// context.response().end("<h1>404</h1>");
			context.reroute(HttpMethod.GET, "/recipes/404");
		});

		// Route处理过程中发生了错误，且请求匹配的Route没有通过方法failureHandler设置自己专属的错误处理器，则返回
		router.route().failureHandler(handler -> {
			logger.error("Route处理过程出现异常");
			handler.response().putHeader("Content-Type", "text/html;charset=utf-8")
//					.end("系统异常，请联系管理员");
					.end(handler.failure().getMessage());
			handler.failure().printStackTrace();
		});

		// start listen port
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(router).listen(port, handler -> {
			if (handler.succeeded()) {
				logger.info("vertx run port : [{}] run state : [{}]", port, handler.succeeded());
				startPromise.complete();
			} else {
				logger.info("vertx run port : [{}] run state : [{}]", port, handler.failed());
				startPromise.fail(handler.cause());
			}
		});
	}

	/**
	 * register controller
	 */
	private void registerController(@NotNull Router router, String packagePath) {

		if (SpringBootContext.getApplicationContext() == null) {
			logger.warn("SpringBoot application context is null register controller is fail");
			return;
		}

		try {
			List<String> clazzNameList = ClazzUtils.getClazzName(packagePath, false);
			for (String clazzName : clazzNameList) {
				// get class
				Class<?> controllerClass = Class.forName(clazzName);
				// from class get controller instance bean
				Object controller = SpringBootContext.getApplicationContext()
						.getBean(controllerClass);

				RequestMapping classRequestMapping = controllerClass
						.getAnnotation(RequestMapping.class);
				// if controller class not have requestMapping annotation -> skip register
				if (classRequestMapping == null) {
					continue;
				}
				// register controller method
				registerControllerMethod(router, classRequestMapping, controllerClass, controller);
			}
		} catch (Exception ex) {
			logger.error("registerController fail ", ex);
		}
	}

	/**
	 * register controller method
	 *
	 * @param router
	 *            route
	 * @param classRequestMapping
	 *            controller requestMapping annotation
	 * @param controllerClass
	 *            controller class
	 * @param controller
	 *            controller instance
	 */
	private void registerControllerMethod(@NotNull Router router,
			@NotNull RequestMapping classRequestMapping, @NotNull Class<?> controllerClass,
			@NotNull Object controller) {

		// 获取控制器里的方法
		Method[] controllerClassMethods = controllerClass.getMethods();
		Arrays.asList(controllerClassMethods).stream()
				.filter(method -> method.getAnnotation(RequestMapping.class) != null)
				.forEach(method -> {
					try {
						RequestMapping methodRequestMapping = method
								.getAnnotation(RequestMapping.class);
						String superPath = classRequestMapping.value()[0];
						String methodPath = methodRequestMapping.value()[0];
						// if api path empty skip
						if (StringUtils.isEmpty(superPath) || StringUtils.isEmpty(methodPath)) {
							return;
						}
						String url = VerticleUtils.buildApiPath(superPath, methodPath);
						// build route
						Route route = VerticleUtils.buildRouterUrl(url, router,
								methodRequestMapping.method());
						// run controller method get Handler object
						Handler<RoutingContext> methodHandler = (Handler<RoutingContext>) method
								.invoke(controller);
						// register bodyAsJson handler
						Optional.ofNullable(method.getAnnotation(RequestBody.class))
								.ifPresent(requestBody -> {
									route.handler(BodyHandler.create());
								});
						// register controller mthod Handler object
						RequestBlockingHandler requestBlockingHandler = Optional
								.ofNullable(method.getAnnotation(RequestBlockingHandler.class))
								.orElseGet(() -> controllerClass
										.getAnnotation(RequestBlockingHandler.class));
						if (requestBlockingHandler != null) {
							// register blocking handler
							route.blockingHandler(methodHandler);
						} else {
							route.handler(methodHandler);
						}
						logger.info("register controller -> [{}]  method -> [{}]  url -> [{}] ",
								controllerClass.getName(), method.getName(), url);
					} catch (Exception e) {
						logger.error("registerControllerMethod fail controller: [{}]  method: [{}]",
								controllerClass, method.getName());
					}
				});
	}
}
