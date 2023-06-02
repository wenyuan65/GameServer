package com.panda.game.core.jdbc.entity;

import com.panda.game.common.utils.SQLTypeUtils;
import com.panda.game.core.jdbc.param.ColumnHandlerFactory;
import com.panda.game.core.jdbc.param.ColumnValueHandler;
import com.panda.game.core.jdbc.param.impl.NullParamValueSetter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FieldEntity {
	
	/** jdbc对象中的字段 */
	private Field field;
	/** 字段名 */
	private String fieldName;
	/** 字段类型 */
	private Class<?> fieldTypeClazz;
	/** 字段类型 */
	private String fieldType;
	/** 数据库中的字段 */
	private String columnName;
	/** 数据库字段类型  */
	private String columnType;
	/** sql type */
	private int sqlType;
	/** 是否主键 */
	private boolean primary;
	/** 自动增长 */
	private boolean autoIncrement;
	/** getter/setter方法 */
	private Method setter;
	private Method getter;

	private ColumnValueHandler paramValueSetter;
	private ColumnValueHandler autoIncrementParamValueSetter;
	
	public FieldEntity(Field field) {
		this.field = field;
	}
	
	public void init() throws Exception {
		this.sqlType = SQLTypeUtils.convert2SqlType(this.columnType);
		this.paramValueSetter = ColumnHandlerFactory.createColumnHandler(this.sqlType);
		if (this.autoIncrement) {
			autoIncrementParamValueSetter = new NullParamValueSetter(this.sqlType);
		}
	}
	
	public void setAutoIncrementParamValue(PreparedStatement ps, int paramIndex, Object value) throws SQLException {
		if (autoIncrementParamValueSetter != null) {
			this.autoIncrementParamValueSetter.setParamValue(ps, paramIndex, value);
		} else {
			this.paramValueSetter.setParamValue(ps, paramIndex, value);
		}
	}
	
	public void setParamValue(PreparedStatement ps, int paramIndex, Object value) throws SQLException {
		this.paramValueSetter.setParamValue(ps, paramIndex, value);
	}
	
	public Object getResultValue(ResultSet rs, int columnIndex) throws SQLException {
		return this.paramValueSetter.getResultValue(rs, columnIndex);
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class<?> getFieldTypeClazz() {
		return fieldTypeClazz;
	}

	public void setFieldTypeClazz(Class<?> fieldTypeClzz) {
		this.fieldTypeClazz = fieldTypeClzz;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String jdbcName) {
		this.columnName = jdbcName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String jdbcType) {
		this.columnType = jdbcType;
	}

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public Method getSetter() {
		return setter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

	public Method getGetter() {
		return getter;
	}

	public void setGetter(Method getter) {
		this.getter = getter;
	}
}
