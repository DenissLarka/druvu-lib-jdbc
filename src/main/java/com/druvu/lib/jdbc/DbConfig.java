package com.druvu.lib.jdbc;

import java.util.Locale;
import java.util.Objects;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;

import com.druvu.lib.jdbc.tomcat_pool.PoolPropertiesEx;

/**
 * @author Deniss Larka
 * at 11 Aug 2020
 */
public class DbConfig {

	private final String id;
	private final PoolConfiguration pp;

	private DbConfig(String id, PoolConfiguration poolConfig) {
		this.id = Objects.requireNonNull(id).trim().toUpperCase(Locale.ENGLISH);
		this.pp = Objects.requireNonNull(poolConfig);
	}

	public DbConfig(String id, String url, String user, String password, String driver, String validationSelect) {
		this(id, PoolPropertiesEx.create(url, user, password, driver, validationSelect));
	}

	public String getId() {
		return id;
	}

	PoolConfiguration getPoolProperties() {
		return pp;
	}

	public static DbConfig of(String id, String url, String user, String password, String driver, String validationSelect) { //NOPMD
		return new DbConfig(id, url, user, password, driver, validationSelect);
	}

	public DbConfig withDefaultTransactionIsolation(int transactionIsolation) {
		final PoolConfiguration copyConf = PoolPropertiesEx.copy(this.pp);
		copyConf.setDefaultTransactionIsolation(transactionIsolation);
		return new DbConfig(this.id, copyConf);
	}

	@Override
	public String toString() {
		return String.format("DB:%s/%s/%s", id, this.pp.getUsername(), this.pp.getUrl());
	}

}
