package com.supermap.analyze.helper;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author gzw
 */
public class UniqueFieldNameHelper {

    private final Set<String> usedNames = new LinkedHashSet<>();

    public void addUsedName(String name) {
        usedNames.add(name);
    }

    public String uniqueFieldName(String name) {
        String result = name;
        int i = 1;

        while (usedNames.contains(result)) {
            result = name + "_" + i++;
        }

        usedNames.add(result);
        return result;
    }

}
