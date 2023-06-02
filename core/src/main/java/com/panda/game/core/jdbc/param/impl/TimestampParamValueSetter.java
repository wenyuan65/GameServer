package com.panda.game.core.jdbc.param.impl;

import com.panda.game.core.jdbc.param.ColumnValueHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampParamValueSetter implements ColumnValueHandler {

	@Override
	public void setParamValue(PreparedStatement ps, int index, Object value) throws SQLException {
		java.util.Date date = (java.util.Date)value;
		Timestamp sqlTimestamp = new Timestamp(date.getTime());
		ps.setTimestamp(index, sqlTimestamp);
	}

	@Override
	public Object getResultValue(ResultSet rs, int index) throws SQLException {
		Timestamp timestamp = rs.getTimestamp(index);
		return new java.util.Date(timestamp.getTime());
		
	}

}
