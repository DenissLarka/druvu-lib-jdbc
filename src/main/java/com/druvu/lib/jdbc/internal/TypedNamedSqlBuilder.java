package com.druvu.lib.jdbc.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.RowMapper;

import com.druvu.lib.jdbc.SqlStatement;

/**
 * Fluent builder for typed SQL statements with named parameters and custom RowMapper.
 *
 * <p>Example usage:
 * <pre>{@code
 * db.select(SimpleSql.named("SELECT id, name FROM users WHERE status = :status")
 *     .with("status", "active")
 *     .map((rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name"))));
 * }</pre>
 *
 * @param <T> the result type
 * @author Deniss Larka
 */
public class TypedNamedSqlBuilder<T> extends SqlStatement<T> implements NamedSqlStatement<T> {

	private final String query;
	private final Map<String, Object> parameters;

	public TypedNamedSqlBuilder(String query, RowMapper<T> mapper) {
		super(mapper);
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
	public TypedNamedSqlBuilder<T> with(String name, Object value) {
		parameters.put(name, value);
		return this;
	}

	/**
	 * Adds multiple named parameters from a map.
	 *
	 * @param params map of parameter names to values
	 * @return this builder for chaining
	 */
	public TypedNamedSqlBuilder<T> with(Map<String, Object> params) {
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

}
