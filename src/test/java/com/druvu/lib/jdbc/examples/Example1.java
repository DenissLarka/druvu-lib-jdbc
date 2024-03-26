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
public class Example1 {

	public static void main(String[] args) {
		Example1 example1 = new Example1();
		example1.example();
	}

	public void example() {

		final DbConfig config = createConfig();

		//create pool, connections etc.
		final DbAccess access = DbAccessFactory.create(config);

		//loadBulk splits sql by lines and feed it to consumer - dbAccess
		SqlLoader.loadBulk("sql/examples/create-and-fill-table1.sql", access::update);

		access.inTransaction(this::tr);

		//Statements are first class citizens in opposite to ORM frameworks where entities are.

	}

	private List<Object> tr(DbAccessDirect access) {
		int sequence = sequence(access.select(new SimpleSqlStatement("SELECT nextval('SEQ1') AS ID")));
		access.update(new InsertStatement1(sequence, "yes!"));
		return null;
	}

	private int sequence(List<Map<String, Object>> nextVal) {
		final Map<String, Object> map = OptionalUtils.uniqueItem(nextVal);
		return (int) map.get("ID");
	}

	private DbConfig createConfig() {
		return DbConfig.of("testDb",
				"jdbc:h2:mem:mockChanges;MODE=PostgreSQL",
				"sa",
				"",
				"org.h2.Driver",
				"select 1 from dual");
	}

}
