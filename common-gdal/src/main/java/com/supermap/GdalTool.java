package com.supermap;

import com.supermap.info.LayerMeta;
import com.supermap.info.parser.GdalInfoParser;
import com.supermap.info.parser.GdalLayerInfoParser;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(GdalProperties.class)
public class GdalTool {

    private final CommandExecutor executor;

    private final GdalProperties gdalProperties;

    public List<String> listGdbLayers(String gdbPath) {
        List<String> command = new ArrayList<>();
        command.add(gdalProperties.getOgrInfo());
        command.add("-so");
        command.add(gdbPath);

        CommandResult execute = executor.execute(command);
        String stdout = execute.stdout();
        return GdalLayerInfoParser.parse(stdout);
    }

    public LayerMeta queryLayerMeta(String path, String layerName) {
        List<String> command = new ArrayList<>();
        command.add(gdalProperties.getOgrInfo());
        command.add("-so");
        command.add(path);
        command.add(layerName);

        CommandResult execute = executor.execute(command);
        String stdout = execute.stdout();
        return GdalInfoParser.parse(stdout);
    }

}
