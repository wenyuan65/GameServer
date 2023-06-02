package com.panda.game.core.jdbc.param.impl;

import com.panda.game.core.jdbc.param.ColumnValueHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class TimeParamValueSetter implements ColumnValueHandler {

	@Override
	public void setParamValue(PreparedStatement ps, int index, Object value) throws SQLException {
		java.util.Date date = (java.util.Date)value;
		Time sqlTime = new Time(date.getTime());
		ps.setTime(index, sqlTime);
	}

	@Override
	public Object getResultValue(ResultSet rs, int index) throws SQLException {
		Time time = rs.getTime(index);
		return new java.util.Date(time.getTime());
	}

}
