# SQL First JDBC

Work with SQL directly, skip the ORM complexity.

A lightweight JDBC wrapper built on [Spring JDBC](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/package-summary.html) where statements are first-class citizens.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Features

- Fluent API for building SQL statements
- Positional (`?`) and named (`:param`) parameter support
- SQL loading from strings, resources, and files
- SQL composition with `%s` includes
- Dynamic parameter expansion (`???` → `?,?,?`)
- Lambda-based row mappers
- Built-in transaction support
- Optional result handling with `selectOne()` / `selectFirst()`
- Row-by-row streaming for large result sets



## Quick Start

```java
// Create database access
DbConfig config = DbConfig.of("myDb",
    "jdbc:postgresql://localhost/mydb", "user", "pass",
    "org.postgresql.Driver", "SELECT 1");
DbAccess db = DbAccessFactory.create(config);

// Simple query
List<Map<String, Object>> users = db.select(
    SimpleSql.fromString("SELECT * FROM users WHERE status = ?").with("active"));

// Get single result
Optional<Map<String, Object>> user = db.selectOne(
    SimpleSql.fromString("SELECT * FROM users WHERE id = ?").with(userId));
```



## API Reference

### Creating Statements

| Method | Description |
|--------|-------------|
| `SimpleSql.fromString(sql)` | Create from SQL string with positional `?` params |
| `SimpleSql.fromResource(path)` | Load SQL from classpath resource |
| `SimpleSql.fromFile(path)` | Load SQL from file system |
| `SimpleSql.named(sql)` | Create with named `:param` parameters |
| `SimpleSql.scalar(sql, type)` | Single-value queries (COUNT, MAX, etc.) |
| `SimpleSql.query(sql, mapper)` | Typed query with custom RowMapper |

### Executing Statements

| Method | Description |
|--------|-------------|
| `db.select(statement)` | Returns `List<T>` |
| `db.selectOne(statement)` | Returns `Optional<T>`, throws if > 1 row |
| `db.selectFirst(statement)` | Returns `Optional<T>` (first row only) |
| `db.stream(statement, consumer)` | Process rows one-by-one (memory efficient) |
| `db.update(statement)` | Returns affected row count |
| `db.inTransaction(fn)` | Execute multiple statements in transaction |



## Examples

### Positional Parameters

```java
// Fluent parameter binding
db.select(SimpleSql.fromString("SELECT * FROM users WHERE status = ? AND role = ?")
    .with("active", "admin"));

// Chained binding
db.select(SimpleSql.fromString("SELECT * FROM orders WHERE user_id = ? AND total > ?")
    .with(userId)
    .with(minTotal));
```

### Named Parameters

```java
// Named parameters - clearer for complex queries
db.select(SimpleSql.named("SELECT * FROM users WHERE id = :id AND status = :status")
    .with("id", userId)
    .with("status", "active"));

// Using Map
db.select(SimpleSql.named("SELECT * FROM users WHERE id = :id")
    .with(Map.of("id", userId)));
```

### Scalar Queries

```java
// Count
int count = db.selectOne(SimpleSql.scalar("SELECT COUNT(*) FROM users", Integer.class))
    .orElse(0);

// Single value with parameter
String name = db.selectOne(
    SimpleSql.scalar("SELECT name FROM users WHERE id = ?", String.class).with(userId))
    .orElseThrow();
```

### Custom Row Mappers (Lambda)

```java
// Inline lambda mapper - no separate class needed
List<User> users = db.select(
    SimpleSql.query("SELECT id, name, email FROM users WHERE status = ?",
        (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email")))
    .with("active"));

// With named parameters
Optional<User> user = db.selectOne(
    SimpleSql.named("SELECT * FROM users WHERE id = :id")
        .with("id", userId)
        .map((rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name"))));
```

### Loading SQL from Resources

```java
// Load from classpath resource
db.select(SimpleSql.fromResource("sql/find-users.sql").with(status));

// With SQL includes using %s placeholders
// main.sql: SELECT * FROM users WHERE status = ? %s
// filter.sql: AND role = ?
db.select(SimpleSql.fromResource("sql/main.sql", "sql/filter.sql")
    .with("active", "admin"));
```

### Dynamic IN Clause

```java
// ??? expands to match parameter count
List<Integer> ids = List.of(1, 2, 3, 4, 5);
String sql = MultiParam.replace("SELECT * FROM users WHERE id IN (???)", ids.size());
// Result: SELECT * FROM users WHERE id IN (?,?,?,?,?)

db.select(SimpleSql.fromString(sql).with(ids.toArray()));
```

### Streaming Large Result Sets

```java
// Process rows one-by-one without loading entire result set into memory
db.stream(
    SimpleSql.query("SELECT * FROM large_table", (rs, rowNum) -> new Record(rs.getInt("id"), rs.getString("data"))),
    record -> {
        process(record);  // Called for each row
    });

// With parameters
db.stream(
    SimpleSql.query("SELECT * FROM events WHERE date > ?",
        (rs, rowNum) -> new Event(rs.getInt("id"), rs.getString("name")))
        .with(startDate),
    event -> exportToFile(event));
```

### Transactions

```java
// Execute multiple statements in a transaction (no return value)
db.runInTransaction(tx -> {
    tx.update(SimpleSql.fromString("UPDATE accounts SET balance = balance - ? WHERE id = ?").with(amount, fromAccount));
    tx.update(SimpleSql.fromString("UPDATE accounts SET balance = balance + ? WHERE id = ?").with(amount, toAccount));
});

// Or with a return value
List<Account> updated = db.inTransaction(tx -> {
    tx.update(SimpleSql.fromString("UPDATE accounts SET balance = balance - ? WHERE id = ?").with(amount, fromAccount));
    tx.update(SimpleSql.fromString("UPDATE accounts SET balance = balance + ? WHERE id = ?").with(amount, toAccount));
    return tx.select(SimpleSql.fromString("SELECT * FROM accounts WHERE id IN (?, ?)").with(fromAccount, toAccount));
});
```

### Bulk SQL Execution

```java
// Execute multiple statements from a file (split by ;)
SqlLoader.loadBulk("sql/schema.sql", db::update);
```



## Utilities

### Debugging SQL

Get the filled-in SQL for logging/debugging:

```java
var stmt = SimpleSql.fromString("SELECT * FROM users WHERE id = ?").with(42);
log.debug("Executing: {}", stmt.toDebugString());
// Output: SELECT * FROM users WHERE id = 42
```

### ArrayUtils

Flatten nested collections for IN clauses:

```java
Object[] params = ArrayUtils.flat(1, Arrays.asList(2, 3), 4).toArray();
// Result: [1, 2, 3, 4]
```

## Configuration

```java
DbConfig config = DbConfig.of(
    "connectionId",           // Unique identifier
    "jdbc:postgresql://...",  // JDBC URL
    "username",               // Database user
    "password",               // Database password
    "org.postgresql.Driver",  // Driver class
    "SELECT 1"                // Validation query
);
```

Connection pooling is handled via Tomcat JDBC Pool with sensible defaults.

## Module Structure (JPMS)

```java
module com.druvu.lib.jdbc {
    exports com.druvu.lib.jdbc;      // Core API
    exports com.druvu.lib.jdbc.util; // Utilities
}
```

Import the main classes from `com.druvu.lib.jdbc`:

```java
import com.druvu.lib.jdbc.DbAccess;
import com.druvu.lib.jdbc.DbConfig;
import com.druvu.lib.jdbc.DbAccessFactory;
import com.druvu.lib.jdbc.SimpleSql;
import com.druvu.lib.jdbc.SqlStatement;
```

## Installation

This library is published to **GitHub Packages**. To use it, you need to configure Maven authentication.

**1. Generate a GitHub Personal Access Token:**

Go to [GitHub Settings > Developer settings > Personal access tokens](https://github.com/settings/tokens) and create a token with the `read:packages` scope.

**2. Add the server to `~/.m2/settings.xml`:**

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

**3. Add the repository and dependency to your project `pom.xml`:**

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/DenissLarka/druvu-lib-jdbc</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.druvu</groupId>
    <artifactId>druvu-lib-jdbc</artifactId>
    <version>1.0.0</version>
</dependency>
```

## License

Apache License 2.0
