package com.aureumgrand.report.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportTemplateJpaRepository extends JpaRepository<ReportTemplateEntity, Long> {
    Optional<ReportTemplateEntity> findByTemplateName(String templateName);
    boolean existsByTemplateName(String templateName);
}
