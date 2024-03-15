package com.druvu.lib.jdbc.statement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Deniss Larka
 * at 29 May 2020
 */
public class SqlStatement<T> {

	private final RowMapper<T> rowMapper;
	private final String query;
	private final List<Object> arguments;

	public SqlStatement() {
		this(new ThrowingMapper<>(), "", Collections.emptyList());
	}

	public SqlStatement(RowMapper<T> rowMapper, String query, List<Object> arguments) {
		this.rowMapper = Objects.requireNonNull(rowMapper);
		this.query = Objects.requireNonNull(query);
		this.arguments = List.copyOf(Objects.requireNonNull(arguments));
	}

	public SqlStatement(RowMapper<T> rowMapper, String query) {
		this(rowMapper, query, Collections.emptyList());
	}

	public SqlStatement(RowMapper<T> rowMapper) {
		this(rowMapper, "");
	}

	public String getQuery() {
		return query;
	}

	public Object[] getParameters() {
		return arguments.toArray();
	}

	public RowMapper<T> rowMapper() {
		return rowMapper;
	}

	@Override
	public String toString() {
		return getQuery();
	}
}
