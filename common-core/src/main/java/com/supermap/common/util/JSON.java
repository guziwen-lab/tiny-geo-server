package com.supermap.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JSON {

    private static ObjectMapper objectMapper;

    @Autowired
    public JSON(ObjectMapper mapper) {
        JSON.objectMapper = mapper;
    }

    /**
     * 将对象序列化为 JSON 字符串
     */
    public static String toJSONString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    /**
     * 将对象序列化为格式化 JSON 字符串
     */
    public static String toJSONString(Object obj, boolean prettyFormat) {
        try {
            if (prettyFormat) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                return objectMapper.writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为对象列表
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为任意复杂类型对象（使用 TypeReference）
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

}