package com.demo.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.DataSourceProvider;

/**
 * @ClassName: DruidDataSourceProvider
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-25 15:18
 * @Version 1.0
 */
public class DruidDataSourceProvider implements DataSourceProvider {

	private final Logger logger = LoggerFactory.getLogger(DruidDataSourceProvider.class);

	@Override
	public DataSource getDataSource(JsonObject config) throws SQLException {

		DruidDataSource ds = new DruidDataSource();
		// Method[] methods = DruidDataSource.class.getMethods();
		// Map<String, Method> methodmap = new HashMap<>(methods.length);
		// for (Method method : methods) {
		// methodmap.put(method.getName(), method);
		// }

		for (Map.Entry<String, Object> entry : config) {
			String name = entry.getKey();

			if ("provider_class".equals(name)) {
				continue;
			}

			String mName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

			try {
				Class paramClazz = entry.getValue().getClass();
				if (paramClazz.equals(Integer.class)) {
					paramClazz = int.class;
				} else if (paramClazz.equals(Long.class)) {
					paramClazz = long.class;
				} else if (paramClazz.equals(Boolean.class)) {
					paramClazz = boolean.class;
				}
				Method method = DruidDataSource.class.getMethod(mName, paramClazz);
				method.invoke(ds, entry.getValue());
			} catch (NoSuchMethodException e) {
				logger.warn("no such method:" + mName);
				System.out.println(entry.getValue().getClass());
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return ds;
	}

	@Override
	public void close(DataSource dataSource) throws SQLException {

		if (dataSource instanceof DruidDataSource) {
			((DruidDataSource) dataSource).close();
		}
	}

	@Override
	public int maximumPoolSize(DataSource dataSource, JsonObject config) throws SQLException {

		if (dataSource instanceof DruidDataSource) {
			Integer val = config.getInteger("maxActive");
			if (val == null) {
				val = ((DruidDataSource) dataSource).getMaxActive();
				// val = DruidAbstractDataSource.DEFAULT_MAX_ACTIVE_SIZE;
			}
			return val;
		}
		return -1;
	}
}
