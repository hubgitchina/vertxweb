package com.demo.verticle;

import java.util.List;

import com.demo.util.AsyncJdbcUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

/**
 * @ClassName: JdbcTestVerticle
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-19 15:32
 * @Version 1.0
 */
public class AsyncJdbcTestVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {

		// 获取到数据库连接的客户端
		AsyncSQLClient asyncSQLClient = new AsyncJdbcUtils(vertx).getAsyncSQLClient();

		asyncSQLClient.getConnection(res -> {
			if (res.succeeded()) {
				System.out.println("连接成功");
				SQLConnection connection = res.result();
				connection.query("select * from auth_user", res2 -> {
					if (res2.succeeded()) {
						System.out.println("查询成功");
						ResultSet rs = res2.result();
						// Do something with results
						List<JsonObject> rows = rs.getRows();
						for (JsonObject jsonObject : rows) {
							System.out.println(jsonObject);
						}
					} else {
						System.out.println("查询失败");
					}
					connection.close();
				});
			} else {
				// Failed to get connection - deal with it
				System.out.println("连接失败");
			}
		});
	}

	public static void main(String[] args) {

		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new AsyncJdbcTestVerticle());
	}

}
