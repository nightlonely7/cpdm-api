package com.fpt.cpdm.models.tasks;

import com.fpt.cpdm.models.documents.DocumentSummary;
import com.fpt.cpdm.models.tasks.task_issues.TaskIssueDetail;
import com.fpt.cpdm.models.users.UserSummary;

import java.util.List;

public interface TaskDetail extends TaskSummary {

    String getDescription();

    List<UserSummary> getRelatives();

    List<TaskIssueDetail> getIssues();

    List<DocumentSummary> getDocuments();
}
