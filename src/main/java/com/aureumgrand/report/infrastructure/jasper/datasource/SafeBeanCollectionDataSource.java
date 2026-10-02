package com.aureumgrand.report.infrastructure.jasper.datasource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;
import org.apache.commons.beanutils2.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A robust JRDataSource implementation for JavaBean collections that:
 * 1. Supports case-insensitive property lookup (e.g. 'Qty', 'qty', 'QTY', 'ShopName', 'shopName').
 * 2. Matches snake_case to camelCase (e.g. 'item_code' to 'itemCode').
 * 3. Gracefully returns null for missing properties instead of crashing report generation.
 * 4. Supports Map items if mixed collections are provided.
 */
public class SafeBeanCollectionDataSource extends JRAbstractBeanDataSource {
    private static final Logger log = LoggerFactory.getLogger(SafeBeanCollectionDataSource.class);

    private final Collection<?> data;
    private Iterator<?> iterator;
    private Object currentBean;

    // Cache resolved property descriptors per class to ensure high performance
    private static final Map<Class<?>, Map<String, PropertyDescriptor>> CLASS_PROP_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, Method>> CLASS_GETTER_MAP = new ConcurrentHashMap<>();

    public SafeBeanCollectionDataSource(Collection<?> data) {
        super(false);
        this.data = data != null ? data : Collections.emptyList();
        this.iterator = this.data.iterator();
    }

    @Override
    public boolean next() {
        if (iterator != null && iterator.hasNext()) {
            currentBean = iterator.next();
            return true;
        }
        return false;
    }

    @Override
    public void moveFirst() {
        if (data != null) {
            iterator = data.iterator();
        }
    }

    public Collection<?> getData() {
        return data;
    }

    public int getRecordCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        if (currentBean == null || jrField == null) {
            return null;
        }
        String fieldName = getPropertyName(jrField);
        return getPropertySafely(currentBean, fieldName);
    }

    public static Object getPropertySafely(Object bean, String propertyName) {
        if (bean == null || propertyName == null || propertyName.isBlank()) {
            return null;
        }

        // Support nested dot notation safely (e.g. "amountReceive.amount", "AmountReceive.CurrencySymbol")
        if (propertyName.contains(".")) {
            String[] parts = propertyName.split("\\.", 2);
            Object parent = getPropertySafely(bean, parts[0]);
            return getPropertySafely(parent, parts[1]);
        }

        // Support Map directly
        if (bean instanceof Map<?, ?> map) {
            if (map.containsKey(propertyName)) {
                return map.get(propertyName);
            }
            // Case-insensitive key lookup in Map
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null && entry.getKey().toString().equalsIgnoreCase(propertyName)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        Class<?> beanClass = bean.getClass();

        // 1. Check cached PropertyDescriptor
        Map<String, PropertyDescriptor> propMap = CLASS_PROP_MAP.computeIfAbsent(beanClass, SafeBeanCollectionDataSource::buildPropertyDescriptorMap);
        PropertyDescriptor pd = propMap.get(normalize(propertyName));
        if (pd != null && pd.getReadMethod() != null) {
            try {
                return pd.getReadMethod().invoke(bean);
            } catch (Exception e) {
                log.debug("Failed to invoke read method '{}' on {}: {}", pd.getReadMethod().getName(), beanClass.getSimpleName(), e.getMessage());
            }
        }

        // 2. Check direct getters / boolean isXxx methods
        Map<String, Method> getterMap = CLASS_GETTER_MAP.computeIfAbsent(beanClass, SafeBeanCollectionDataSource::buildGetterMap);
        Method method = getterMap.get(normalize(propertyName));
        if (method != null) {
            try {
                return method.invoke(bean);
            } catch (Exception e) {
                log.debug("Failed to invoke getter '{}' on {}: {}", method.getName(), beanClass.getSimpleName(), e.getMessage());
            }
        }

        // 3. Fallback to standard PropertyUtils
        try {
            return PropertyUtils.getProperty(bean, propertyName);
        } catch (Exception ignored) {
            // Decapitalized fallback
            try {
                String decap = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
                return PropertyUtils.getProperty(bean, decap);
            } catch (Exception ignored2) {
                // Property not found on bean - return null safely instead of throwing exception
                return null;
            }
        }
    }

    private static String normalize(String name) {
        if (name == null) return "";
        return name.replace("_", "").toLowerCase(Locale.ROOT);
    }

    private static Map<String, PropertyDescriptor> buildPropertyDescriptorMap(Class<?> clazz) {
        Map<String, PropertyDescriptor> map = new HashMap<>();
        try {
            PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
            if (pds != null) {
                for (PropertyDescriptor pd : pds) {
                    if (pd.getReadMethod() != null) {
                        map.put(normalize(pd.getName()), pd);
                        map.put(pd.getName(), pd);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to introspect properties of {}: {}", clazz.getName(), e.getMessage());
        }
        return map;
    }

    private static Map<String, Method> buildGetterMap(Class<?> clazz) {
        Map<String, Method> map = new HashMap<>();
        try {
            for (Method m : clazz.getMethods()) {
                if (m.getParameterCount() == 0 && m.getReturnType() != void.class) {
                    String name = m.getName();
                    if (name.startsWith("get") && name.length() > 3) {
                        String prop = name.substring(3);
                        map.put(normalize(prop), m);
                        map.put(prop, m);
                    } else if (name.startsWith("is") && name.length() > 2 && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) {
                        String prop = name.substring(2);
                        map.put(normalize(prop), m);
                        map.put(prop, m);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to introspect methods of {}: {}", clazz.getName(), e.getMessage());
        }
        return map;
    }
}
