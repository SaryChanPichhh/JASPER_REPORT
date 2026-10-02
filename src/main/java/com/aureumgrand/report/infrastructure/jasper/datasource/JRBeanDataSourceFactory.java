package com.aureumgrand.report.infrastructure.jasper.datasource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class JRBeanDataSourceFactory {

    public JRDataSource create(Collection<?> data) {
        if (data == null || data.isEmpty()) {
            return new JREmptyDataSource();
        }
        return new SafeBeanCollectionDataSource(data);
    }

    public JRDataSource createEmpty() {
        return new JREmptyDataSource();
    }
}
