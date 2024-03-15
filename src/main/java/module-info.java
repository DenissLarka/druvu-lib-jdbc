/**
 * @author Deniss Larka
 * on 15 Mar 2024
 */
module com.druvu.lib.jdbc {
	//SQL
	requires java.sql;
	//SPRING JDBC and TX
	requires spring.jdbc;
	requires spring.tx;
	//TOMCAT POOL
	requires tomcat.jdbc;
	//SLF4J
	requires org.slf4j;
	//LOMBOK
	requires static lombok;
}