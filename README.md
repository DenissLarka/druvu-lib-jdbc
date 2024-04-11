# druvu-lib-jdbc


Based on [spring-jdbc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/package-summary.html) a lightweight API allows to easy working with relational data without ORM burden.

A database represents by [DbAccess](src/main/java/com/druvu/lib/jdbc/DbAccess.java) interface



Here are few examples:

## Simple select



## In transaction


```java

public void example() {
	final DbConfig config = createConfig();
	//create pool, connections etc.
	final DbAccess access = DbAccessFactory.create(config);
	//loadBulk splits sql by lines and feed it to consumer - dbAccess
	SqlLoader.loadBulk("sql/examples/create-and-fill-table1.sql", access::update);
	access.inTransaction(this::inTransaction);
}

private List<Object> inTransaction(DbAccessDirect access) {
	int sequence = sequence(access.select(new SimpleSqlStatement("SELECT nextval('SEQ1') AS ID")));
	access.update(new InsertStatement1(sequence, "yes!"));
	return null;
}

private DbConfig createConfig() {
		return DbConfig.of("testDb",
				"jdbc:h2:mem:mockChanges;MODE=PostgreSQL",
				"sa",
				"",
				"org.h2.Driver",
				"select 1 from dual");
	}
```

where [InsertStatement1](src/main/java/com/druvu/lib/jdbc/examples/InsertStatement1.java):

```java
public class InsertStatement1 extends SqlStatement<Entity1> {

	private final int id;
	private final String newValue;

	public InsertStatement1(int id, String newValue) {
		super(new Entity1Mapper());
		this.id = id;
		this.newValue = Objects.requireNonNull(newValue);
	}
	@Override
	public Object[] getParameters() {
		return new Object[] {id, newValue};
	}

	@Override
	public String getQuery() {
		return SqlLoader.load("sql/examples/insert-table1.sql");
	}
}
```



where create-and-fill-table1.sql is:

```sql
CREATE SEQUENCE SEQ1 START WITH 1;
CREATE TABLE TABLE1
(
    ID_COL     INT NOT NULL PRIMARY KEY,
    FIRST_COL  VARCHAR2(20),
    SECOND_COL VARCHAR2(20)
);
INSERT INTO TABLE1 (ID_COL, FIRST_COL, SECOND_COL)
VALUES (nextval('SEQ1'), 'VALUE_COL_1_ROW_1', 'VALUE_COL_2_ROW_1');
INSERT INTO TABLE1 (ID_COL, FIRST_COL, SECOND_COL)
VALUES (nextval('SEQ1'), 'VALUE_COL_1_ROW_2', 'VALUE_COL_2_ROW_2');
INSERT INTO TABLE1 (ID_COL, FIRST_COL, SECOND_COL)
VALUES (nextval('SEQ1'), 'VALUE_COL_1_ROW_3', 'VALUE_COL_2_ROW_3');
INSERT INTO TABLE1 (ID_COL, FIRST_COL, SECOND_COL)
VALUES (nextval('SEQ1'), 'VALUE_COL_1_ROW_4', 'VALUE_COL_2_ROW_4');
INSERT INTO TABLE1 (ID_COL, FIRST_COL, SECOND_COL)
VALUES (nextval('SEQ1'), 'VALUE_COL_1_ROW_5', 'VALUE_COL_2_ROW_5');
```


