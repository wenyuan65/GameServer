package com.panda.game.core.jdbc.entity;

import com.panda.game.common.constants.DataBaseType;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.jdbc.base.BaseEntity;
import com.panda.game.core.jdbc.common.JdbcConstants;
import com.panda.game.core.jdbc.common.JdbcUtils;
import com.panda.game.core.jdbc.common.SQLHelper;
import com.panda.game.core.jdbc.name.NameStrategy;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableEntity<T> {
	
	private static Logger log = LoggerFactory.getLogger(TableEntity.class);

	/** 数据库的简称 */
	private DataBaseType database;

	/** Entity类名 */
	private Class<T> entityClass;
	/** jdbc对象名 */
	private String tableEntityName;
	/** 对应数据库表名 */
	private String tableName;
	/** 表字段名 */
	private List<FieldEntity> fieldEntityList = new ArrayList<>();
	/** 表字段是playerId的 */
	private FieldEntity playerIdFieldEntity;

	private String insertSQL;
	private String deleteSQL;
	private String deleteAllSQL;
	private String updateSQL;
	private String selectSQL; // 通过primary字段查询
	private String selectAllSQL;
	private String selectByPlayerIdSQL; // 通过playerId字段查询
	private String addOrUpdateSQL;
	private String maxIdSQL;
	private String countSQL;

	private NameStrategy nameStrategy;
	
	public TableEntity(Class<T> clazz) {
		this.entityClass = clazz;
	}
	
	public void init(DataSource dataSource) throws Exception {
		this.tableEntityName = entityClass.getSimpleName();
		this.tableName = nameStrategy.convert2TableName(tableEntityName);

		parseField(dataSource);
		buildSQlTemplate();
	}

	private void buildSQlTemplate() {
		insertSQL = SQLHelper.buildInsertSQL(this);
		deleteSQL = SQLHelper.buildDeleteSQL(this);
		updateSQL = SQLHelper.buildUpdateSQL(this);
		selectSQL = SQLHelper.buildSelectSQL(this);
		addOrUpdateSQL = SQLHelper.buildAddOrUpdateSQL(this);
		selectAllSQL = SQLHelper.buildSelectAllSQL(this);
		selectByPlayerIdSQL = SQLHelper.buildSelectByPlayerIdSQL(this);
		deleteAllSQL = SQLHelper.buildDeleteAllSQL(this);
		maxIdSQL = SQLHelper.buildMaxIdSQL(this);
		countSQL = SQLHelper.buildCountSQL(this);

		log.info("============================================================");
		log.info("Build SQL: {} ", insertSQL);
		log.info("Build SQL: {} ", deleteSQL);
		log.info("Build SQL: {} ", updateSQL);
		log.info("Build SQL: {} ", selectSQL);
		log.info("Build SQL: {} ", addOrUpdateSQL);
		log.info("Build SQL: {} ", selectAllSQL);
		log.info("Build SQL: {} ", selectByPlayerIdSQL);
		log.info("Build SQL: {} ", deleteAllSQL);
		log.info("Build SQL: {} ", maxIdSQL);
		log.info("Build SQL: {} ", countSQL);
	}

	private void parseField(DataSource dataSource) throws Exception {
		Field[] fields = entityClass.getDeclaredFields();

		// 解析key字段
		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : fields) {
			field.setAccessible(true);
			fieldMap.put(field.getName(), field);
		}

		String sqlDescTable = String.format("desc %s", this.tableName);
		List<Map<String, Object>> resultList = JdbcUtils.queryMapList(dataSource, sqlDescTable);
		for (Map<String, Object> map : resultList) {
			String columnName = (String)map.get(JdbcConstants.META_DATA_COLUMN_NAME);
			String columnType = (String)map.get(JdbcConstants.META_DATA_COLUMN_TYPE);
			String extra = (String)map.get(JdbcConstants.META_DATA_COLUMN_EXTRA);
			String KeyType = (String)map.get(JdbcConstants.META_DATA_COLUMN_KEY);
			
			boolean autoIncrement = StringUtils.isNotBlank(extra) && extra.indexOf(JdbcConstants.META_DATA_AUTOINCREMENT) != -1;
			boolean isPrimary = StringUtils.isNotBlank(KeyType) && KeyType.indexOf(JdbcConstants.META_DATA_PRI) != -1;
			
			if (!nameStrategy.checkColumnName(columnName)) {
				throw new Exception(String.format("数据库字段命令异常, table:%s, field:%s", tableName, columnName));
			}
			
			String fieldName = nameStrategy.convert2FieldName(columnName);
			Field field = fieldMap.get(fieldName);
			if (field == null) {
				throw new Exception(String.format("在表对象%s中找不到表%s列名%s对应的的字段%s", tableEntityName, tableName, columnName, fieldName));
			}

			String getterName = nameStrategy.getGetterName(fieldName);
			Method getter = entityClass.getDeclaredMethod(getterName);
			String setterName = nameStrategy.getSetterName(fieldName);
			Method setter = entityClass.getDeclaredMethod(setterName);

			FieldEntity entity = new FieldEntity(field);
			entity.setFieldName(fieldName);
			entity.setColumnName(columnName);
			entity.setColumnType(columnType);
			entity.setAutoIncrement(autoIncrement);
			entity.setPrimary(isPrimary);
			entity.setFieldTypeClazz(field.getType());
			entity.setGetter(getter);
			entity.setSetter(setter);
			entity.init();
			fieldEntityList.add(entity);

			// 当前字段是playerId
			if ("playerId".equals(fieldName)) {
				playerIdFieldEntity = entity;
			}
		}
	}

	/**
	 * 将数据库查询结果resultSet转化为对象列表<br/>
	 * 暂时默认resultSet是类似于"select * from table [where id = ?]"的sql查询出来的结果<br/>
	 * 如果指定了查询的字段不是默认排序的所有字段，则不可时使用该方法
	 * @param resultSet
	 * @return
	 * @throws Exception
	 */
	public List<T> convert(ResultSet resultSet) throws Exception {
		List<T> list = new ArrayList<>();
		while (resultSet.next()) {
			T obj = entityClass.newInstance();
			for (int i = 0; i < fieldEntityList.size(); i++) {
				FieldEntity fieldEntity = fieldEntityList.get(i);

				Object result = fieldEntity.getResultValue(resultSet, i + 1);
				fieldEntity.getSetter().invoke(obj, result);
			}

			if (obj instanceof BaseEntity) {
				((BaseEntity)obj).clearOption();
			}

			list.add(obj);
		}

		return list;
	}

	/**
	 * 往PreparedStatement中注入参数<br/>
	 * SQL类似于"select * from {table} where id = ?"
	 * @param ps
	 * @param keys
	 * @throws Exception
	 */
	public void setSelectSQLParams(PreparedStatement ps, Object... keys) throws Exception {
		int paramIndex = 1;
		for (int i = 0; i < fieldEntityList.size(); i++) {
			FieldEntity fieldEntity = fieldEntityList.get(i);
			if (!fieldEntity.isPrimary()) {
				continue;
			}

			fieldEntity.setParamValue(ps, paramIndex, keys[paramIndex - 1]);
			paramIndex ++;
		}
	}

	/**
	 * 往PreparedStatement中注入参数<br/>
	 * SQL类似于"select * from {table} where player_id = ?"
	 * @param ps
	 * @param playerId
	 * @throws Exception
	 */
	public void setSelectByPlayerIdSQLParams(PreparedStatement ps, long playerId) throws Exception {
		playerIdFieldEntity.setParamValue(ps, 1, playerId);
	}

	/**
	 * 往PreparedStatement中注入参数<br/>
	 * SQL类似于"insert/replace into {table} (column1, column2, ...) value (?, ?, ...)"
	 * @param ps
	 * @param obj
	 * @throws Exception
	 */
	public void setInsertOrReplaceSQLParams(PreparedStatement ps, T obj) throws Exception {
		int paramIndex = 1;
		for (int i = 0; i < fieldEntityList.size(); i++) {
			FieldEntity fieldEntity = fieldEntityList.get(i);
			Method getter = fieldEntity.getGetter();
			Object result = getter.invoke(obj);
			if (fieldEntity.isAutoIncrement() && fieldEntity.getFieldTypeClazz() == int.class && ((int)result) <= 0) {
				fieldEntity.setAutoIncrementParamValue(ps, paramIndex++, result);
			} else {
				fieldEntity.setParamValue(ps, paramIndex++, result);
			}
		}
	}

	/**
	 * 往PreparedStatement中注入参数<br/>
	 * SQL类似于"update {table} set {column1}=? , {column2}=? where key1=? and key2=?"
	 * @param ps
	 * @param obj
	 * @throws Exception
	 */
	public void setUpdateSQLParams(PreparedStatement ps, T obj) throws Exception {
		int paramIndex = 1;
		List<FieldEntity> primaryFieldEntityList = new ArrayList<>();
		for (int i = 0; i < fieldEntityList.size(); i++) {
			FieldEntity fieldEntity = fieldEntityList.get(i);
			if (fieldEntity.isPrimary()) {
				primaryFieldEntityList.add(fieldEntity);
				continue;
			}
			Object result = fieldEntity.getGetter().invoke(obj);
			fieldEntity.setParamValue(ps, paramIndex++, result);
		}

		// 注入primary字段
		for (int i = 0; i < primaryFieldEntityList.size(); i++) {
			FieldEntity fieldEntity = primaryFieldEntityList.get(i);
			Object result = fieldEntity.getGetter().invoke(obj);
			fieldEntity.setParamValue(ps, paramIndex++, result);
		}
	}

	/**
	 * 往PreparedStatement中注入参数<br/>
	 * SQL类似于"delete from {table} where key1=? and key2=?"
	 * @param ps
	 * @param obj
	 * @throws Exception
	 */
	public void setDeleteSQLParams(PreparedStatement ps, T obj) throws Exception {
		int paramIndex = 1;
		for (int i = 0; i < fieldEntityList.size(); i++) {
			FieldEntity fieldEntity = fieldEntityList.get(i);
			if (!fieldEntity.isPrimary()) {
				continue;
			}

			Object result = fieldEntity.getGetter().invoke(obj);
			fieldEntity.setParamValue(ps, paramIndex++, result);
		}
	}

	public DataBaseType getDatabase() {
		return database;
	}

	public void setDatabase(DataBaseType database) {
		this.database = database;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public String getTableEntityName() {
		return tableEntityName;
	}

	public String getTableName() {
		return tableName;
	}

	public List<FieldEntity> getFieldEntityList() {
		return fieldEntityList;
	}

	public void setNameStrategy(NameStrategy nameStrategy) {
		this.nameStrategy = nameStrategy;
	}

	public String getInsertSQL() {
		return insertSQL;
	}

	public String getDeleteSQL() {
		return deleteSQL;
	}

	public String getDeleteAllSQL() {
		return deleteAllSQL;
	}

	public String getUpdateSQL() {
		return updateSQL;
	}

	public String getSelectSQL() {
		return selectSQL;
	}

	public String getSelectAllSQL() {
		return selectAllSQL;
	}

	public String getSelectByPlayerIdSQL() {
		return selectByPlayerIdSQL;
	}

	public String getAddOrUpdateSQL() {
		return addOrUpdateSQL;
	}

	public String getMaxIdSQL() {
		return maxIdSQL;
	}

	public String getCountSQL() {
		return countSQL;
	}
}
