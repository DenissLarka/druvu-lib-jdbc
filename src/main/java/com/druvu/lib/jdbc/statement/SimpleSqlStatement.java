package com.druvu.lib.jdbc.statement;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.springframework.jdbc.core.ColumnMapRowMapper;

/**
 * @author Deniss Larka
 * at 11 Aug 2020
 */
public class SimpleSqlStatement extends SqlStatement<Map<String, Object>> {

	private final String query;
	private final Object[] parameters;

	public SimpleSqlStatement(String query, Object... args) {
		super(new ColumnMapRowMapper());
		this.query = Objects.requireNonNull(query);
		this.parameters = args == null ? new Object[0] : args;
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public Object[] getParameters() {
		return parameters != null ? Arrays.copyOf(parameters, parameters.length) : new Object[0];
	}

}
