package com.druvu.lib.jdbc.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.druvu.lib.jdbc.SqlStatement;

/**
 * Fluent builder for creating SQL statements with parameters.
 *
 * <p>Can be used directly where SqlStatement is expected (no need to call build()):
 * <pre>{@code
 * db.select(SimpleSql.fromString("SELECT * FROM users WHERE id = ?").with(userId));
 * }</pre>
 *
 * <p>Or with a custom mapper:
 * <pre>{@code
 * db.select(SimpleSql.fromString("SELECT id, name FROM users WHERE status = ?")
 *     .with("active")
 *     .map((rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name"))));
 * }</pre>
 *
 * @author Deniss Larka
 * <br/>on 09 Jan 2021
 */
public class SimpleSqlBuilder extends SqlStatement<Map<String, Object>> {

	private final String query;
	private final List<Object> parameters;

	public SimpleSqlBuilder(String query) {
		super(new ColumnMapRowMapper());
		this.query = Objects.requireNonNull(query);
		this.parameters = new ArrayList<>();
	}

	/**
	 * Adds parameters to the statement in order.
	 *
	 * @param args parameters to bind to ? placeholders
	 * @return this builder for chaining
	 */
	public SimpleSqlBuilder with(Object... args) {
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

	/**
	 * Builds a typed SqlStatement with a custom RowMapper.
	 *
	 * @param mapper the row mapper to convert result rows
	 * @param <T> the result type
	 * @return typed statement
	 */
	public <T> SqlStatement<T> map(RowMapper<T> mapper) {
		return new SqlStatement<>(mapper, query, parameters);
	}

}
