package com.aureumgrand.report.domain.port;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {
    Optional<String> findJrxmlByTemplateName(String templateName);
    boolean existsByTemplateName(String templateName);
    void saveTemplate(String templateName, String jrxmlContent, String description);
    List<String> findAllTemplateNames();
}
