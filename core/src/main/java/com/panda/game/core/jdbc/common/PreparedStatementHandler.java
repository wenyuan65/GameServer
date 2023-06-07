package com.panda.game.core.jdbc.common;

import com.sun.istack.internal.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementHandler<T> {
    @Nullable
    T handle(PreparedStatement ps) throws Throwable;
}
