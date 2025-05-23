package com.example.examg6.repo;

import com.example.examg6.entity.Status;
import com.example.examg6.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    List<Status> findByIsActivePositionNumberNotNullOrderByIsActivePositionNumberAsc();
    List<Status> findByIsNotActivePositionNumberNotNullOrderByIsNotActivePositionNumberAsc();

    Status findByIsActivePositionNumber(Integer positionNumber);


}
