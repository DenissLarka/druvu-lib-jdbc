package com.druvu.lib.jdbc.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.druvu.lib.jdbc.DbAccess;
import com.druvu.lib.jdbc.DbAccessFactory;
import com.druvu.lib.jdbc.DbConfig;
import com.druvu.lib.jdbc.SimpleSql;

/**
 * @author Deniss Larka
 * on 11 Jul 2022
 */
public class TestDbAccess {

	private DbAccess dbAccess;

	@BeforeMethod
	public void init() {
		this.dbAccess = createDb();
	}

	@AfterMethod
	public void deInit() {
		dbAccess = null;
	}

	@Test
	public void test1() {

		final List<Map<String, Object>> select = dbAccess.select(SimpleSql.fromString("SELECT * FROM TABLE1"));
		Assert.assertEquals(select.size(), 1);

	}

	@Test
	public void testSelectOneReturnsResult() {
		Optional<Map<String, Object>> result = dbAccess.selectOne(
				SimpleSql.fromString("SELECT * FROM TABLE1 WHERE ID_COL = ?").with(1));

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(result.get().get("FIRST_COL"), "value");
	}

	@Test
	public void testSelectOneReturnsEmptyWhenNoRows() {
		Optional<Map<String, Object>> result = dbAccess.selectOne(
				SimpleSql.fromString("SELECT * FROM TABLE1 WHERE ID_COL = ?").with(999));

		Assert.assertTrue(result.isEmpty());
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void testSelectOneThrowsWhenMultipleRows() {
		// Insert another row
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (2, 'value2')"));

		// Should throw because more than one row
		dbAccess.selectOne(SimpleSql.fromString("SELECT * FROM TABLE1"));
	}

	@Test
	public void testSelectFirstReturnsFirstRow() {
		// Insert more rows
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (2, 'value2')"));
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (3, 'value3')"));

		Optional<Map<String, Object>> result = dbAccess.selectFirst(
				SimpleSql.fromString("SELECT * FROM TABLE1 ORDER BY ID_COL"));

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(result.get().get("ID_COL"), 1);
	}

	@Test
	public void testSelectFirstReturnsEmptyWhenNoRows() {
		Optional<Map<String, Object>> result = dbAccess.selectFirst(
				SimpleSql.fromString("SELECT * FROM TABLE1 WHERE ID_COL = ?").with(999));

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testScalarCount() {
		Integer count = dbAccess.selectOne(SimpleSql.scalar("SELECT COUNT(*) FROM TABLE1", Integer.class))
				.orElse(0);

		Assert.assertEquals(count.intValue(), 1);
	}

	@Test
	public void testScalarCountWithParams() {
		// Insert more rows
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (2, 'value2')"));

		Integer count = dbAccess.selectOne(
				SimpleSql.scalar("SELECT COUNT(*) FROM TABLE1 WHERE ID_COL > ?", Integer.class).with(0))
				.orElse(0);

		Assert.assertEquals(count.intValue(), 2);
	}

	@Test
	public void testScalarString() {
		String value = dbAccess.selectOne(
				SimpleSql.scalar("SELECT FIRST_COL FROM TABLE1 WHERE ID_COL = ?", String.class).with(1))
				.orElse(null);

		Assert.assertEquals(value, "value");
	}

	@Test
	public void testScalarLong() {
		Long id = dbAccess.selectOne(
				SimpleSql.scalar("SELECT ID_COL FROM TABLE1 WHERE FIRST_COL = ?", Long.class).with("value"))
				.orElse(null);

		Assert.assertEquals(id.longValue(), 1L);
	}

	@Test
	public void testScalarReturnsEmptyWhenNoRows() {
		Optional<Integer> result = dbAccess.selectOne(
				SimpleSql.scalar("SELECT ID_COL FROM TABLE1 WHERE ID_COL = ?", Integer.class).with(999));

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testQueryWithLambdaMapper() {
		List<TestEntity> results = dbAccess.select(
				SimpleSql.query("SELECT ID_COL, FIRST_COL FROM TABLE1",
						(rs, rowNum) -> new TestEntity(rs.getInt("ID_COL"), rs.getString("FIRST_COL"))));

		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).id, 1);
		Assert.assertEquals(results.get(0).name, "value");
	}

	@Test
	public void testQueryWithLambdaMapperAndParams() {
		// Insert more rows
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (2, 'value2')"));
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (3, 'value3')"));

		List<TestEntity> results = dbAccess.select(
				SimpleSql.query("SELECT ID_COL, FIRST_COL FROM TABLE1 WHERE ID_COL > ? ORDER BY ID_COL",
						(rs, rowNum) -> new TestEntity(rs.getInt("ID_COL"), rs.getString("FIRST_COL")))
						.with(1));

		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(results.get(0).id, 2);
		Assert.assertEquals(results.get(1).id, 3);
	}

	@Test
	public void testQueryWithSelectOne() {
		Optional<TestEntity> result = dbAccess.selectOne(
				SimpleSql.query("SELECT ID_COL, FIRST_COL FROM TABLE1 WHERE ID_COL = ?",
						(rs, rowNum) -> new TestEntity(rs.getInt("ID_COL"), rs.getString("FIRST_COL")))
						.with(1));

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(result.get().id, 1);
		Assert.assertEquals(result.get().name, "value");
	}

	@Test
	public void testNamedParametersSelect() {
		List<Map<String, Object>> results = dbAccess.select(
				SimpleSql.named("SELECT * FROM TABLE1 WHERE ID_COL = :id")
						.with("id", 1));

		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).get("FIRST_COL"), "value");
	}

	@Test
	public void testNamedParametersMultipleParams() {
		// Insert more rows
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (2, 'value2')"));
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (3, 'other')"));

		List<Map<String, Object>> results = dbAccess.select(
				SimpleSql.named("SELECT * FROM TABLE1 WHERE ID_COL > :minId AND FIRST_COL LIKE :pattern ORDER BY ID_COL")
						.with("minId", 0)
						.with("pattern", "value%"));

		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(results.get(0).get("ID_COL"), 1);
		Assert.assertEquals(results.get(1).get("ID_COL"), 2);
	}

	@Test
	public void testNamedParametersWithSelectOne() {
		Optional<Map<String, Object>> result = dbAccess.selectOne(
				SimpleSql.named("SELECT * FROM TABLE1 WHERE ID_COL = :id")
						.with("id", 1));

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(result.get().get("FIRST_COL"), "value");
	}

	@Test
	public void testNamedParametersUpdate() {
		dbAccess.update(SimpleSql.named("UPDATE TABLE1 SET FIRST_COL = :newValue WHERE ID_COL = :id")
				.with("id", 1)
				.with("newValue", "updated"));

		String value = dbAccess.selectOne(
				SimpleSql.scalar("SELECT FIRST_COL FROM TABLE1 WHERE ID_COL = ?", String.class).with(1))
				.orElse(null);

		Assert.assertEquals(value, "updated");
	}

	@Test
	public void testNamedParametersInsert() {
		dbAccess.update(SimpleSql.named("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (:id, :value)")
				.with("id", 99)
				.with("value", "new"));

		String value = dbAccess.selectOne(
				SimpleSql.scalar("SELECT FIRST_COL FROM TABLE1 WHERE ID_COL = ?", String.class).with(99))
				.orElse(null);

		Assert.assertEquals(value, "new");
	}

	@Test
	public void testNamedParametersWithMapper() {
		Optional<TestEntity> result = dbAccess.selectOne(
				SimpleSql.named("SELECT ID_COL, FIRST_COL FROM TABLE1 WHERE ID_COL = :id")
						.with("id", 1)
						.map((rs, rowNum) -> new TestEntity(rs.getInt("ID_COL"), rs.getString("FIRST_COL"))));

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(result.get().id, 1);
		Assert.assertEquals(result.get().name, "value");
	}

	@Test
	public void testNamedParametersWithMapParams() {
		List<Map<String, Object>> results = dbAccess.select(
				SimpleSql.named("SELECT * FROM TABLE1 WHERE ID_COL = :id")
						.with(Map.of("id", 1)));

		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).get("FIRST_COL"), "value");
	}

	@Test
	public void testStreamProcessesAllRows() {
		// Insert more rows
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (2, 'value2')"));
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (3, 'value3')"));

		List<TestEntity> collected = new ArrayList<>();
		dbAccess.stream(
				SimpleSql.query("SELECT ID_COL, FIRST_COL FROM TABLE1 ORDER BY ID_COL",
						(rs, rowNum) -> new TestEntity(rs.getInt("ID_COL"), rs.getString("FIRST_COL"))),
				collected::add);

		Assert.assertEquals(collected.size(), 3);
		Assert.assertEquals(collected.get(0).id, 1);
		Assert.assertEquals(collected.get(1).id, 2);
		Assert.assertEquals(collected.get(2).id, 3);
	}

	@Test
	public void testStreamWithParameters() {
		// Insert more rows
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (2, 'value2')"));
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (3, 'value3')"));

		List<TestEntity> collected = new ArrayList<>();
		dbAccess.stream(
				SimpleSql.query("SELECT ID_COL, FIRST_COL FROM TABLE1 WHERE ID_COL > ? ORDER BY ID_COL",
						(rs, rowNum) -> new TestEntity(rs.getInt("ID_COL"), rs.getString("FIRST_COL")))
						.with(1),
				collected::add);

		Assert.assertEquals(collected.size(), 2);
		Assert.assertEquals(collected.get(0).id, 2);
		Assert.assertEquals(collected.get(1).id, 3);
	}

	@Test
	public void testStreamWithNamedParameters() {
		// Insert more rows
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (2, 'value2')"));
		dbAccess.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (3, 'value3')"));

		List<TestEntity> collected = new ArrayList<>();
		dbAccess.stream(
				SimpleSql.named("SELECT ID_COL, FIRST_COL FROM TABLE1 WHERE ID_COL > :minId ORDER BY ID_COL")
						.with("minId", 1)
						.map((rs, rowNum) -> new TestEntity(rs.getInt("ID_COL"), rs.getString("FIRST_COL"))),
				collected::add);

		Assert.assertEquals(collected.size(), 2);
		Assert.assertEquals(collected.get(0).id, 2);
		Assert.assertEquals(collected.get(1).id, 3);
	}

	@Test
	public void testStreamEmptyResult() {
		List<TestEntity> collected = new ArrayList<>();
		dbAccess.stream(
				SimpleSql.query("SELECT ID_COL, FIRST_COL FROM TABLE1 WHERE ID_COL = ?",
						(rs, rowNum) -> new TestEntity(rs.getInt("ID_COL"), rs.getString("FIRST_COL")))
						.with(999),
				collected::add);

		Assert.assertTrue(collected.isEmpty());
	}

	private static class TestEntity {
		final int id;
		final String name;

		TestEntity(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	private DbAccess createDb() {

		final DbConfig config = DbConfig.of("testDb",
				"jdbc:h2:mem:mockChanges;MODE=PostgreSQL",
				"sa",
				"",
				"org.h2.Driver",
				"select 1 from dual");

		final DbAccess support = DbAccessFactory.create(config);

		support.update(SimpleSql.fromString("DROP ALL OBJECTS"));
		support.update(SimpleSql.fromString("CREATE TABLE TABLE1 (ID_COL INT NOT NULL PRIMARY KEY, FIRST_COL VARCHAR2(20))"));
		support.update(SimpleSql.fromString("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (1,'value')"));
		return support;
	}

}