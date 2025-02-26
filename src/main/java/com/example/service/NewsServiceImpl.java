package com.example.service;

import com.example.dto.NewsDTO;
import com.example.entity.News;
import com.example.entity.Outbox;
import com.example.mapper.NewsMapper;
import com.example.repository.NewsRepository;
import com.example.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final NewsMapper mapper;
    private final OutboxRepository outboxRepository;

    @Override
    public List<NewsDTO> getAllNews() {
        return newsRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsDTO> getNewsToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return newsRepository.findByTimeAfter(startOfDay)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsDTO> getNewsYesterday() {
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        // Получаем конец вчерашнего дня (23:59:59)
        LocalDateTime endOfYesterday = startOfYesterday.plusHours(23).plusMinutes(59).plusSeconds(59);
        // Находим все новости, созданные вчера
        return newsRepository.findByTimeBetween(startOfYesterday, endOfYesterday)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsDTO> getNewsByKeyword(String keyword) {
        return newsRepository.findByKeywordsContaining(keyword)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNewsById(Long id) {
        outboxRepository.deleteById(id);
    }

    @Override
    public void deleteNewsByKeyword(String keyword) {
        List<News> list = newsRepository.findByKeywordsContainingIgnoreCase(keyword);
        newsRepository.deleteAll(list);
    }

    @Transactional
    @Override
    public NewsDTO addNews(NewsDTO newsDTO) {
        // Преобразуем NewsDTO в NewsEntity с помощью маппера
        News newsEntity = mapper.toEntity(newsDTO);
        // Сохраняем новость в основной таблице
        News savedEntityNews = newsRepository.save(newsEntity);
        // Сохраняем новость в таблице Outbox
        Outbox outboxEntity = new Outbox();
        outboxEntity.setTime(newsEntity.getTime());
        outboxEntity.setKeywords(newsEntity.getKeywords());
        outboxEntity.setText(newsEntity.getText());
        Outbox savedEntityOutbox = outboxRepository.save(outboxEntity);
        // Возвращаем DTO из сохранённой сущности News
        return mapper.toDto(savedEntityNews);
    }

}
