package com.supermap.util;

import com.supermap.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 无法区分时默认 UTF-8
 * @author gzw
 */
@Slf4j
public final class ShapeEncodingDetector {

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

    private ShapeEncodingDetector() {
    }

    public static String detect(String sourcePath, String layerName) {
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

    private static String readShpAttr(String sourcePath, String layerName, String encoding) {
        List<String> cmd = new ArrayList<>();
        cmd.add("ogrinfo");

        if (StringUtils.isNotBlank(encoding)) {
            cmd.add("--config");
            cmd.add("SHAPE_ENCODING");
            cmd.add(encoding);
        }

        cmd.add("-al");
        cmd.add(sourcePath);

        if (layerName != null) {
            cmd.add(layerName);
        }

        cmd.add("-limit");
        cmd.add("1");

        log.info("执行检查乱码命令: {}", String.join(" ", cmd));

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            StringBuilder attributeText = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("(String) =")) {
                        attributeText.append(line).append('\n');
                    }

                    output.append(line).append("\n");
                }
            }

            int code = process.waitFor();
            if (code != 0) {
                log.error("ogrinfo 检查乱码失败, exitCode={}, output={}", code, output);
                throw new RuntimeException("ogrinfo 检查乱码失败(exitCode=" + code + "): " + output);
            }

            return attributeText.toString();
        } catch (IOException e) {
            throw new RuntimeException("执行 ogrinfo 检查乱码失败，请确认已安装 GDAL", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("ogrinfo 检查乱码失败过程被中断", e);
        }
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

}
