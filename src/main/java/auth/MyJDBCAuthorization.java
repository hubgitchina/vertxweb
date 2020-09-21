package auth;

import auth.impl.MyJDBCAuthorizationImpl;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.jdbc.JDBCAuthorizationOptions;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: MyJDBCAuthorization
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-09-18 18:33
 * @Version 1.0
 */
public interface MyJDBCAuthorization extends AuthorizationProvider {

	static MyJDBCAuthorization create(String providerId, JDBCClient client,
			JDBCAuthorizationOptions options) {

		return new MyJDBCAuthorizationImpl(providerId, client, options);
	}
}
