package com.druvu.lib.jdbc.examples;

import java.util.List;
import java.util.Map;

import com.druvu.lib.jdbc.DbAccess;
import com.druvu.lib.jdbc.DbAccessDirect;
import com.druvu.lib.jdbc.DbAccessFactory;
import com.druvu.lib.jdbc.DbConfig;
import com.druvu.lib.jdbc.statement.SimpleSqlStatement;
import com.druvu.lib.jdbc.util.OptionalUtils;
import com.druvu.lib.jdbc.util.SqlLoader;

/**
 * @author Deniss Larka
 * on 26 Mar 2024
 */
public class ExampleInTransaction extends ExampleBase {

	public static void main(String[] args) {
		ExampleInTransaction exampleInTransaction = new ExampleInTransaction();
		exampleInTransaction.example();
	}

	public void example() {
		final DbConfig config = createConfig();
		//create pool, connections etc.
		final DbAccess access = DbAccessFactory.create(config);
		fillFewLines(access);
		access.inTransaction(this::inTransaction);
		//Statements are first class citizens in opposite to ORM frameworks where entities are.
	}

	private void fillFewLines(DbAccess access) {
		//loadBulk splits sql by lines and feed it to consumer - dbAccess
		SqlLoader.loadBulk("sql/examples/create-and-fill-table1.sql", access::update);
	}

	private List<Object> inTransaction(DbAccessDirect access) {
		int sequence = sequence(access.select(new SimpleSqlStatement("SELECT nextval('SEQ1') AS ID")));
		access.update(new InsertStatement1(sequence, "yes!"));
		return null;
	}

	private int sequence(List<Map<String, Object>> nextVal) {
		final Map<String, Object> map = OptionalUtils.uniqueItem(nextVal);
		return (int) map.get("ID");
	}



}
