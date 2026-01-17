package com.druvu.lib.jdbc.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.druvu.lib.jdbc.SqlStatement;

/**
 * Fluent builder for SQL statements with named parameters.
 *
 * <p>Example usage:
 * <pre>{@code
 * db.select(SimpleSql.named("SELECT * FROM users WHERE id = :id AND status = :status")
 *     .with("id", userId)
 *     .with("status", "active"));
 *
 * // Or with multiple params at once
 * db.select(SimpleSql.named("SELECT * FROM users WHERE id = :id")
 *     .with(Map.of("id", userId)));
 * }</pre>
 *
 * @author Deniss Larka
 */
public class NamedSqlBuilder extends SqlStatement<Map<String, Object>> implements NamedSqlStatement<Map<String, Object>> {

	private final String query;
	private final Map<String, Object> parameters;

	public NamedSqlBuilder(String query) {
		super(new ColumnMapRowMapper());
		this.query = Objects.requireNonNull(query);
		this.parameters = new LinkedHashMap<>();
	}

	/**
	 * Adds a named parameter.
	 *
	 * @param name the parameter name (without the colon prefix)
	 * @param value the parameter value
	 * @return this builder for chaining
	 */
	public NamedSqlBuilder with(String name, Object value) {
		parameters.put(name, value);
		return this;
	}

	/**
	 * Adds multiple named parameters from a map.
	 *
	 * @param params map of parameter names to values
	 * @return this builder for chaining
	 */
	public NamedSqlBuilder with(Map<String, Object> params) {
		if (params != null) {
			parameters.putAll(params);
		}
		return this;
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public Object[] getParameters() {
		// Not used for named statements, but required by parent
		return new Object[0];
	}

	@Override
	public Map<String, Object> getNamedParameters() {
		return Map.copyOf(parameters);
	}

	/**
	 * Builds a typed SqlStatement with a custom RowMapper.
	 *
	 * @param mapper the row mapper to convert each row
	 * @param <R> the result type
	 * @return typed named statement
	 */
	public <R> TypedNamedSqlBuilder<R> map(RowMapper<R> mapper) {
		TypedNamedSqlBuilder<R> typed = new TypedNamedSqlBuilder<>(query, mapper);
		typed.with(parameters);
		return typed;
	}

}
