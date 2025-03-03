package com.example.service;

import com.example.dto.NewsDTO;
import com.example.entity.News;
import com.example.mapper.NewsMapper;
import com.example.repository.NewsRepository;
import com.example.repository.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private NewsMapper mapper;

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private NewsServiceImpl newsService;

    private List<News> newsList;
    private List<NewsDTO> newsDTOList;

    @BeforeEach
    void setUp() {
        // Prepare entity test data
        News news1 = News.builder()
                .id(1L)
                .time(LocalDateTime.now())
                .keywords("test, news")
                .text("Test news content 1")
                .build();

        News news2 = News.builder()
                .id(2L)
                .time(LocalDateTime.now())
                .keywords("important, update")
                .text("Test news content 2")
                .build();

        newsList = Arrays.asList(news1, news2);

        // Prepare DTO test data
        NewsDTO newsDTO1 = new NewsDTO();
        // Set properties for newsDTO1

        NewsDTO newsDTO2 = new NewsDTO();
        // Set properties for newsDTO2

        newsDTOList = Arrays.asList(newsDTO1, newsDTO2);
    }

    @Test
    void getAllNews_ShouldReturnAllNewsMappedToDTO() {
        // Arrange
        when(newsRepository.findAll()).thenReturn(newsList);
        when(mapper.toDto(any(News.class))).thenReturn(newsDTOList.get(0), newsDTOList.get(1));

        // Act
        List<NewsDTO> result = newsService.getAllNews();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(newsDTOList, result);

        // Verify method calls
        verify(newsRepository, times(1)).findAll();
        verify(mapper, times(2)).toDto(any(News.class));
        verifyNoMoreInteractions(newsRepository);
        verifyNoInteractions(outboxRepository);
    }

    @Test
    void getAllNews_WithEmptyRepository_ShouldReturnEmptyList() {
        // Arrange
        when(newsRepository.findAll()).thenReturn(List.of());

        // Act
        List<NewsDTO> result = newsService.getAllNews();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify method calls
        verify(newsRepository, times(1)).findAll();
        verifyNoMoreInteractions(newsRepository);
        verifyNoInteractions(mapper); // Mapper shouldn't be called for empty list
        verifyNoInteractions(outboxRepository);
    }
}