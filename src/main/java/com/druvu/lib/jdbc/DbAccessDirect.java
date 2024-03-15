package com.druvu.lib.jdbc;

import java.util.List;

import com.druvu.lib.jdbc.statement.SqlStatement;

/**
 * Represents database communication interface
 *
 * By contract promises to call the statements without transaction.
 * Caller responsibility is to have or not to have transactions around.
 *
 * @author Deniss Larka
 * at 11 Nov 2020
 */
public interface DbAccessDirect {

	<T> List<T> select(SqlStatement<T> select);

	Integer update(SqlStatement<?> update);

	void call(String procedure);
}
