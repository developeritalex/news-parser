package com.example.controller;

import com.example.dto.NewsDTO;
import com.example.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsControllerTest {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;

    private List<NewsDTO> newsList;

    @BeforeEach
    void setUp() {
        // Prepare test data
        NewsDTO news1 = new NewsDTO();
        // Set news1 properties

        NewsDTO news2 = new NewsDTO();
        // Set news2 properties

        newsList = Arrays.asList(news1, news2);
    }

    @Test
    void getAllNews_ShouldReturnAllNews() {
        // Arrange
        when(newsService.getAllNews()).thenReturn(newsList);

        // Act
        List<NewsDTO> result = newsController.getAllNews();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(newsList, result);

        // Verify that service method was called exactly once
        verify(newsService, times(1)).getAllNews();
        verifyNoMoreInteractions(newsService);
    }
}