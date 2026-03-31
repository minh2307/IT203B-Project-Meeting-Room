package org.example.util;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class JDBCConnectionTest {

    @Test
    void getInstance_shouldReturnSameInstance() {
        JDBCConnection instance1 = JDBCConnection.getInstance();
        JDBCConnection instance2 = JDBCConnection.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }

    @Test
    void getConnection_shouldReturnOpenConnection() throws SQLException {
        try (Connection connection = JDBCConnection.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void closeConnection_shouldCloseConnectionSuccessfully() throws SQLException {
        JDBCConnection jdbcConnection = JDBCConnection.getInstance();
        Connection connection = JDBCConnection.getConnection();

        assertNotNull(connection);
        assertFalse(connection.isClosed());

        jdbcConnection.closeConnection(connection);

        assertTrue(connection.isClosed());
    }

    @Test
    void closeConnection_shouldNotThrowExceptionWhenConnectionIsNull() {
        JDBCConnection jdbcConnection = JDBCConnection.getInstance();

        assertDoesNotThrow(() -> jdbcConnection.closeConnection(null));
    }
}