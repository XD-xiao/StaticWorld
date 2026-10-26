package com.example.clouddisk.enums;

import lombok.Getter;

/**
 * 仓库可见状态。
 *
 * <p>对应 {@code cloud_repository.visibility} 字段。</p>
 */
@Getter
public enum Visibility {

    /** 私密 */
    PRIVATE(0, "私密"),

    /** 公开 */
    PUBLIC(1, "公开");

    /** 枚举编码 */
    private final int code;

    /** 枚举描述 */
    private final String desc;

    Visibility(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 按编码解析枚举。
     *
     * @param code 枚举编码
     * @return 匹配的枚举；入参为 {@code null} 或未知编码时返回 {@code null}
     */
    public static Visibility fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (Visibility value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
