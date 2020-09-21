package auth;

import auth.impl.MyJDBCAuthImpl;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.jdbc.JDBCAuthenticationOptions;
import io.vertx.ext.auth.jdbc.JDBCAuthorizationOptions;
import io.vertx.ext.auth.jdbc.JDBCHashStrategy;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: MyJDBCAuth
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-09-18 11:33
 * @Version 1.0
 */
public interface MyJDBCAuth extends AuthenticationProvider {

	String DEFAULT_AUTHENTICATE_QUERY = "SELECT password, salt, id FROM user WHERE login_name = ?";
	String DEFAULT_ROLES_QUERY = "SELECT position_id FROM position_user WHERE user_id = ?";
	String DEFAULT_PERMISSIONS_QUERY = "SELECT name FROM position RP, position_user UR WHERE UR.user_id = ? AND UR.position_id = RP.id";
	String DEFAULT_ROLE_PREFIX = "role:";

	static MyJDBCAuth create(Vertx vertx, JDBCClient client) {

		return new MyJDBCAuthImpl(vertx, client);
	}

	static MyJDBCAuth create(Vertx vertx, JDBCClient client,
			JDBCAuthenticationOptions authenticationOptions,
			JDBCAuthorizationOptions authorizationOptions) {

		return new MyJDBCAuthImpl(vertx, client, authenticationOptions, authorizationOptions);
	}

	MyJDBCAuth setAuthenticationQuery(String var1);

	MyJDBCAuth setRolesQuery(String var1);

	MyJDBCAuth setPermissionsQuery(String var1);

	MyJDBCAuth setRolePrefix(String var1);

	MyJDBCAuth setHashStrategy(JDBCHashStrategy var1);

	default String computeHash(String password, String salt) {

		return this.computeHash(password, salt, -1);
	}

	String computeHash(String var1, String var2, int var3);

	String generateSalt();

	MyJDBCAuth setNonces(JsonArray var1);

	void authenticate(UsernamePasswordCredentials var1, Handler<AsyncResult<User>> var2);

	default Future<User> authenticate(UsernamePasswordCredentials credentials) {

		Promise<User> promise = Promise.promise();
		this.authenticate(credentials, promise);
		return promise.future();
	}
}
