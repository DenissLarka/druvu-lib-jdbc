package com.druvu.lib.jdbc.test;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.druvu.lib.jdbc.DbAccess;
import com.druvu.lib.jdbc.DbAccessFactory;
import com.druvu.lib.jdbc.DbConfig;
import com.druvu.lib.jdbc.statement.SimpleSql;
import com.druvu.lib.jdbc.statement.SimpleSqlStatement;

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

	private DbAccess createDb() {

		final DbConfig config = DbConfig.of("testDb",
				"jdbc:h2:mem:mockChanges;MODE=PostgreSQL",
				"sa",
				"",
				"org.h2.Driver",
				"select 1 from dual");

		final DbAccess support = DbAccessFactory.create(config);

		support.update(new SimpleSqlStatement("DROP ALL OBJECTS"));
		support.update(new SimpleSqlStatement("CREATE TABLE TABLE1 (ID_COL INT NOT NULL PRIMARY KEY, FIRST_COL VARCHAR2(20))"));
		support.update(new SimpleSqlStatement("INSERT INTO TABLE1 (ID_COL, FIRST_COL) VALUES (1,'value')"));
		return support;
	}

}