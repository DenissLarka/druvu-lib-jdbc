package com.druvu.lib.jdbc;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.sql.DataSource;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.druvu.lib.jdbc.internal.NamedSqlStatement;
import com.druvu.lib.jdbc.util.SqlDebug;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Deniss Larka
 * at 29 Mar 2020
 */

@Slf4j
class DbAccessTxImpl implements DbAccess {

	public static final int FETCH_SIZE = 2000;
	private final String id;
	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedJdbcTemplate;
	private final TransactionTemplate transactionReadOnly;
	private final TransactionTemplate transactionWrite;

	DbAccessTxImpl(final String id, final DataSource dataSource, PlatformTransactionManager transactionManager) {
		this.id = Objects.requireNonNull(id);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcTemplate.setFetchSize(FETCH_SIZE);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		this.transactionReadOnly = new TransactionTemplate(transactionManager);
		this.transactionReadOnly.setReadOnly(true);
		this.transactionWrite = new TransactionTemplate(transactionManager);
		this.transactionWrite.setReadOnly(false);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public <T> List<T> inTransaction(Function<DbAccessDirect, List<T>> statement) {
		return transactionWrite.execute(status -> statement.apply(new DbAccessDirectImpl(this.jdbcTemplate)));
	}

	@Override
	public <T> List<T> select(SqlStatement<T> select) {
		final long start = System.currentTimeMillis();
		final List<T> result;
		if (select instanceof NamedSqlStatement<?> named) {
			result = transactionReadOnly.execute(status -> namedJdbcTemplate.query(
					select.getQuery(),
					named.getNamedParameters(),
					select.rowMapper()));
		} else {
			result = transactionReadOnly.execute(status -> jdbcTemplate.query(
					select.getQuery(),
					select.rowMapper(),
					select.getParameters()));
		}
		if (log.isDebugEnabled()) {
			final String filledSqlString = SqlDebug.debug(select);
			final long stop = System.currentTimeMillis();
			log.debug("DB-SELECT: {}/{}", (stop - start), filledSqlString);
		}
		return result;
	}

	@Override
	public Integer update(SqlStatement<?> update) {
		final long start = System.currentTimeMillis();
		final Integer result;
		if (update instanceof NamedSqlStatement<?> named) {
			result = transactionWrite.execute(status -> namedJdbcTemplate.update(update.getQuery(), named.getNamedParameters()));
		} else {
			result = transactionWrite.execute(status -> jdbcTemplate.update(update.getQuery(), update.getParameters()));
		}
		if (log.isDebugEnabled()) {
			final String filledSqlString = SqlDebug.debug(update);
			final long stop = System.currentTimeMillis();
			log.debug("DB-UPDATE: {}/{}", (stop - start), filledSqlString);
		}
		return result;
	}

	@Override
	public void call(String procedure) {
		transactionWrite.execute(status -> jdbcTemplate.update(procedure));
	}

}
