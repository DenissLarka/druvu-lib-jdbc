package com.druvu.lib.jdbc.examples;

import java.util.Objects;

import com.druvu.lib.jdbc.statement.SqlStatement;
import com.druvu.lib.jdbc.util.SqlLoader;

/**
 * @author Deniss Larka
 * on 26 Mar 2024
 */
public class InsertStatement1 extends SqlStatement<Entity1> {

	private final int id;
	private final String newValue;

	public InsertStatement1(int id, String newValue) {
		super(new Entity1Mapper());
		this.id = id;
		this.newValue = Objects.requireNonNull(newValue);
	}
	@Override
	public Object[] getParameters() {
		return new Object[] {id, newValue};
	}

	@Override
	public String getQuery() {
		return SqlLoader.load("sql/examples/insert-table1.sql");
	}
}
