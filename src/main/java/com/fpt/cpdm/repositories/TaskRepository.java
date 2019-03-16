package com.fpt.cpdm.repositories;

import com.fpt.cpdm.entities.TaskEntity;
import com.fpt.cpdm.entities.UserEntity;
import com.fpt.cpdm.models.tasks.TaskDetail;
import com.fpt.cpdm.models.tasks.TaskSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {

    TaskDetail findDetailById(Integer id);

    Page<TaskSummary> findSummaryByTitleContaining(String title, Pageable pageable);

    Page<TaskSummary> findSummaryByTitleAndSummaryAndDescriptionContaining(String title, String summary,
                                                                           String description, Pageable pageable);

    TaskSummary findSummaryById(Integer id);

    Page<TaskSummary> findAllSummaryByRelatives(UserEntity userEntity, Pageable pageable);

    Page<TaskSummary> findAllSummaryByExecutorAndIsAvailableTrue(UserEntity userEntity, Pageable pageable);

    Page<TaskSummary> findAllSummaryByCreatorAndTitleContainsAndSummaryContainsAndIsAvailableTrue(UserEntity userEntity, String title, String description, Pageable pageable);

    Boolean existsByCreatorOrExecutor(UserEntity creator, UserEntity executor);

}
