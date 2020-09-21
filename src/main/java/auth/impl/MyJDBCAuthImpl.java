package auth.impl;

import java.util.Objects;

import auth.MyJDBCAuth;
import auth.MyJDBCAuthentication;
import auth.MyJDBCAuthorization;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.jdbc.JDBCAuthenticationOptions;
import io.vertx.ext.auth.jdbc.JDBCAuthorizationOptions;
import io.vertx.ext.auth.jdbc.JDBCHashStrategy;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: MyJDBCAuthImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-09-18 11:29
 * @Version 1.0
 */
public class MyJDBCAuthImpl implements MyJDBCAuth {

	private JDBCClient client;
	private MyJDBCAuthentication authenticationProvider;
	private JDBCAuthenticationOptions authenticationOptions;
	private MyJDBCAuthorization authorizationProvider;
	private JDBCAuthorizationOptions authorizationOptions;
	private JDBCHashStrategy hashStrategy;

	public MyJDBCAuthImpl(Vertx vertx, JDBCClient client) {

		this.client = client;

		this.hashStrategy = JDBCHashStrategy.createSHA512(vertx);

		this.authenticationOptions = new JDBCAuthenticationOptions();
		this.authenticationOptions.setAuthenticationQuery(DEFAULT_AUTHENTICATE_QUERY);

		this.authorizationOptions = new JDBCAuthorizationOptions();
		this.authorizationOptions.setRolesQuery(DEFAULT_ROLES_QUERY);
		this.authorizationOptions.setPermissionsQuery(DEFAULT_PERMISSIONS_QUERY);

		this.authenticationProvider = MyJDBCAuthentication.create(client, this.hashStrategy,
				this.authenticationOptions);

		this.authorizationProvider = MyJDBCAuthorization.create("jdbc-auth", client,
				this.authorizationOptions);
	}

	public MyJDBCAuthImpl(Vertx vertx, JDBCClient client,
			JDBCAuthenticationOptions authenticationOptions,
			JDBCAuthorizationOptions authorizationOptions) {

		this.client = client;
		this.hashStrategy = JDBCHashStrategy.createSHA512(vertx);
		this.authenticationOptions = authenticationOptions;
		this.authorizationOptions = authorizationOptions;
		this.authenticationProvider = MyJDBCAuthentication.create(client, this.hashStrategy,
				this.authenticationOptions);
		this.authorizationProvider = MyJDBCAuthorization.create("jdbc-auth", client,
				this.authorizationOptions);
	}

	@Override
	public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {

		this.authenticate(new UsernamePasswordCredentials(authInfo), resultHandler);
	}

	@Override
	public void authenticate(UsernamePasswordCredentials credentials,
			Handler<AsyncResult<User>> resultHandler) {

		this.authenticationProvider.authenticate(credentials, (authenticationResult) -> {
			if (authenticationResult.failed()) {
				resultHandler.handle(Future.failedFuture(authenticationResult.cause()));
			} else {
				User user = authenticationResult.result();
				this.authorizationProvider.getAuthorizations(user, (userAuthorizationResult) -> {
					if (userAuthorizationResult.failed()) {
						resultHandler.handle(Future.failedFuture(userAuthorizationResult.cause()));
					} else {
						resultHandler.handle(Future.succeededFuture(user));
					}

				});
			}

		});
	}

	@Override
	public MyJDBCAuth setAuthenticationQuery(String authenticationQuery) {

		this.authenticationOptions.setAuthenticationQuery(authenticationQuery);
		return this;
	}

	@Override
	public MyJDBCAuth setRolesQuery(String rolesQuery) {

		this.authorizationOptions.setRolesQuery(rolesQuery);
		return this;
	}

	@Override
	public MyJDBCAuth setPermissionsQuery(String permissionsQuery) {

		this.authorizationOptions.setPermissionsQuery(permissionsQuery);
		return this;
	}

	@Override
	public MyJDBCAuth setRolePrefix(String rolePrefix) {

		return this;
	}

	@Override
	public MyJDBCAuth setHashStrategy(JDBCHashStrategy strategy) {

		this.hashStrategy = Objects.requireNonNull(strategy);
		this.authenticationProvider = MyJDBCAuthentication.create(this.client, strategy,
				this.authenticationOptions);
		return this;
	}

	@Override
	public String computeHash(String password, String salt, int version) {

		return this.hashStrategy.computeHash(password, salt, version);
	}

	@Override
	public String generateSalt() {

		return this.hashStrategy.generateSalt();
	}

	@Override
	public MyJDBCAuth setNonces(JsonArray nonces) {

		this.hashStrategy.setNonces(nonces);
		return this;
	}

	String getRolesQuery() {

		return this.authorizationOptions.getRolesQuery();
	}

	String getPermissionsQuery() {

		return this.authorizationOptions.getPermissionsQuery();
	}
}
