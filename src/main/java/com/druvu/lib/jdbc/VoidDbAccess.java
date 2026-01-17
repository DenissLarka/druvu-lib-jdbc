package com.druvu.lib.jdbc;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


/**
 * @author Deniss Larka
 * on 08 Feb 2023
 */
public class VoidDbAccess implements DbAccess {

	private final String id;

	public VoidDbAccess(String id) {
		this.id = Objects.requireNonNull(id);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public <T> List<T> inTransaction(Function<DbAccessDirect, List<T>> statement) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> select(SqlStatement<T> select) {
		return Collections.emptyList();
	}

	@Override
	public Integer update(SqlStatement<?> update) {
		return 0;
	}

	@Override
	public void call(String procedure) {
		//
	}

}
