package com.druvu.lib.jdbc.internal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Deniss Larka
 * on 10 Jan 2024
 */
public class ThrowingMapper<T> implements RowMapper<T> {
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		throw new UnsupportedOperationException();
	}
}
