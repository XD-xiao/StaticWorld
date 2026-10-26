package com.example.clouddisk.enums;

import lombok.Getter;

/**
 * 文件类型黑名单规则类型：按扩展名或 MIME 类型匹配。
 *
 * <p>对应 {@code cloud_filetype_rule.rule_type} 字段。</p>
 */
@Getter
public enum FileTypeRuleType {

    /** 扩展名 */
    EXTENSION(1, "扩展名"),

    /** MIME 类型 */
    MIME(2, "MIME 类型");

    /** 枚举编码 */
    private final int code;

    /** 枚举描述 */
    private final String desc;

    FileTypeRuleType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 按编码解析枚举。
     *
     * @param code 枚举编码
     * @return 匹配的枚举；入参为 {@code null} 或未知编码时返回 {@code null}
     */
    public static FileTypeRuleType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FileTypeRuleType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
