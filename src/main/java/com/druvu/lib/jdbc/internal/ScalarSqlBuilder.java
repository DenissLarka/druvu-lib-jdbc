package com.druvu.lib.jdbc.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.druvu.lib.jdbc.SqlStatement;

/**
 * Fluent builder for scalar (single-value) SQL queries.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Count query
 * int count = db.selectOne(SimpleSql.scalar("SELECT COUNT(*) FROM users", Integer.class))
 *     .orElse(0);
 *
 * // With parameters
 * String name = db.selectOne(SimpleSql.scalar("SELECT name FROM users WHERE id = ?", String.class)
 *     .with(userId))
 *     .orElseThrow();
 * }</pre>
 *
 * @param <T> the scalar result type
 * @author Deniss Larka
 */
public class ScalarSqlBuilder<T> extends SqlStatement<T> {

	private final String query;
	private final List<Object> parameters;

	public ScalarSqlBuilder(String query, Class<T> type) {
		super(new SingleColumnRowMapper<>(type));
		this.query = Objects.requireNonNull(query);
		this.parameters = new ArrayList<>();
	}

	/**
	 * Adds parameters to the statement in order.
	 *
	 * @param args parameters to bind to ? placeholders
	 * @return this builder for chaining
	 */
	public ScalarSqlBuilder<T> with(Object... args) {
		if (args != null) {
			Collections.addAll(parameters, args);
		}
		return this;
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public Object[] getParameters() {
		return parameters.toArray();
	}

}
