package com.druvu.lib.jdbc;

import java.util.List;
import java.util.Optional;

import com.druvu.lib.jdbc.internal.OptionalUtils;

/**
 * Represents database communication interface
 *
 * By contract promises to call the statements without transaction.
 * Caller responsibility is to have or not to have transactions around.
 *
 * @author Deniss Larka
 * <br/>on 11 Nov 2020
 */
public interface DbAccessDirect {

	<T> List<T> select(SqlStatement<T> select);

	/**
	 * Selects a single result, expecting at most one row.
	 *
	 * @param select the statement to execute
	 * @param <T> the result type
	 * @return Optional containing the result, or empty if no rows
	 * @throws IllegalStateException if more than one row is returned
	 */
	default <T> Optional<T> selectOne(SqlStatement<T> select) {
		return OptionalUtils.uniqueOpt(select(select));
	}

	/**
	 * Selects the first result from the query.
	 * <p>
	 * Use this when you expect multiple rows but only need the first one.
	 * Consider adding ORDER BY to ensure deterministic results.
	 *
	 * @param select the statement to execute
	 * @param <T> the result type
	 * @return Optional containing the first result, or empty if no rows
	 */
	default <T> Optional<T> selectFirst(SqlStatement<T> select) {
		return OptionalUtils.from(select(select));
	}

	Integer update(SqlStatement<?> update);

	void call(String procedure);
}
