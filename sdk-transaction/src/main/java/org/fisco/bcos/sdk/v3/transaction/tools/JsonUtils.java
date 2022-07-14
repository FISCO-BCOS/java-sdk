/**
 * Copyright 2014-2019 the original author or authors.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.transaction.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.v3.transaction.model.exception.JsonException;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a useful toolkit of json based on Jackson.
 *
 * @author maojiayu
 */
public class JsonUtils {

    private JsonUtils() {}

    protected static Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return fromJsonWithException(json, clazz);
        } catch (Exception e) {
            log.error("json is: " + json, e);
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public static <T> T fromJson(String json, Class<T> c, Class... t) {
        try {
            return fromJsonWithException(json, c, t);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static <T> T fromJson(String json, JavaType type) {
        try {
            return fromJsonWithException(json, type);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("json is: {}, e:", json, e);
            return null;
        }
    }

    public static <T> T fromJsonWithException(String json, Class<T> clazz)
            throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }

    @SuppressWarnings("rawtypes")
    public static <T> T fromJsonWithException(String json, Class<T> c, Class... t)
            throws IOException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(c, t);
        return objectMapper.readValue(json, javaType);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJsonWithException(String json, JavaType type)
            throws JsonProcessingException {
        return (T) objectMapper.readValue(json, type);
    }

    public static <T> List<T> fromJsonList(String json, Class<T> c) {
        try {
            return fromJsonListWithException(json, c);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> fromJsonListWithException(String json, Class<T> c)
            throws IOException {
        JavaType type = getCollectionType(ArrayList.class, c);
        return (List<T>) objectMapper.readValue(json, type);
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper
                .getTypeFactory()
                .constructParametricType(collectionClass, elementClasses);
    }

    public static String toJsonWithException(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    public static String toJson(Object o) {
        try {
            return toJsonWithException(o);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    public static <T, K> Map<T, K> convertValue(
            Object req, Class<T> keyClazz, Class<K> valueClazz) {
        return objectMapper.convertValue(
                req,
                objectMapper.getTypeFactory().constructMapType(Map.class, keyClazz, valueClazz));
    }

    @SuppressWarnings("rawtypes")
    public static <T> T convertMap(Map map, Class<T> retClazz) {
        return objectMapper.convertValue(map, retClazz);
    }
}
