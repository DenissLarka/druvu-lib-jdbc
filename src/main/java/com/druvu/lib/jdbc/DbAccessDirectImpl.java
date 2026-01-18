package com.druvu.lib.jdbc;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.druvu.lib.jdbc.internal.NamedSqlStatement;

/**
 * @author Deniss Larka
 * <br/>on 11 Nov 2020
 */
class DbAccessDirectImpl implements DbAccessDirect {

	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedJdbcTemplate;

	DbAccessDirectImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	}

	@Override
	public <T> List<T> select(SqlStatement<T> select) {
		if (select instanceof NamedSqlStatement<?> named) {
			return namedJdbcTemplate.query(select.getQuery(), named.getNamedParameters(), select.rowMapper());
		}
		return jdbcTemplate.query(select.getQuery(), select.rowMapper(), select.getParameters());
	}

	@Override
	public Integer update(SqlStatement<?> update) {
		if (update instanceof NamedSqlStatement<?> named) {
			return namedJdbcTemplate.update(update.getQuery(), named.getNamedParameters());
		}
		return jdbcTemplate.update(update.getQuery(), update.getParameters());
	}

	@Override
	public void call(String procedure) {
		jdbcTemplate.update(procedure);
	}

	@Override
	public <T> void stream(SqlStatement<T> statement, Consumer<T> rowConsumer) {
		if (statement instanceof NamedSqlStatement<?> named) {
			namedJdbcTemplate.query(statement.getQuery(), named.getNamedParameters(), rs -> {
				rowConsumer.accept(statement.rowMapper().mapRow(rs, rs.getRow()));
			});
		} else {
			jdbcTemplate.query(statement.getQuery(), rs -> {
				rowConsumer.accept(statement.rowMapper().mapRow(rs, rs.getRow()));
			}, statement.getParameters());
		}
	}
}
