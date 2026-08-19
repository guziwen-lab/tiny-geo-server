package com.supermap.gdal.encoding;

import com.supermap.command.CommandExecutor;
import com.supermap.command.CommandResult;
import com.supermap.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 无法区分时默认 UTF-8
 * @author gzw
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShapeEncodingDetector {

    private final CommandExecutor executor;

    private static final Set<Character> MESSY_CHARS = Set.of(
            '鍊', '鍖', '鍙', '鍥', '鍜', '鎴', '鏄', '鏈', '鐨', '涓',
            '浠', '鎺', '甯', '鏋', '鏃', '銆', '锛', '锟', '鑰', '鑴',
            '鐩', '鐪', '鎵', '鐢', '鎬', '濂', '娌', '娴', '寮', '瀛',
            '缂', '绗', '缁', '鏉', '鐭', '鐜'
    );

    private static final Set<Character> BAD_CHARS = Set.of(
            '�'
    );

    private static final int BAD_WEIGHT = 5;
    private static final int MESSY_WEIGHT = 2;

    private static final String[] ENCODINGS = {"UTF-8", "GBK"};

    public String detect(String sourcePath, String layerName) {
        int minPenaltyIndex = 0;

        String text = readShpAttr(sourcePath, layerName, ENCODINGS[minPenaltyIndex]);
        int minPenalty = getPenalty(text);
        if (minPenalty == 0) {
            return ENCODINGS[minPenaltyIndex];
        }

        for (int i = 1; i < ENCODINGS.length; i++) {
            text = readShpAttr(sourcePath, layerName, ENCODINGS[i]);
            int penalty = getPenalty(text);

            if (penalty == 0) {
                return ENCODINGS[i];
            }

            if (penalty < minPenalty) {
                minPenalty = penalty;
                minPenaltyIndex = i;
            }
        }

        return ENCODINGS[minPenaltyIndex];
    }

    private String readShpAttr(String sourcePath, String layerName, String encoding) {
        List<String> command = buildCommand(sourcePath, layerName, encoding);
        CommandResult result = executor.execute(command);

        String stdout = result.stdout();

        StringBuilder attributeText = new StringBuilder();
        stdout.lines().forEach(line -> {
            if (line.contains("(String) =")) {
                attributeText.append(line).append('\n');
            }
        });
        return attributeText.toString();
    }

    private static int getPenalty(String text) {
        int messy = 0;
        int bad = 0;

        for (char c : text.toCharArray()) {
            if (BAD_CHARS.contains(c)) {
                bad++;
            } else if (MESSY_CHARS.contains(c)) {
                messy++;
            }
        }

        return bad * BAD_WEIGHT + messy * MESSY_WEIGHT;
    }

    private List<String> buildCommand(String sourcePath, String layerName, String encoding) {
        List<String> command = new ArrayList<>();
        command.add("ogrinfo");

        if (StringUtils.isNotBlank(encoding)) {
            command.add("--config");
            command.add("SHAPE_ENCODING");
            command.add(encoding);
        }

        command.add("-al");
        command.add(sourcePath);

        if (layerName != null) {
            command.add(layerName);
        }

        command.add("-limit");
        command.add("1");

        return command;
    }

}
