package com.druvu.lib.jdbc.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.RowMapper;

import com.druvu.lib.jdbc.SqlStatement;

/**
 * Fluent builder for typed SQL statements with a custom RowMapper.
 *
 * <p>Example usage:
 * <pre>{@code
 * db.select(SimpleSql.query("SELECT id, name FROM users WHERE status = ?",
 *         (rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name")))
 *     .with("active"));
 * }</pre>
 *
 * @param <T> the result type
 * @author Deniss Larka
 */
public class TypedSqlBuilder<T> extends SqlStatement<T> {

	private final String query;
	private final List<Object> parameters;

	public TypedSqlBuilder(String query, RowMapper<T> mapper) {
		super(mapper);
		this.query = Objects.requireNonNull(query);
		this.parameters = new ArrayList<>();
	}

	/**
	 * Adds parameters to the statement in order.
	 *
	 * @param args parameters to bind to ? placeholders
	 * @return this builder for chaining
	 */
	public TypedSqlBuilder<T> with(Object... args) {
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
