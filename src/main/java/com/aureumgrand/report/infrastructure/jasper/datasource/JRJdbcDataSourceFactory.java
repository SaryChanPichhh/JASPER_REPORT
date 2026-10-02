package com.aureumgrand.report.infrastructure.jasper.datasource;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class JRJdbcDataSourceFactory {

    private final DataSource dataSource;

    public JRJdbcDataSourceFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
