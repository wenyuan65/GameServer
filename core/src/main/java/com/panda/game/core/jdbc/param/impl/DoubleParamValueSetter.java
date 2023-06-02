package com.panda.game.core.jdbc.param.impl;

import com.panda.game.core.jdbc.param.ColumnValueHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleParamValueSetter implements ColumnValueHandler {

	@Override
	public void setParamValue(PreparedStatement ps, int index, Object value) throws SQLException {
		ps.setDouble(index, (double)value);
	}

	@Override
	public Object getResultValue(ResultSet rs, int index) throws SQLException {
		return rs.getDouble(index);
	}

}
