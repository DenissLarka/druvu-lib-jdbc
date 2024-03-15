package com.druvu.lib.jdbc;

import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;

import com.druvu.lib.jdbc.statement.SqlStatement;

/**
 * @author Deniss Larka
 * at 11 Nov 2020
 */
class DbAccessDirectImpl implements DbAccessDirect {

	private final JdbcTemplate jdbcTemplate;

	DbAccessDirectImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
	}

	@Override
	public <T> List<T> select(SqlStatement<T> select) {
		return jdbcTemplate.query(select.getQuery(), select.rowMapper(), select.getParameters());
	}

	@Override
	public Integer update(SqlStatement<?> update) {
		return jdbcTemplate.update(update.getQuery(), update.getParameters());
	}

	@Override
	public void call(String procedure) {
		jdbcTemplate.update(procedure);
	}
}
