package com.supermap.common.util;

import java.util.Base64;

public class Base64FileTypeDetectorUtils {

    public enum FileType {
        PDF, PNG, JPEG, GIF, UNKNOWN;

        public String getExtension() {
            return switch (this) {
                case PDF -> "pdf";
                case PNG -> "png";
                case JPEG -> "jpg";
                case GIF -> "gif";
                default -> "unknown";
            };
        }
    }

    /**
     * 判断 Base64 是哪种类型的文件
     *
     * @param base64Str Base64 字符串（可以是纯内容或 data URL）
     * @return FileType 枚举
     */
    public static FileType detect(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) return FileType.UNKNOWN;

        String cleanBase64 = extractPureBase64(base64Str);

        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(cleanBase64);
        } catch (IllegalArgumentException e) {
            return FileType.UNKNOWN;
        }

        if (bytes.length < 4) return FileType.UNKNOWN;

        // PDF：%PDF = 0x25 0x50 0x44 0x46
        if (bytes[0] == 0x25 && bytes[1] == 0x50 && bytes[2] == 0x44 && bytes[3] == 0x46) {
            return FileType.PDF;
        }

        // PNG：\x89PNG = 0x89 0x50 0x4E 0x47
        if ((bytes[0] & 0xFF) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) {
            return FileType.PNG;
        }

        // JPEG：0xFF 0xD8
        if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8) {
            return FileType.JPEG;
        }

        // GIF：GIF87a / GIF89a
        if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
            return FileType.GIF;
        }

        return FileType.UNKNOWN;
    }

    /**
     * 如果是 data URL 格式，提取纯 base64 内容
     *
     * @param base64Str 原始 base64 字符串
     * @return 纯 base64 数据（不包含 data:image/png;base64,）
     */
    private static String extractPureBase64(String base64Str) {
        if (base64Str.startsWith("data:")) {
            int commaIndex = base64Str.indexOf(',');
            if (commaIndex >= 0 && commaIndex < base64Str.length() - 1) {
                return base64Str.substring(commaIndex + 1);
            }
        }
        return base64Str;
    }

}