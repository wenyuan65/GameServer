package com.panda.game.core.jdbc.common;

import com.panda.game.core.jdbc.entity.FieldEntity;
import com.panda.game.core.jdbc.entity.TableEntity;

import java.util.List;

public class SQLHelper {

    public static String buildMaxIdSQL(TableEntity tableEntity) {
        return "select max(id) as id from " + tableEntity.getTableName();
    }

    public static String buildCountSQL(TableEntity tableEntity) {
        return "select count(*) as count from " + tableEntity.getTableName();
    }

    public static String buildSelectAllSQL(TableEntity tableEntity) {
        return String.format("select * from `%s`", tableEntity.getTableName());
    }

    public static String buildSelectByPlayerIdSQL(TableEntity tableEntity) {
        return String.format("select * from `%s` where playerId=?", tableEntity.getTableName());
    }

    public static String buildSelectSQL(TableEntity tableEntity) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from ").append(tableEntity.getTableName());

        boolean isFirst = true;
        List<FieldEntity> entityList = tableEntity.getFieldEntityList();
        for (int i = 0; i < entityList.size(); i++) {
            FieldEntity fieldEntity = entityList.get(i);
            if (!fieldEntity.isPrimary()) {
                continue;
            }

            if (isFirst) {
                sqlBuilder.append(" where ");
            } else {
                sqlBuilder.append(" and ");
            }
            sqlBuilder.append(fieldEntity.getColumnName()).append("=?");
            isFirst = false;
        }

        return sqlBuilder.toString();
    }

    public static String buildUpdateSQL(TableEntity tableEntity) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("update ").append(tableEntity.getTableName()).append(" SET ");

        boolean isFirst = true;
        List<FieldEntity> entityList = tableEntity.getFieldEntityList();
        for (int i = 0; i < entityList.size(); i++) {
            FieldEntity fieldEntity = entityList.get(i);
            if (fieldEntity.isPrimary()) {
                continue;
            }

            if (!isFirst) {
                sqlBuilder.append(',');
            }
            sqlBuilder.append(fieldEntity.getColumnName()).append("=?");
            isFirst = false;
        }

        isFirst = true;
        for (int i = 0; i < entityList.size(); i++) {
            FieldEntity fieldEntity = entityList.get(i);
            if (!fieldEntity.isPrimary()) {
                continue;
            }

            if (isFirst) {
                sqlBuilder.append(" where ");
            } else {
                sqlBuilder.append(" and ");
            }
            sqlBuilder.append(fieldEntity.getColumnName()).append("=?");
            isFirst = false;
        }

        return sqlBuilder.toString();
    }

    public static String buildDeleteSQL(TableEntity tableEntity) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("delete from ").append(tableEntity.getTableName());

        boolean isFirst = true;
        List<FieldEntity> entityList = tableEntity.getFieldEntityList();
        for (int i = 0; i < entityList.size(); i++) {
            FieldEntity fieldEntity = entityList.get(i);
            if (!fieldEntity.isPrimary()) {
                continue;
            }

            if (isFirst) {
                sqlBuilder.append(" where ");
            } else {
                sqlBuilder.append(" and ");
            }
            sqlBuilder.append(fieldEntity.getColumnName()).append("=?");
            isFirst = false;
        }

        return sqlBuilder.toString();
    }

    public static String buildDeleteAllSQL(TableEntity tableEntity) {
        return "truncate table " + tableEntity.getTableName();
    }

    public static String buildInsertSQL(TableEntity tableEntity) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder paramBuilder = new StringBuilder();
        boolean isFirst = true;

        sqlBuilder.append("insert into ").append(tableEntity.getTableName()).append('(');
        List<FieldEntity> entityList = tableEntity.getFieldEntityList();
        for (int i = 0; i < entityList.size(); i++) {
            FieldEntity fieldEntity = entityList.get(i);

            if (!isFirst) {
                sqlBuilder.append(',');
                paramBuilder.append(',');
            }
            paramBuilder.append('?');

            sqlBuilder.append(fieldEntity.getColumnName());
            isFirst = false;
        }
        sqlBuilder.append(") value (").append(paramBuilder).append(')');

        return sqlBuilder.toString();
    }

    public static String buildAddOrUpdateSQL(TableEntity tableEntity) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder paramBuilder = new StringBuilder();
        boolean isFirst = true;

        sqlBuilder.append("replace into ").append(tableEntity.getTableName()).append('(');
        List<FieldEntity> entityList = tableEntity.getFieldEntityList();
        for (int i = 0; i < entityList.size(); i++) {
            FieldEntity fieldEntity = entityList.get(i);

            if (!isFirst) {
                sqlBuilder.append(',');
                paramBuilder.append(',');
            }
            paramBuilder.append('?');

            sqlBuilder.append(fieldEntity.getColumnName());
            isFirst = false;
        }
        sqlBuilder.append(") value (").append(paramBuilder).append(')');

        return sqlBuilder.toString();
    }

}
