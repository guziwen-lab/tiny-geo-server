package com.supermap.common.util;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author gzw
 */
public class CollectionUtils extends CollectionUtil {

    public static <T, K, V> Map<K, V> toMap(Collection<T> list,
                                            Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends V> valueMapper) {
        if (list == null) {
            return new HashMap<>();
        } else {
            Map<K, V> map = new HashMap<>();
            for (T t : list) {
                if (t != null) {
                    map.put(keyMapper.apply(t), valueMapper.apply(t));
                }
            }
            return map;
        }
    }

    public static <T, K, V> Map<K, List<V>> groupByToMap(Collection<T> list,
                                                         Function<? super T, ? extends K> keyMapper,
                                                         Function<? super T, ? extends V> valueMapper) {
        return list.stream()
                .collect(Collectors.groupingBy(keyMapper, Collectors.mapping(valueMapper, Collectors.toList())));
    }

}
