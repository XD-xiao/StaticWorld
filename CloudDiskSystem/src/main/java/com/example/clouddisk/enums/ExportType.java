package com.example.clouddisk.enums;

import lombok.Getter;

/**
 * 导出类型：区分文件夹导出与整个仓库导出。
 *
 * <p>对应 {@code cloud_export_task.export_type} 字段。</p>
 */
@Getter
public enum ExportType {

    /** 文件夹 */
    FOLDER(1, "文件夹"),

    /** 整个仓库 */
    REPOSITORY(2, "整个仓库");

    /** 枚举编码 */
    private final int code;

    /** 枚举描述 */
    private final String desc;

    ExportType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 按编码解析枚举。
     *
     * @param code 枚举编码
     * @return 匹配的枚举；入参为 {@code null} 或未知编码时返回 {@code null}
     */
    public static ExportType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ExportType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
