package com.svalyn.application.entities;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class TestResultStatusEntityEnumType extends org.hibernate.type.EnumType<TestResultStatusEntity> {

    private static final long serialVersionUID = -7249728734316649635L;

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index,
            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.OTHER);
        } else {
            preparedStatement.setObject(index, value.toString(), Types.OTHER);
        }
    }
}