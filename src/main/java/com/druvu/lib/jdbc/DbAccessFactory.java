package com.druvu.lib.jdbc;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Deniss Larka
 * at 11 Aug 2020
 */
@Slf4j
public final class DbAccessFactory {

	private DbAccessFactory() {
	}

	public static DbAccess create(DbConfig dbConfig) {
		DataSource pool = createPool(dbConfig);
		DbAccessTxImpl result = new DbAccessTxImpl(dbConfig.getId(), pool, new DataSourceTransactionManager(pool));
		validate(result, dbConfig);
		log.info("DB created {}", dbConfig);
		return result;
	}

	/**
	 * Variant for drivers that do not support JDBC transactions — primarily ClickHouse,
	 * which rejects both {@code setAutoCommit(false)} (made by Spring's TX manager) and
	 * {@code setReadOnly(false)} (made by the Tomcat pool on every borrow when its
	 * {@code defaultReadOnly} property is non-null).
	 *
	 * <p>This factory addresses both:
	 * <ul>
	 *   <li>Returns {@link DbAccessDirect} (no TX manager wired) — the narrower interface
	 *       also makes it a compile-time error to call {@code inTransaction} on a
	 *       connection that cannot provide one.</li>
	 *   <li>Forces {@code defaultReadOnly=null} on the pool so Tomcat skips the
	 *       {@code setReadOnly} call entirely. This overrides whatever the caller set,
	 *       because the only realistic reason to use this factory is a driver/server
	 *       (ClickHouse, analytical stores) that rejects the call.</li>
	 * </ul>
	 *
	 * <p>Use {@link #create(DbConfig)} for normal relational databases (PostgreSQL, Oracle, …) —
	 * they support both transactions and {@code setReadOnly}.
	 */
	public static DbAccessDirect createNonTransactional(DbConfig dbConfig) {
		DbConfig safeConfig = dbConfig.withDefaultReadOnly(null);
		DataSource pool = createPool(safeConfig);
		JdbcTemplate template = new JdbcTemplate(pool);
		template.setFetchSize(DbAccessTxImpl.FETCH_SIZE);
		DbAccessDirect result = new DbAccessDirectImpl(safeConfig.getId(), template);
		validate(result, safeConfig);
		log.info("DB created (non-transactional) {}", safeConfig);
		return result;
	}

	private static DataSource createPool(DbConfig dbConfig) {
		return new DataSource(dbConfig.getPoolProperties());
	}

	private static void validate(DbAccessDirect dbAccess, DbConfig dbConfig) {
		final String validationQuery = dbConfig.getPoolProperties().getValidationQuery();
		if (validationQuery == null || validationQuery.isEmpty()) {
			return;
		}
		try {
			dbAccess.select(SimpleSql.fromString(validationQuery));
		}
		catch (Exception e) { //NOPMD
			log.error("Exception in DB validation: {}", dbConfig);
			throw e;
		}
	}

}
