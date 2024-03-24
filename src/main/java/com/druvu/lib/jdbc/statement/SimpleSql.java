package com.druvu.lib.jdbc.statement;

import com.druvu.lib.jdbc.util.SqlLoader;

/**
 * Creates {@link SimpleSqlStatement} from different sources
 *
 * @author Deniss Larka
 * at 09 Jan 2020
 */
public final class SimpleSql {

	private SimpleSql() {
	}

	/**
	 * Loading statement from string template replacing %s placeholders with the resources provided in args
	 * This method is preferable to the {@link #fromFile(String)} as it is not depends on working directory
	 */
	public static SimpleSqlStatement fromResource(String mainPath, String... includePaths) {
		return new SimpleSqlStatement(SqlLoader.load(mainPath, includePaths));
	}

	/**
	 *
	 */
	public static SimpleSqlStatement fromFile(String mainPath) {
		return new SimpleSqlStatement(SqlLoader.loadFromFile(mainPath));
	}

	public static SimpleSqlStatement fromString(String query) {
		return new SimpleSqlStatement(query);
	}

}
