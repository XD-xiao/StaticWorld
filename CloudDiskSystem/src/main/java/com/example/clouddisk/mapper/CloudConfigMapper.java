package com.example.clouddisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.domain.Entity.CloudDiskSystem.CloudConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网盘模块通用配置 Mapper（cloud_config）。
 */
@Mapper
public interface CloudConfigMapper extends BaseMapper<CloudConfig> {
}
