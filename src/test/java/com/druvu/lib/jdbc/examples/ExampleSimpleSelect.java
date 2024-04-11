package com.druvu.lib.jdbc.examples;

import com.druvu.lib.jdbc.DbAccess;
import com.druvu.lib.jdbc.DbAccessFactory;
import com.druvu.lib.jdbc.DbConfig;
import com.druvu.lib.jdbc.statement.SimpleSql;

/**
 * @author Deniss Larka
 * on 11 Apr 2024
 */
public class ExampleSimpleSelect extends ExampleBase{

	public static void main(String[] args) {
		ExampleSimpleSelect example = new ExampleSimpleSelect();
		example.example();
	}

	private void example() {

		final DbConfig config = createConfig();
		//create pool, connections etc.
		final DbAccess access = DbAccessFactory.create(config);

		access.select(SimpleSql.fromString("DROP ALL OBJECTS"));

	}
}
