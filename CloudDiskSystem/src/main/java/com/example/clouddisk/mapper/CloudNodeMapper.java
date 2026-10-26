package com.example.clouddisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.domain.Entity.CloudDiskSystem.CloudNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网盘文件/文件夹节点 Mapper（cloud_node）。
 */
@Mapper
public interface CloudNodeMapper extends BaseMapper<CloudNode> {
}
