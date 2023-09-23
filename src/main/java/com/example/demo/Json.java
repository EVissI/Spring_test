package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.*;

import static java.lang.String.format;

public class Json implements Map<String, Object>, Serializable {
    private static final ObjectMapper OBJECT_MAPPER;

    private Map<String, Object> map;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        OBJECT_MAPPER = objectMapper;
    }

    public Json() {
        this.map = new LinkedHashMap<>();
    }

    public Json(final String key, final Object value) {
        this.map = new LinkedHashMap<>();
        this.map.put(key, value);
    }

    public Json(final Map<String, Object> map) {
        this.map = new LinkedHashMap<>(map);
    }

    @SuppressWarnings("unchecked")
    public Json(String someString) {
        try {
            this.map = OBJECT_MAPPER.readValue(someString, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMap(final Map<String, Object> map) {
        this.map = map;
    }

    public Json append(final String key, final Object value) {
        if (value == null) {
            remove(key);
        } else {
            map.put(key, value);
        }
        return this;
    }

    public Json appendAll(Map<String, Object> map) {
        if (!map.isEmpty()) {
            putAll(map);
        }
        return this;
    }

    public <T> T get(final Object key, final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz can not be null");
        }
        return clazz.cast(get(key));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Object key, final T defaultValue) {
        if (defaultValue == null) {
            throw new IllegalArgumentException("defaultValue can not be null");
        }
        Object value = get(key);
        return value == null ? defaultValue : (T) value;
    }

    public Integer getInteger(final Object key) {
        Number value = getNumber(key);
        if (value == null) return null;

        return value.intValue();
    }

    public int getInteger(final Object key, final int defaultValue) {
        Number value = getNumber(key);
        if (value == null) return defaultValue;

        return value.intValue();
    }

    public Long getLong(final Object key) {
        Number value = getNumber(key);
        if (value == null) return null;

        return value.longValue();
    }

    public Double getDouble(final Object key) {
        Number value = getNumber(key);
        if (value == null) return null;

        return value.doubleValue();
    }

    public String getString(final Object key) {
        return getString(key, null);
    }

    public String getString(final Object key, String defaultValue) {
        Object value = get(key);
        if (value == null) return defaultValue;

        return String.valueOf(value);
    }

    public Boolean getBoolean(final Object key) {
        return (Boolean) get(key);
    }

    public boolean getBoolean(final Object key, final boolean defaultValue) {
        return get(key, defaultValue);
    }

    public BigDecimal getBigDecimal(final Object key) {
        return (BigDecimal) get(key);
    }

    public <T> List<T> getList(final Object key, final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz can not be null");
        }
        return constructValuesList(key, clazz, null);
    }

    public <T> List<T> getList(final Object key, final Class<T> clazz, final List<T> defaultValue) {
        if (defaultValue == null) {
            throw new IllegalArgumentException("defaultValue can not be null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz can not be null");
        }
        return constructValuesList(key, clazz, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T extends Json> List<T> getList(final String key, final Class<T> clazz) {
        List<T> result = new ArrayList<>();
        List<Map<String, Object>> value = (List<Map<String, Object>>) get(key);

        if (value.isEmpty()) {
            return result;
        }
        for (Map<String, Object> map : value) {
            T t = newInstance(clazz);
            t.putAll(map);
            result.add(t);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> constructValuesList(final Object key, final Class<T> clazz, final List<T> defaultValue) {
        List<T> value = get(key, List.class);
        if (value == null) {
            return defaultValue;
        }

        for (Object item : value) {
            if (!clazz.isAssignableFrom(item.getClass())) {
                throw new ClassCastException(format("List element cannot be cast to %s", clazz.getName()));
            }
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public  <T extends Json> T getObject(Class<T> clazz, String field) {
        Object o = get(field);
        if (o == null) {
            return null;
        }

        if (clazz.isAssignableFrom(o.getClass())) {
            return (T) o;
        }

        T result = newInstance(clazz);
        result.putAll((Map<String, ?>) o);

        return result;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Json> T newInstance(Class<T> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return (T) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании объекта", e);
        }
    }

    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        if (value == null) {
            return remove(key);
        }

        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        if (map.isEmpty()) {
            return;
        }
        for (Map.Entry<? extends String, ?> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (!(o instanceof Json))
            return false;

        return map.equals(o);
    }

    private Number getNumber(final Object key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value);
        }
        if (value instanceof String) {
            String s = ((String) value).trim();
            if (s == null) return null;

            try {
                return Long.parseLong(s);
            } catch (Exception e) {
                try {
                    return Double.parseDouble(s);
                } catch (Exception ignore) {
                }
            }
        }

        throw new ClassCastException(value.getClass() + " cannot be cast to " + Number.class);
    }
}
