package com.druvu.lib.jdbc.internal;

import java.util.Map;

/**
 * Marker interface for SQL statements with named parameters.
 * <p>
 * Implementations provide parameters as a Map instead of positional array.
 * Used by DbAccess to determine whether to use NamedParameterJdbcTemplate.
 *
 * @param <T> the result type
 * @author Deniss Larka
 */
public interface NamedSqlStatement<T> {

	/**
	 * Returns the named parameters as a Map.
	 *
	 * @return map of parameter names to values
	 */
	Map<String, Object> getNamedParameters();

}
