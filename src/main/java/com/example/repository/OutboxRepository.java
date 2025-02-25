package com.example.repository;

import com.example.entity.News;
import com.example.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    boolean existsByText(String text);

    List<Outbox> findByKeywordsContainingIgnoreCase(String keyword);

}
