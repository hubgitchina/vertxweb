package auth;

import java.util.Map;

import auth.impl.MyJDBCAuthenticationImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.jdbc.JDBCAuthenticationOptions;
import io.vertx.ext.auth.jdbc.JDBCHashStrategy;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: MyJDBCAuthentication
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-09-18 14:00
 * @Version 1.0
 */
public interface MyJDBCAuthentication extends AuthenticationProvider {

	/** @deprecated */
	@Deprecated
	static MyJDBCAuthentication create(JDBCClient client, JDBCHashStrategy hashStrategy,
			JDBCAuthenticationOptions options) {

		return new MyJDBCAuthenticationImpl(client, hashStrategy, options);
	}

	static MyJDBCAuthentication create(JDBCClient client, JDBCAuthenticationOptions options) {

		return new MyJDBCAuthenticationImpl(client, options);
	}

	void authenticate(UsernamePasswordCredentials var1, Handler<AsyncResult<User>> var2);

	default Future<User> authenticate(UsernamePasswordCredentials credentials) {

		Promise<User> promise = Promise.promise();
		this.authenticate(credentials, promise);
		return promise.future();
	}

	String hash(String var1, Map<String, String> var2, String var3, String var4);

	default String hash(String id, String salt, String password) {

		return this.hash(id, (Map) null, salt, password);
	}
}
