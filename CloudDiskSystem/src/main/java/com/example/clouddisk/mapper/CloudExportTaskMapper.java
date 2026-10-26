package com.example.clouddisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.domain.Entity.CloudDiskSystem.CloudExportTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网盘导出任务 Mapper（cloud_export_task）。
 */
@Mapper
public interface CloudExportTaskMapper extends BaseMapper<CloudExportTask> {
}
