package com.aureumgrand.report.infrastructure.persistence;

import com.aureumgrand.report.domain.port.ReportRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportTemplateJpaRepository jpaRepository;

    public ReportRepositoryImpl(ReportTemplateJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<String> findJrxmlByTemplateName(String templateName) {
        return jpaRepository.findByTemplateName(templateName)
                .map(ReportTemplateEntity::getJrxmlContent);
    }

    @Override
    public boolean existsByTemplateName(String templateName) {
        return jpaRepository.existsByTemplateName(templateName);
    }

    @Override
    public void saveTemplate(String templateName, String jrxmlContent, String description) {
        ReportTemplateEntity entity = jpaRepository.findByTemplateName(templateName)
                .orElse(new ReportTemplateEntity(templateName, description, jrxmlContent));
        entity.setJrxmlContent(jrxmlContent);
        entity.setDescription(description);
        entity.setVersion(entity.getVersion() != null ? entity.getVersion() + 1 : 1);
        jpaRepository.save(entity);
    }

    @Override
    public List<String> findAllTemplateNames() {
        return jpaRepository.findAll().stream()
                .map(ReportTemplateEntity::getTemplateName)
                .toList();
    }
}
