package com.druvu.lib.jdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.RowMapper;

import com.druvu.lib.jdbc.internal.ThrowingMapper;
import com.druvu.lib.jdbc.util.SqlDebug;

/**
 * Base class for SQL statements with type-safe result mapping.
 *
 * @param <T> the result type
 *
 * @author Deniss Larka
 * <br/>on 29 May 2020
 */
public class SqlStatement<T> {

	private final RowMapper<T> rowMapper;
	private final String query;
	private final List<Object> arguments;

	public SqlStatement() {
		this(Collections.emptyList(), new ThrowingMapper<>(), "");
	}

	//in all public constructors validation happens in the argument expressions, before Object's
	//constructor runs: a failing new SqlStatement never becomes reachable by a finalizer
	//(CT_CONSTRUCTOR_THROW); the private constructor itself never throws
	public SqlStatement(RowMapper<T> rowMapper, String query, List<Object> arguments) {
		//not List.copyOf: null elements are legitimate (SQL NULL) and List.copyOf rejects them
		this(Collections.unmodifiableList(new ArrayList<>(arguments)), Objects.requireNonNull(rowMapper), Objects.requireNonNull(query));
	}

	public SqlStatement(RowMapper<T> rowMapper, String query) {
		this(Collections.emptyList(), Objects.requireNonNull(rowMapper), Objects.requireNonNull(query));
	}

	public SqlStatement(RowMapper<T> rowMapper) {
		this(Collections.emptyList(), Objects.requireNonNull(rowMapper), "");
	}

	private SqlStatement(List<Object> arguments, RowMapper<T> rowMapper, String query) {
		this.rowMapper = rowMapper;
		this.query = query;
		this.arguments = arguments;
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

	/**
	 * Returns the SQL query with parameter placeholders filled in with actual values.
	 * Useful for debugging and logging.
	 *
	 * @return the SQL string with parameters substituted
	 */
	public String toDebugString() {
		return SqlDebug.debug(this);
	}

	@Override
	public String toString() {
		return getQuery();
	}
}
