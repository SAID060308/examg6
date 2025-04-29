package com.example.examg6.repo;

import com.example.examg6.entity.Status;
import com.example.examg6.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(Status status);

    // Yoki @Query bilan
    @Query("SELECT t FROM Task t WHERE t.status = :status ORDER BY t.id DESC")
    List<Task> findTasksByStatus(@Param("status") Status status);
}
