package com.panda.game.core.jdbc.entity;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.StringUtils;
import com.panda.game.core.jdbc.common.JdbcConstants;
import com.panda.game.core.jdbc.common.JdbcUtils;
import com.panda.game.core.jdbc.common.SQLHelper;
import com.panda.game.core.jdbc.name.NameStrategy;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableEntity {
	
	private static Logger log = LoggerFactory.getLogger(TableEntity.class);

	private DataSource dataSource;

	/** Entity类名 */
	private Class<?> entityClass;
	/** jdbc对象名 */
	private String tableEntityName;
	/** 对应数据库表名 */
	private String tableName;
	/** 表字段名 */
	private List<FieldEntity> fieldEntityList = new ArrayList<>();

	private String insertSQL;
	private String deleteSQL;
	private String deleteAllSQL;
	private String updateSQL;
	private String selectSQL;
	private String selectAllSQL;
	private String selectByPlayerIdSQL;
	private String addOrUpdateSQL;
	private String maxIdSQL;
	private String countSQL;

	private NameStrategy nameStrategy;
	
	public TableEntity(Class<?> clazz) {
		this.entityClass = clazz;
	}
	
	public void init() throws Exception {
		this.tableEntityName = entityClass.getSimpleName();
		this.tableName = nameStrategy.classNameToTableName(tableEntityName);

		parseField();
		buildSQlTemplate();
	}

	private void buildSQlTemplate() {
		insertSQL = SQLHelper.buildInsertSqlTemplate(this);
		deleteSQL = SQLHelper.buildDeleteSqlTemplate(this);
		updateSQL = SQLHelper.buildUpdateSqlTemplate(this);
		selectSQL = SQLHelper.buildSelectSQLTemplate(this);
		addOrUpdateSQL = SQLHelper.buildAddOrUpdateSQLTemplate(this);
		selectAllSQL = SQLHelper.buildSelectAllSqlTemplate(this);
		selectByPlayerIdSQL = SQLHelper.buildSelectByPlayerIdSqlTemplate(this);
		deleteAllSQL = SQLHelper.buildDeleteAllSqlTemplate(this);
		maxIdSQL = SQLHelper.buildMaxIdSqlTemplate(this);
		countSQL = SQLHelper.buildCountSqlTemplate(this);
	}

	private void parseField() throws Exception {
		Field[] fields = entityClass.getDeclaredFields();

		// 解析key字段
		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : fields) {
			field.setAccessible(true);
			fieldMap.put(field.getName(), field);
		}

		String sqlDescTable = String.format("desc %s", this.tableName);
		List<Map<String, Object>> resultList = JdbcUtils.queryListMap(this.getDataSource(), sqlDescTable);
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
			
			String propertyName = nameStrategy.columnsNameToPropertyName(columnName);
			Field field = fieldMap.get(propertyName);
			if (field == null) {
				throw new Exception(String.format("在表对象%s中找不到表%s列名%s对应的的字段%s", tableEntityName, tableName, columnName, propertyName));
			}

			String getterName = nameStrategy.getGetterName(propertyName);
			Method getter = entityClass.getDeclaredMethod(getterName);
			String setterName = nameStrategy.getSetterName(propertyName);
			Method setter = entityClass.getDeclaredMethod(setterName);

			FieldEntity entity = new FieldEntity(field);
			entity.setFieldName(propertyName);
			entity.setColumnName(columnName);
			entity.setColumnType(columnType);
			entity.setAutoIncrement(autoIncrement);
			entity.setPrimary(isPrimary);
			entity.setFieldTypeClazz(field.getType());
			entity.setGetter(getter);
			entity.setSetter(setter);
			entity.init();
			fieldEntityList.add(entity);
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
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
