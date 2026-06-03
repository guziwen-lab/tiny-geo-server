package com.supermap.common.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ZipUtils {

    public static void zipDir(String dir, String zipFile) {
        zipDir(dir, zipFile, null);
    }

    public static void zipDir(String dir, String zipFile, String password) {
        zipDir(new File(dir), new File(zipFile), password);
    }

    public static void zipDir(File dir, File zipFile) {
        zipDir(dir, zipFile, null);
    }

    public static void zipDir(File dir, File zipFile, String password) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipParameters zipParameters = new ZipParameters();
            if (StringUtils.isNotEmpty(password)) {
                zipParameters.setEncryptFiles(true);
                zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
                zip.setPassword(password.toCharArray());
            }
            zip.addFolder(dir, zipParameters);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipFile(String file, String zipFile) {
        zipFile(new File(file), new File(zipFile), null);
    }

    public static void zipFile(String file, String zipFile, String password) {
        zipFile(new File(file), new File(zipFile), password);
    }

    public static void zipFile(File file, File zipFile) {
        zipFile(file, zipFile, null);
    }

    public static void zipFile(File file, File zipFile, String password) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            ZipParameters zipParameters = new ZipParameters();
            if (StringUtils.isNotEmpty(password)) {
                zipParameters.setEncryptFiles(true);
                zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
                zip.setPassword(password.toCharArray());
            }
            zip.addFile(file, zipParameters);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解压zip
     *
     * @param zipFile 压缩包
     * @param dest    目的目录
     */
    public static void unzip(String zipFile, String dest) {
        unzip(zipFile, dest, null);
    }

    /**
     * 解压zip
     *
     * @param zipFile  压缩包
     * @param dest     目的目录
     * @param password 解压密码
     */
    public static void unzip(String zipFile, String dest, String password) {
        unzip(new File(zipFile), new File(dest), password);
    }

    /**
     * 解压zip
     * 支持解压utf-8和gbk编码的文件名
     *
     * @param zipFile  压缩包
     * @param destDir  目的目录
     */
    public static void unzip(File zipFile, File destDir) {
        unzip(zipFile, destDir, null);
    }

    /**
     * 解压zip
     * 支持解压utf-8和gbk编码的文件名
     *
     * @param zipFile  压缩包
     * @param destDir  目的目录
     * @param password 解压密码
     */
    public static void unzip(File zipFile, File destDir, String password) {
        ZipFile zFile = new ZipFile(zipFile);
        try {
            List<FileHeader> headers = zFile.getFileHeaders();
            if (isMessyCode(headers)) {//判断文件名是否有乱码，有乱码，将编码格式设置成GBK
                zFile.close();
                zFile = new ZipFile(zipFile);
                zFile.setCharset(Charset.forName("GBK"));
            }
            if (!destDir.isDirectory() && !destDir.mkdirs()) {
                throw new RuntimeException("创建文件夹失败 " + destDir);
            }
            if (zFile.isEncrypted() && StringUtils.isNotEmpty(password)) {
                zFile.setPassword(password.toCharArray());
            }
            zFile.extractAll(destDir.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                zFile.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 解压fileHeaders
     *
     * @param zFile       压缩文件
     * @param fileHeaders 要解压的文件头
     * @param target      解压目标
     * @throws ZipException ZipException
     */
    public static void unZipByFileHeader(ZipFile zFile, List<FileHeader> fileHeaders, String target) throws ZipException {
        for (FileHeader extractHeader : fileHeaders) {
            zFile.extractFile(extractHeader, target);
        }
    }

    /**
     * 待解压的文件名是否乱码
     *
     * @param fileHeaders FileHeader
     * @return 是否乱码
     */
    private static boolean isMessyCode(List<FileHeader> fileHeaders) {
        for (FileHeader fileHeader : fileHeaders) {
            boolean canEnCode = Charset.forName("GBK").newEncoder().canEncode(fileHeader.getFileName());
            if (!canEnCode) { //canEnCode为true，表示不是乱码。false.表示乱码。是乱码则需要重新设置编码格式
                return true;
            }
        }
        return false;
    }

}
