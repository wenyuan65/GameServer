package com.panda.game.core.jdbc.param;

import com.panda.game.core.jdbc.param.impl.*;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class ColumnHandlerFactory {

	private static final Map<Integer, Class<? extends ColumnValueHandler>> map = new HashMap<>();

	static {
		map.put(Types.SMALLINT, IntParamValueSetter.class);
		map.put(Types.TINYINT, IntParamValueSetter.class);
		map.put(Types.INTEGER, IntParamValueSetter.class);
		map.put(Types.BIGINT, LongParamValueSetter.class);
		map.put(Types.DOUBLE, DoubleParamValueSetter.class);
		map.put(Types.FLOAT, FloatParamValueSetter.class);
		map.put(Types.VARCHAR, StringParamValueSetter.class);
		map.put(Types.NVARCHAR, StringParamValueSetter.class);
		map.put(Types.LONGVARCHAR, StringParamValueSetter.class);
		map.put(Types.LONGNVARCHAR, StringParamValueSetter.class);
		map.put(Types.DATE, DateParamValueSetter.class);
		map.put(Types.TIME, TimeParamValueSetter.class);
		map.put(Types.TIMESTAMP, TimestampParamValueSetter.class);
	}

	public static ColumnValueHandler createColumnHandler(int sqlType) throws Exception {
		Class<? extends ColumnValueHandler> paramValueSetterClazz = map.get(sqlType);
		if (paramValueSetterClazz == null) {
			throw new Exception("no ParamValueSetter for sqlType:" + sqlType);
		}
		
		try {
			return paramValueSetterClazz.newInstance();
		} catch (Exception e) {
			throw new Exception("generate ParamValueSetter error", e);
		}
	}
	
}
