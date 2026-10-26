package com.example.clouddisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.domain.Entity.CloudDiskSystem.CloudRepository;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网盘仓库 Mapper（cloud_repository）。
 */
@Mapper
public interface CloudRepositoryMapper extends BaseMapper<CloudRepository> {
}
