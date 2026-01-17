package com.druvu.lib.jdbc;

import java.util.List;
import java.util.function.Function;

/**
 * Represents database communication interface
 *
 * By contract promises to call all statements (also using inherited methods) in transaction
 *
 * @author Deniss Larka
 * <br/> on 11 Nov 2020
 */
public interface DbAccess extends DbAccessDirect {

	String getId();

	<T> List<T> inTransaction(Function<DbAccessDirect, List<T>> statement);

}
