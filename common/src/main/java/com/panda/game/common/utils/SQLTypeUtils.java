package com.panda.game.common.utils;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SQLTypeUtils {

	private static Map<String, Integer> typeMap = new HashMap<>();
	static {
		typeMap.put("bit", Types.BIT);
		typeMap.put("smallint", Types.SMALLINT);
		typeMap.put("tinyint", Types.TINYINT);
		typeMap.put("int", Types.INTEGER);
		typeMap.put("bigint", Types.BIGINT);
		typeMap.put("decimal", Types.DECIMAL);
		typeMap.put("double", Types.DOUBLE);
		typeMap.put("float", Types.FLOAT);
		typeMap.put("numeric", Types.NUMERIC);
		typeMap.put("real", Types.REAL);
		typeMap.put("varchar", Types.VARCHAR);
		typeMap.put("nvarchar", Types.NVARCHAR);
		typeMap.put("longvarchar", Types.LONGVARCHAR);
		typeMap.put("longnvarchar", Types.LONGNVARCHAR);
		typeMap.put("date", Types.DATE);
		typeMap.put("time", Types.TIME);
		typeMap.put("timestamp", Types.TIMESTAMP);
		typeMap.put("null", Types.NULL);
	}
	
	public static int convert2SqlType(String columnType) {
		for (Entry<String, Integer> entry : typeMap.entrySet()) {
			if (columnType.trim().toLowerCase().startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return Types.NULL;
	}

}
