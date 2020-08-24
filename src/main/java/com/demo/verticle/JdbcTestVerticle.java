package com.demo.verticle;

import java.util.List;

import com.demo.util.JdbcUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

/**
 * @ClassName: JdbcTestVerticle
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-19 15:32
 * @Version 1.0
 */
public class JdbcTestVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {

		// 获取到数据库连接的客户端
		JDBCClient jdbcClient = new JdbcUtils(vertx).getDbClient();

		jdbcClient.getConnection(res -> {
			if (res.succeeded()) {

				SQLConnection connection = res.result();

				connection.query("select * from user", res2 -> {
					if (res2.succeeded()) {

						ResultSet rs = res2.result();
						// Do something with results
						List<JsonObject> rows = rs.getRows();
						for (JsonObject jsonObject : rows) {
							System.out.println(jsonObject);
						}
					}
				});
			} else {
				// Failed to get connection - deal with it
				System.out.println("查询失败");
			}
		});

		// String sql = "select * from user where locked = ?";
		// // 构造参数
		// JsonArray params = new JsonArray().add(0);
		// // 执行查询
		// jdbcClient.queryWithParams(sql, params, qryRes -> {
		// if (qryRes.succeeded()) {
		// // 获取到查询的结果，Vert.x对ResultSet进行了封装
		// ResultSet resultSet = qryRes.result();
		// // 把ResultSet转为List<JsonObject>形式
		// List<JsonObject> rows = resultSet.getRows();
		// // 输出结果
		// System.out.println(rows);
		// } else {
		// System.out.println("查询数据库出错！");
		// }
		// });

	}

	public static void main(String[] args) {

		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new JdbcTestVerticle());
	}

}
