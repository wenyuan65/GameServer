package com.panda.game.core.jdbc.param.impl;

import com.panda.game.core.jdbc.param.ColumnValueHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DateParamValueSetter implements ColumnValueHandler {

	@Override
	public void setParamValue(PreparedStatement ps, int index, Object value) throws SQLException {
		java.util.Date date = (java.util.Date)value;
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		ps.setDate(index, sqlDate);
	}
	
	@Override
	public Object getResultValue(ResultSet rs, int index) throws SQLException {
		java.sql.Date date = rs.getDate(index);
		return new java.util.Date(date.getTime());
	}

}
