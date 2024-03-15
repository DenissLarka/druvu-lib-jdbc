package com.druvu.lib.jdbc.tomcat_pool;

import java.util.Objects;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import lombok.SneakyThrows;

/**
 * @author Deniss Larka
 * on 30 Jan 2024
 */
public class PoolPropertiesEx extends PoolProperties {

	private static final long serialVersionUID = 1L;

	public static final int INITIAL_SIZE = 1;
	public static final int MAX_ACTIVE = 5;
	public static final int MAX_IDLE = 2;
	public static final int MIN_IDLE = 1;
	public static final int MAX_WAIT = 20000;
	public static final int VALIDATION_QUERY_TIMEOUT = -1;
	public static final int TIME_BETWEEN_EVICTION_RUNS_MILLIS = 120000;
	public static final int MIN_EVICTABLE_IDLE_TIME_MILLIS = 1800000;

	public static PoolPropertiesEx create(String url, String user, String password, String driver, String validationSelect) { //NOPMD
		PoolPropertiesEx result = new PoolPropertiesEx();
		result.setDriverClassName(Objects.requireNonNull(driver, "driver is null"));
		result.setUrl(Objects.requireNonNull(url, "url is null"));
		result.setUsername(Objects.requireNonNull(user, "user is null"));
		result.setPassword(Objects.requireNonNull(password, "password is null"));
		result.setValidationQuery(Objects.requireNonNull(validationSelect, "validation query is is null"));

		result.setConnectionProperties("[]");
		result.setDefaultAutoCommit(false);
		result.setDefaultReadOnly(false);
		result.setInitialSize(INITIAL_SIZE);
		result.setMaxActive(MAX_ACTIVE);
		result.setMaxIdle(MAX_IDLE);
		result.setMinIdle(MIN_IDLE);
		result.setMaxWait(MAX_WAIT);
		result.setValidationQueryTimeout(VALIDATION_QUERY_TIMEOUT);
		result.setTestOnBorrow(true);
		result.setTestOnReturn(true);
		result.setTestWhileIdle(true);
		result.setTimeBetweenEvictionRunsMillis(TIME_BETWEEN_EVICTION_RUNS_MILLIS);
		result.setMinEvictableIdleTimeMillis(MIN_EVICTABLE_IDLE_TIME_MILLIS);
		result.setAccessToUnderlyingConnectionAllowed(false);
		return result;
	}

	@SneakyThrows
	public static PoolConfiguration copy(PoolConfiguration pp) {
		if (pp instanceof PoolPropertiesEx ex) {
			return (PoolPropertiesEx) ex.clone();
		}
		throw new IllegalArgumentException("Should be PoolPropertiesEx instance");
	}
}
