package com.example.clouddisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.domain.Entity.CloudDiskSystem.CloudFiletypeRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件类型黑名单配置 Mapper（cloud_filetype_rule）。
 */
@Mapper
public interface CloudFiletypeRuleMapper extends BaseMapper<CloudFiletypeRule> {
}
