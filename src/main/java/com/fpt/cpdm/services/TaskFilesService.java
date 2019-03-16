package com.fpt.cpdm.services;

import com.fpt.cpdm.models.tasks.task_files.TaskFilesSummary;

import java.util.List;

public interface TaskFilesService {

    List<TaskFilesSummary> findSummaryByTask_Id(Integer id);
}
