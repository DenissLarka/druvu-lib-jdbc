package com.druvu.lib.jdbc;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;


import lombok.extern.slf4j.Slf4j;

/**
 * @author Deniss Larka
 * at 11 Aug 2020
 */
@Slf4j
public final class DbAccessFactory {

	private DbAccessFactory() {
	}

	public static DbAccess create(DbConfig dbConfig) {
		DataSource pool = new DataSource(dbConfig.getPoolProperties());
		DbAccessTxImpl result = new DbAccessTxImpl(dbConfig.getId(), pool, new DataSourceTransactionManager(pool));
		if (!dbConfig.getPoolProperties().getValidationQuery().isEmpty()) {
			try {
				result.select(SimpleSql.fromString(dbConfig.getPoolProperties().getValidationQuery()));
			}
			catch (Exception e) { //NOPMD
				log.error("Exception in DB validation: {}", dbConfig);
				throw e;
			}
		}
		log.info("DB created {}", dbConfig);
		return result;
	}

}
