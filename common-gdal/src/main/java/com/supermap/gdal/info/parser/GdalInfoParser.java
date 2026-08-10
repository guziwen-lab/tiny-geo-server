package com.supermap.gdal.info.parser;

import com.supermap.gdal.info.LayerMeta;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gzw
 */
public class GdalInfoParser {

    private static final Pattern geomPattern = Pattern.compile("^Geometry:\\s+(.+)$");
    private static final Pattern countPattern = Pattern.compile("^Feature Count:\\s+(\\d+)$");
    private static final Pattern epsgPattern = Pattern.compile("(?:ID\\[\"EPSG\",|AUTHORITY\\[\"EPSG\",\")(\\d+)\"?]");

    public static LayerMeta parse(String stdout) {
        AtomicReference<String> geomType = new AtomicReference<>();
        AtomicReference<Integer> srid = new AtomicReference<>();
        AtomicLong featureCount = new AtomicLong();

        stdout.lines().forEach(line -> {
            Matcher geomMatcher = geomPattern.matcher(line);
            if (geomMatcher.find()) {
                geomType.set(geomMatcher.group(1).trim());
                return;
            }

            Matcher countMatcher = countPattern.matcher(line);
            if (countMatcher.find()) {
                featureCount.set(Long.parseLong(countMatcher.group(1).trim()));
                return;
            }

            Matcher epsgMatcher = epsgPattern.matcher(line);
            if (epsgMatcher.find()) {
                srid.set(Integer.parseInt(epsgMatcher.group(1).trim()));
            }
        });

        return new LayerMeta(geomType.get(), srid.get(), featureCount.get());
    }

}
