/**
 * druvu-lib-jdbc - A lightweight JDBC wrapper built on Spring JDBC.
 *
 * <p>Public API packages:
 * <ul>
 *   <li>{@code com.druvu.lib.jdbc} - Core API (DbAccess, SimpleSql, SqlStatement)</li>
 *   <li>{@code com.druvu.lib.jdbc.util} - Utilities (SqlLoader, SqlDebug, ArrayUtils, MultiParam)</li>
 * </ul>
 *
 * <p>The {@code com.druvu.lib.jdbc.internal} package contains implementation details
 * and is not part of the public API.
 *
 * @author Deniss Larka
 * on 15 Mar 2024
 */
module com.druvu.lib.jdbc {
	//SQL
	requires java.sql;
	//SPRING JDBC and TX
	requires transitive spring.jdbc;
	requires spring.tx;
	//TOMCAT POOL
	requires tomcat.jdbc;
	//SLF4J
	requires org.slf4j;
	//LOMBOK
	requires static lombok;

	// Public API
	exports com.druvu.lib.jdbc;
	exports com.druvu.lib.jdbc.util;

	// Internal package NOT exported - implementation details
}
