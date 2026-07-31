package com.supermap.util;

import com.supermap.common.util.ChineseUtils;
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

    private static final String[] encodings = {"UTF-8", "GBK"};

    private ShapeEncodingDetector() {
    }

    public static String detect(String sourcePath, String layerName) {
        List<List<Character>> chars = new ArrayList<>(encodings.length);
        for (String encoding : encodings) {
            String original = read(sourcePath, layerName, encoding);
            List<Character> chineseCharArray = getChineseCharArray(original);
            chars.add(chineseCharArray);
        }

        int i = score(chars);

        return encodings[i];
    }

    private static int score(List<List<Character>> chars) {
        List<ScoreData> scoreDataList = new ArrayList<>(chars.size());
        for (List<Character> aChar : chars) {
            int messyNum = countMessyCharacters(aChar);
            int badNum = countBadCharacters(aChar);

            ScoreData scoreData = new ScoreData(aChar.size(), messyNum, badNum);
            scoreDataList.add(scoreData);
        }

        int maxScoreIndex = 0;
        int maxScore = scoreDataList.get(0).getScore();
        for (int i = 1; i < scoreDataList.size(); i++) {
            int score = scoreDataList.get(i).getScore();
            if (score > maxScore) {
                maxScoreIndex = i;
                maxScore = score;
            }
        }

        return maxScoreIndex;
    }

    private static int countMessyCharacters(List<Character> characters) {
        return (int) characters.stream().filter(MESSY_CHARS::contains).count();
    }

    private static int countBadCharacters(List<Character> characters) {
        return (int) characters.stream().filter(BAD_CHARS::contains).count();
    }

    private static List<Character> getChineseCharArray(String str) {
        List<Character> chars = new ArrayList<>();
        for (char c : str.toCharArray()) {
            if (ChineseUtils.isChinese(c)) {
                chars.add(c);
            }
        }
        return chars;
    }

    private static String read(String sourcePath, String layerName, String encoding) {
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
            StringBuilder pendingJudgment = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("(String) =")) {
                        pendingJudgment.append(line).append('\n');
                    }

                    output.append(line).append("\n");
                }
            }

            int code = process.waitFor();
            if (code != 0) {
                log.error("ogrinfo 检查乱码失败, exitCode={}, output={}", code, output);
                throw new RuntimeException("ogrinfo 检查乱码失败(exitCode=" + code + "): " + output);
            }

            return pendingJudgment.toString();
        } catch (IOException e) {
            throw new RuntimeException("执行 ogrinfo 检查乱码失败，请确认已安装 GDAL", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("ogrinfo 检查乱码失败过程被中断", e);
        }
    }

    private record ScoreData(int charactersNum, int messyNum, int badNum) {

        public int getScore() {
            return -(5 * badNum + 2 * messyNum);
        }

    }

}
