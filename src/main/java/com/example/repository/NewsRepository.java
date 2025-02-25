package com.example.repository;

import com.example.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    boolean existsByText(String text);

    List<News> findByTimeAfter(LocalDateTime startOfDay);

    List<News> findByKeywordsContaining(String keyword);

    List<News> findByTimeBetween(LocalDateTime startOfYesterday, LocalDateTime endOfYesterday);

    List<News> findByKeywordsContainingIgnoreCase(String keyword);
}
