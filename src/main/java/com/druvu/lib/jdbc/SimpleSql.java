package com.druvu.lib.jdbc;

import org.springframework.jdbc.core.RowMapper;

import com.druvu.lib.jdbc.internal.NamedSqlBuilder;
import com.druvu.lib.jdbc.internal.ScalarSqlBuilder;
import com.druvu.lib.jdbc.internal.SimpleSqlBuilder;
import com.druvu.lib.jdbc.internal.TypedSqlBuilder;
import com.druvu.lib.jdbc.util.SqlLoader;

import lombok.experimental.UtilityClass;

/**
 * Creates SQL statements from different sources for fluent statement building.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Simple query with parameters
 * db.select(SimpleSql.fromString("SELECT * FROM users WHERE id = ?").with(userId));
 *
 * // From resource file with parameters
 * db.select(SimpleSql.fromResource("sql/find-users.sql").with(status, limit));
 *
 * // With custom row mapper
 * db.select(SimpleSql.fromString("SELECT id, name FROM users")
 *     .map((rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name"))));
 * }</pre>
 *
 * @author Deniss Larka
 * at 09 Jan 2020
 */
@UtilityClass
public final class SimpleSql {

	/**
	 * Creates a builder from a resource file, replacing %s placeholders with included resources.
	 * <p>
	 * This method is preferable to {@link #fromFile(String)} as it does not depend on working directory.
	 *
	 * @param mainPath path to the main SQL resource
	 * @param includePaths paths to resources that replace %s placeholders
	 * @return builder for adding parameters and building the statement
	 */
	public static SimpleSqlBuilder fromResource(String mainPath, String... includePaths) {
		return new SimpleSqlBuilder(SqlLoader.load(mainPath, includePaths));
	}

	/**
	 * Creates a builder from a file path.
	 *
	 * @param mainPath absolute or relative file path
	 * @return builder for adding parameters and building the statement
	 */
	public static SimpleSqlBuilder fromFile(String mainPath) {
		return new SimpleSqlBuilder(SqlLoader.loadFromFile(mainPath));
	}

	/**
	 * Creates a builder from a SQL string.
	 *
	 * @param query the SQL query string
	 * @return builder for adding parameters and building the statement
	 */
	public static SimpleSqlBuilder fromString(String query) {
		return new SimpleSqlBuilder(query);
	}

	/**
	 * Creates a builder for scalar (single-value) queries.
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * int count = db.selectOne(SimpleSql.scalar("SELECT COUNT(*) FROM users", Integer.class))
	 *     .orElse(0);
	 *
	 * String name = db.selectOne(SimpleSql.scalar("SELECT name FROM users WHERE id = ?", String.class)
	 *     .with(userId))
	 *     .orElseThrow();
	 * }</pre>
	 *
	 * @param query the SQL query returning a single column
	 * @param type the expected result type (Integer.class, String.class, Long.class, etc.)
	 * @param <T> the scalar result type
	 * @return builder for adding parameters
	 */
	public static <T> ScalarSqlBuilder<T> scalar(String query, Class<T> type) {
		return new ScalarSqlBuilder<>(query, type);
	}

	/**
	 * Creates a typed builder with a custom RowMapper (supports lambdas).
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Inline lambda mapper
	 * db.select(SimpleSql.query("SELECT id, name FROM users WHERE status = ?",
	 *         (rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name")))
	 *     .with("active"));
	 *
	 * // Method reference
	 * db.select(SimpleSql.query("SELECT * FROM users", User::fromResultSet));
	 * }</pre>
	 *
	 * @param query the SQL query string
	 * @param mapper the row mapper to convert each row (can be a lambda)
	 * @param <T> the result type
	 * @return builder for adding parameters
	 */
	public static <T> TypedSqlBuilder<T> query(String query, RowMapper<T> mapper) {
		return new TypedSqlBuilder<>(query, mapper);
	}

	/**
	 * Creates a builder for SQL with named parameters.
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Named parameters
	 * db.select(SimpleSql.named("SELECT * FROM users WHERE id = :id AND status = :status")
	 *     .with("id", userId)
	 *     .with("status", "active"));
	 *
	 * // With custom row mapper
	 * db.select(SimpleSql.named("SELECT id, name FROM users WHERE status = :status")
	 *     .with("status", "active")
	 *     .map((rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name"))));
	 * }</pre>
	 *
	 * @param query the SQL query with named parameters (e.g., :paramName)
	 * @return builder for adding named parameters
	 */
	public static NamedSqlBuilder named(String query) {
		return new NamedSqlBuilder(query);
	}

}
