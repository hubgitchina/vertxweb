package auth.impl;

import java.util.*;

import auth.MyJDBCAuthorization;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.ext.auth.jdbc.JDBCAuthorizationOptions;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

/**
 * @ClassName: MyJDBCAuthorizationImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-09-18 18:34
 * @Version 1.0
 */
public class MyJDBCAuthorizationImpl implements MyJDBCAuthorization {

	private static final String DEFAULT_USERID_KEY = "userId";
	private String providerId;
	private JDBCAuthorizationOptions options;
	private JDBCClient client;
	private String userIdKey;

	public MyJDBCAuthorizationImpl(String providerId, JDBCClient client,
			JDBCAuthorizationOptions options) {

		this.providerId = Objects.requireNonNull(providerId);
		this.client = Objects.requireNonNull(client);
		this.options = Objects.requireNonNull(options);
		this.userIdKey = DEFAULT_USERID_KEY;
	}

	@Override
	public String getId() {

		return this.providerId;
	}

	private void getRoles(SQLConnection sqlConnection, JsonArray params,
			Handler<AsyncResult<Set<Authorization>>> resultHandler) {

		if (this.options.getRolesQuery() != null) {
			sqlConnection.queryWithParams(this.options.getRolesQuery(), params, (queryResponse) -> {
				if (queryResponse.succeeded()) {
					Set<Authorization> authorizations = new HashSet();
					ResultSet resultSet = queryResponse.result();
					Iterator var4 = resultSet.getResults().iterator();

					while (var4.hasNext()) {
						JsonArray result = (JsonArray) var4.next();
						String role = result.getString(0);
						authorizations.add(RoleBasedAuthorization.create(role));
					}

					resultHandler.handle(Future.succeededFuture(authorizations));
				} else {
					resultHandler.handle(Future.failedFuture(queryResponse.cause()));
				}

			});
		} else {
			resultHandler.handle(Future.succeededFuture(Collections.emptySet()));
		}

	}

	private void getPermissions(SQLConnection sqlConnection, JsonArray params,
			Handler<AsyncResult<Set<Authorization>>> resultHandler) {

		if (this.options.getPermissionsQuery() != null) {
			sqlConnection.queryWithParams(this.options.getPermissionsQuery(), params,
					(queryResponse) -> {
						if (queryResponse.succeeded()) {
							Set<Authorization> authorizations = new HashSet();
							ResultSet resultSet = queryResponse.result();
							Iterator var4 = resultSet.getResults().iterator();

							while (var4.hasNext()) {
								JsonArray result = (JsonArray) var4.next();
								String permission = result.getString(0);
								authorizations.add(PermissionBasedAuthorization.create(permission));
							}

							resultHandler.handle(Future.succeededFuture(authorizations));
						} else {
							resultHandler.handle(Future.failedFuture(queryResponse.cause()));
						}

					});
		} else {
			resultHandler.handle(Future.succeededFuture(Collections.emptySet()));
		}

	}

	@Override
	public void getAuthorizations(User user, Handler<AsyncResult<Void>> resultHandler) {

		this.client.getConnection((connectionResponse) -> {
			if (connectionResponse.succeeded()) {
				String userId = user.principal().getString(this.userIdKey);
				if (userId != null) {
					JsonArray params = (new JsonArray()).add(userId);
					SQLConnection connection = connectionResponse.result();
					this.getRoles(connection, params, (roleResponse) -> {
						if (roleResponse.succeeded()) {
							Set<Authorization> authorizations = new HashSet(roleResponse.result());
							this.getPermissions(connection, params, (permissionResponse) -> {
								if (permissionResponse.succeeded()) {
									authorizations.addAll(permissionResponse.result());
									user.authorizations().add(this.getId(), authorizations);
									resultHandler.handle(Future.succeededFuture());
								} else {
									resultHandler.handle(
											Future.failedFuture(permissionResponse.cause()));
								}

								connection.close();
							});
						} else {
							resultHandler.handle(Future.failedFuture(roleResponse.cause()));
							connection.close();
						}

					});
				} else {
					resultHandler.handle(Future.failedFuture("Couldn't get the userId"));
					(connectionResponse.result()).close();
				}
			} else {
				resultHandler.handle(Future.failedFuture(connectionResponse.cause()));
			}

		});
	}
}
