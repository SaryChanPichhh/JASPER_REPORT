package com.aureumgrand.report.infrastructure.jasper;

import com.aureumgrand.report.domain.exception.ReportGenerationException;
import com.aureumgrand.report.domain.exception.TemplateNotFoundException;
import com.aureumgrand.report.domain.port.ReportRepository;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JasperTemplateLoader {

    private static final Logger log = LoggerFactory.getLogger(JasperTemplateLoader.class);

    private final Map<String, JasperReport> reportCache = new ConcurrentHashMap<>();
    private final ReportRepository reportRepository;

    @Value("${report.template-base-dir:reports/}")
    private String templateBaseDir;

    @Value("${report.cache-enabled:true}")
    private boolean cacheEnabled;

    public JasperTemplateLoader(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public JasperReport loadTemplate(String templateName) {
        if (templateName == null || templateName.isBlank()) {
            throw new IllegalArgumentException("Template name cannot be empty");
        }

        String normalizedName = normalizeName(templateName);

        if (cacheEnabled && reportCache.containsKey(normalizedName)) {
            log.debug("Found cached JasperReport for template '{}'", normalizedName);
            return reportCache.get(normalizedName);
        }

        JasperReport report = resolveTemplate(normalizedName);

        if (cacheEnabled) {
            reportCache.put(normalizedName, report);
            log.debug("Cached compiled template '{}'. Cache size: {}", normalizedName, reportCache.size());
        }

        return report;
    }

    private JasperReport resolveTemplate(String templateName) {
        // 1. Check DB-driven dynamic templates first
        Optional<String> dbJrxml = reportRepository.findJrxmlByTemplateName(templateName);
        if (dbJrxml.isPresent() && !dbJrxml.get().isBlank()) {
            log.info("Loading dynamic report template '{}' from database", templateName);
            try (InputStream is = new ByteArrayInputStream(dbJrxml.get().getBytes(StandardCharsets.UTF_8))) {
                return JasperCompileManager.compileReport(is);
            } catch (Exception e) {
                throw new ReportGenerationException("Failed to compile dynamic template '" + templateName + "' from database", e);
            }
        }

        // 2. Candidate classpath paths to check (.jasper first, then .jrxml)
        String[] candidateJasperPaths = buildCandidatePaths(templateName, ".jasper");
        for (String path : candidateJasperPaths) {
            Resource resource = new ClassPathResource(path);
            if (resource.exists()) {
                log.debug("Loading precompiled report from classpath: {}", path);
                try (InputStream is = resource.getInputStream()) {
                    return (JasperReport) JRLoader.loadObject(is);
                } catch (Exception e) {
                    log.warn("Failed to load precompiled .jasper at '{}', falling back to .jrxml. Reason: {}", path, e.getMessage());
                }
            }
        }

        // 3. Candidate classpath paths for .jrxml
        String[] candidateJrxmlPaths = buildCandidatePaths(templateName, ".jrxml");
        for (String path : candidateJrxmlPaths) {
            Resource resource = new ClassPathResource(path);
            if (resource.exists()) {
                log.info("Compiling report template from JRXML source: {}", path);
                try (InputStream is = resource.getInputStream()) {
                    return JasperCompileManager.compileReport(is);
                } catch (Exception e) {
                    throw new ReportGenerationException("Failed to compile JRXML template at '" + path + "'", e);
                }
            }
        }

        throw new TemplateNotFoundException(templateName, "Looked in DB and classpath locations: " +
                String.join(", ", candidateJasperPaths) + ", " + String.join(", ", candidateJrxmlPaths));
    }

    private String[] buildCandidatePaths(String templateName, String extension) {
        String base = templateBaseDir.endsWith("/") ? templateBaseDir : templateBaseDir + "/";
        String cleanName = templateName.replaceAll("\\.(jrxml|jasper)$", "");
        String simpleName = cleanName.contains("/") ? cleanName.substring(cleanName.lastIndexOf('/') + 1) : cleanName;

        return new String[]{
                base + cleanName + extension,
                base + simpleName + extension,
                base + simpleName + "Report" + extension,
                base + "invoice/" + cleanName + extension,
                base + "sales-summary/" + cleanName + extension,
                base + "subreports/" + cleanName + extension,
                cleanName + extension,
                templateName
        };
    }

    private String normalizeName(String templateName) {
        return templateName.trim().replace("\\", "/");
    }

    public void evict(String templateName) {
        if (templateName != null) {
            reportCache.remove(normalizeName(templateName));
            log.info("Evicted template '{}' from cache", templateName);
        }
    }

    public void clearCache() {
        reportCache.clear();
        log.info("JasperReport template cache cleared");
    }

    public int getCacheSize() {
        return reportCache.size();
    }
}
