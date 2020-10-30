package com.svalyn.application.entities;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class AssessmentStatusEntityEnumType extends org.hibernate.type.EnumType<AssessmentStatusEntity> {

    private static final long serialVersionUID = -8176515135586629648L;

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.OTHER);
        } else {
            preparedStatement.setObject(index, value.toString(), Types.OTHER);
        }
    }
}