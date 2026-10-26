package com.example.clouddisk.enums;

import lombok.Getter;

/**
 * 节点类型：区分文件夹与文件。
 *
 * <p>对应 {@code cloud_node.node_type} 字段。</p>
 */
@Getter
public enum NodeType {

    /** 文件夹 */
    FOLDER(1, "文件夹"),

    /** 文件 */
    FILE(2, "文件");

    /** 枚举编码 */
    private final int code;

    /** 枚举描述 */
    private final String desc;

    NodeType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 按编码解析枚举。
     *
     * @param code 枚举编码
     * @return 匹配的枚举；入参为 {@code null} 或未知编码时返回 {@code null}
     */
    public static NodeType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NodeType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
