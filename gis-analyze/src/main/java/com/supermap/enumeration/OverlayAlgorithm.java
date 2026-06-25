package com.supermap.enumeration;

public enum OverlayAlgorithm {

    /**
     * 相交
     */
    INTERSECT,

    /**
     * 并集
     */
    UNION,

    /**
     * 擦除
     */
    ERASE,

    /**
     * 裁剪
     */
    CLIP,

    /**
     * 对称差
     */
    SYMMETRIC_DIFFERENCE,

    /**
     * 身份识别
     */
    IDENTITY
}