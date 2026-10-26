package com.example.clouddisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.domain.Entity.CloudDiskSystem.CloudBlob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网盘内容对象 Mapper（cloud_blob）。
 */
@Mapper
public interface CloudBlobMapper extends BaseMapper<CloudBlob> {
}
