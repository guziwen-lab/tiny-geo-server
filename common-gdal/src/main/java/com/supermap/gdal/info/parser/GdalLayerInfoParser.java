package com.supermap.gdal.info.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gzw
 */
public class GdalLayerInfoParser {

    private static final Pattern LAYER_PATTERN1 = Pattern.compile("^Layer:\\s+(.+?)\\s*(?:\\(|$)");
    private static final Pattern LAYER_PATTERN2 = Pattern.compile("^\\d+:\\s*(.+?)\\s*\\(");

    public static List<String> parse(String stdout) {
        List<String> layers = new ArrayList<>();

        stdout.lines().forEach(line -> {
            Matcher matcher = LAYER_PATTERN1.matcher(line);
            if (matcher.find()) {
                layers.add(matcher.group(1));
                return;
            }

            matcher = LAYER_PATTERN2.matcher(line);
            if (matcher.find()) {
                layers.add(matcher.group(1));
            }
        });

        return layers;
    }

}
