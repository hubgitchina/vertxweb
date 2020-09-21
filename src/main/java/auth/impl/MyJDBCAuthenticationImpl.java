package auth.impl;

import java.util.Map;
import java.util.Objects;

import com.alibaba.druid.support.json.JSONUtils;

import auth.MyJDBCAuthentication;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.HashingStrategy;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.jdbc.JDBCAuthenticationOptions;
import io.vertx.ext.auth.jdbc.JDBCHashStrategy;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

/**
 * @ClassName: MyJDBCAuthenticationImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-09-18 14:02
 * @Version 1.0
 */
public class MyJDBCAuthenticationImpl implements MyJDBCAuthentication {

	private final HashingStrategy strategy = HashingStrategy.load();
	private JDBCClient client;
	private JDBCHashStrategy legacyStrategy;
	private JDBCAuthenticationOptions options;

	public MyJDBCAuthenticationImpl(JDBCClient client, JDBCHashStrategy hashStrategy,
			JDBCAuthenticationOptions options) {

		this.client = Objects.requireNonNull(client);
		this.options = Objects.requireNonNull(options);
		this.legacyStrategy = Objects.requireNonNull(hashStrategy);
	}

	public MyJDBCAuthenticationImpl(JDBCClient client, JDBCAuthenticationOptions options) {

		this.client = Objects.requireNonNull(client);
		this.options = Objects.requireNonNull(options);
	}

	@Override
	public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {

		this.authenticate(new UsernamePasswordCredentials(authInfo), resultHandler);
	}

	@Override
	public void authenticate(UsernamePasswordCredentials credentials,
			Handler<AsyncResult<User>> resultHandler) {

		if (credentials.getUsername() == null) {
			resultHandler.handle(
					Future.failedFuture("authInfo must contain username in 'username' field"));
		} else if (credentials.getPassword() == null) {
			resultHandler.handle(
					Future.failedFuture("authInfo must contain password in 'password' field"));
		} else {
			this.executeQuery(this.options.getAuthenticationQuery(),
					(new JsonArray()).add(credentials.getUsername()), (queryResponse) -> {
						if (queryResponse.succeeded()) {
							ResultSet rs = queryResponse.result();
							switch (rs.getNumRows()) {
							case 0:
								resultHandler
										.handle(Future.failedFuture("Invalid username/password"));
								break;
							case 1:
								JsonArray row = rs.getResults().get(0);

								try {
									if (this.verify(row, credentials.getPassword())) {
										JsonObject principal = new JsonObject();
										principal.put("userId", row.getString(2));
										principal.put("userName", credentials.getUsername());
										User user = User.create(principal);
										resultHandler.handle(Future.succeededFuture(user));
									} else {
										resultHandler.handle(
												Future.failedFuture("Invalid username/password"));
									}
								} catch (RuntimeException var7) {
									resultHandler.handle(Future.failedFuture(var7));
								}
								break;
							default:
								resultHandler
										.handle(Future.failedFuture("Failure in authentication"));
							}
						} else {
							resultHandler.handle(Future.failedFuture(queryResponse.cause()));
						}

					});
		}
	}

	private boolean verify(JsonArray row, String password) {

		String hash = row.getString(0);
		if (hash.charAt(0) != '$') {
			if (this.legacyStrategy == null) {
				throw new IllegalStateException(
						"JDBC Authentication cannot handle legacy hashes without a JDBCStrategy");
			} else {
				String salt = row.getString(1);
				int version = -1;
				int sep = hash.lastIndexOf(36);
				if (sep != -1) {
					try {
						version = Integer.parseInt(hash.substring(sep + 1));
					} catch (NumberFormatException var8) {
						throw new IllegalStateException("Invalid nonce version: " + version);
					}
				}

				return JDBCHashStrategy.isEqual(hash,
						this.legacyStrategy.computeHash(password, salt, version));
			}
		} else {
			return this.strategy.verify(hash, password);
		}
	}

	void executeQuery(String query, JsonArray params,
			Handler<AsyncResult<ResultSet>> resultHandler) {

		this.client.getConnection((res) -> {
			if (res.succeeded()) {
				SQLConnection connection = (SQLConnection) res.result();
				connection.queryWithParams(query, params, (queryResponse) -> {
					resultHandler.handle(queryResponse);
					connection.close();
				});
			} else {
				resultHandler.handle(Future.failedFuture(res.cause()));
			}

		});
	}

	@Override
	public String hash(String id, Map<String, String> params, String salt, String password) {

		return this.strategy.hash(id, params, salt, password);
	}
}
