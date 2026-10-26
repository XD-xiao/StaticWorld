package com.example.clouddisk.enums;

import lombok.Getter;

/**
 * 导出任务状态：状态机单向流转（排队 → 打包中 → 成功 / 失败）。
 *
 * <p>对应 {@code cloud_export_task.status} 字段。</p>
 */
@Getter
public enum ExportStatus {

    /** 排队 */
    QUEUED(0, "排队"),

    /** 打包中 */
    PACKAGING(1, "打包中"),

    /** 成功 */
    SUCCESS(2, "成功"),

    /** 失败 */
    FAILED(3, "失败");

    /** 枚举编码 */
    private final int code;

    /** 枚举描述 */
    private final String desc;

    ExportStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 按编码解析枚举。
     *
     * @param code 枚举编码
     * @return 匹配的枚举；入参为 {@code null} 或未知编码时返回 {@code null}
     */
    public static ExportStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ExportStatus value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
