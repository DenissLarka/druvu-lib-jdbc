package com.druvu.lib.jdbc.examples;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Deniss Larka
 * on 26 Mar 2024
 */
public record Entity1Mapper() implements RowMapper<Entity1> {

	@Override
	public Entity1 mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Entity1(rs.getInt(1), rs.getString(2));
	}
}
