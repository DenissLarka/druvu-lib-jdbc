package com.druvu.lib.jdbc.examples;

import com.druvu.lib.jdbc.DbConfig;

/**
 * @author Deniss Larka
 * on 11 Apr 2024
 */
public class ExampleBase {


	protected DbConfig createConfig() {
		return DbConfig.of("testDb",
				"jdbc:h2:mem:mockChanges;MODE=PostgreSQL",
				"sa",
				"",
				"org.h2.Driver",
				"select 1 from dual");
	}
}
