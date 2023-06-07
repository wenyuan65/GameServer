package com.panda.game.core.jdbc.name;

public interface NameStrategy {

	boolean checkColumnName(String columnName);
	
	String convert2FieldName(String columnName);
	
	String convert2ColumnName(String fieldName);
	
	String convert2ClassName(String tableName);
	
	String convert2TableName(String className);

	String getSetterName(String fieldName);

	String getGetterName(String fieldName);

}
