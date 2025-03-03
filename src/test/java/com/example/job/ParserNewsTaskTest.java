package com.example.job;

import com.example.dto.NewsDTO;
import com.example.entity.News;
import com.example.entity.Outbox;
import com.example.mapper.NewsMapper;
import com.example.repository.NewsRepository;
import com.example.repository.OutboxRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParserNewsTaskTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private Document document;

    @Mock
    private Connection connection;

    @Mock
    private Elements textElements;

    @Mock
    private Elements timeElements;

    @Mock
    private Element textElement;

    @Mock
    private Element timeElement;

    @Captor
    private ArgumentCaptor<News> newsCaptor;

    @Captor
    private ArgumentCaptor<Outbox> outboxCaptor;

    @InjectMocks
    private ParserNewsTask parserNewsTask;

    private News mockNews;

    @BeforeEach
    void setUp() {
        mockNews = new News();
        mockNews.setTime(LocalDateTime.now());
        mockNews.setKeywords("[Путин]");
        mockNews.setText("Новость про Путина");
    }

    @Test
    void parseAndSave_shouldSaveNewsAndOutbox_whenFoundNewsWithKeywords() throws IOException {
        // Given
        try (MockedStatic<Jsoup> jsoupMockedStatic = mockStatic(Jsoup.class)) {
            jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
            when(connection.get()).thenReturn(document);

            when(document.getElementsByClass("tgme_widget_message_text js-message_text")).thenReturn(textElements);
            when(document.getElementsByClass("tgme_widget_message_date")).thenReturn(timeElements);

            when(textElements.size()).thenReturn(1);
            when(timeElements.size()).thenReturn(1);

            when(textElements.get(0)).thenReturn(textElement);
            when(timeElements.get(0)).thenReturn(timeElement);

            when(textElement.text()).thenReturn("Новость про Путина");
            when(timeElement.text()).thenReturn("12:00");

            when(newsRepository.existsByText(anyString())).thenReturn(false);
            when(outboxRepository.existsByText(anyString())).thenReturn(false);
            when(newsMapper.toEntity(any(NewsDTO.class))).thenReturn(mockNews);

            // When
            parserNewsTask.parseAndSave();

            // Then
            verify(newsRepository).save(newsCaptor.capture());
            News savedNews = newsCaptor.getValue();
            assertEquals("Новость про Путина", savedNews.getText());
            assertEquals("[Путин]", savedNews.getKeywords());
            assertNotNull(savedNews.getTime());

            verify(outboxRepository).save(outboxCaptor.capture());
            Outbox savedOutbox = outboxCaptor.getValue();
            assertEquals("Новость про Путина", savedOutbox.getText());
            assertEquals("[Путин]", savedOutbox.getKeywords());
            assertEquals(savedNews.getTime(), savedOutbox.getTime());
        }
    }

    @Test
    void parseAndSave_shouldNotSaveNewsOrOutbox_whenNewsAlreadyExists() throws IOException {
        // Given
        try (MockedStatic<Jsoup> jsoupMockedStatic = mockStatic(Jsoup.class)) {
            jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
            when(connection.get()).thenReturn(document);

            when(document.getElementsByClass("tgme_widget_message_text js-message_text")).thenReturn(textElements);
            when(document.getElementsByClass("tgme_widget_message_date")).thenReturn(timeElements);

            when(textElements.size()).thenReturn(1);
            when(timeElements.size()).thenReturn(1);

            when(textElements.get(0)).thenReturn(textElement);
            when(timeElements.get(0)).thenReturn(timeElement);

            when(textElement.text()).thenReturn("Новость про Путина");
            when(timeElement.text()).thenReturn("12:00");

            when(newsRepository.existsByText(anyString())).thenReturn(true);

            // When
            parserNewsTask.parseAndSave();

            // Then
            verify(newsRepository, never()).save(any(News.class));
            verify(outboxRepository, never()).save(any(Outbox.class));
        }
    }

    @Test
    void parseAndSave_shouldSaveNewsButNotOutbox_whenOutboxAlreadyExists() throws IOException {
        // Given
        try (MockedStatic<Jsoup> jsoupMockedStatic = mockStatic(Jsoup.class)) {
            jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
            when(connection.get()).thenReturn(document);

            when(document.getElementsByClass("tgme_widget_message_text js-message_text")).thenReturn(textElements);
            when(document.getElementsByClass("tgme_widget_message_date")).thenReturn(timeElements);

            when(textElements.size()).thenReturn(1);
            when(timeElements.size()).thenReturn(1);

            when(textElements.get(0)).thenReturn(textElement);
            when(timeElements.get(0)).thenReturn(timeElement);

            when(textElement.text()).thenReturn("Новость про Путина");
            when(timeElement.text()).thenReturn("12:00");

            when(newsRepository.existsByText(anyString())).thenReturn(false);
            when(outboxRepository.existsByText(anyString())).thenReturn(true);
            when(newsMapper.toEntity(any(NewsDTO.class))).thenReturn(mockNews);

            // When
            parserNewsTask.parseAndSave();

            // Then
            verify(newsRepository).save(any(News.class));
            verify(outboxRepository, never()).save(any(Outbox.class));
        }
    }

    @Test
    void parseAndSave_shouldNotSaveNewsOrOutbox_whenNoKeywordsFound() throws IOException {
        // Given
        try (MockedStatic<Jsoup> jsoupMockedStatic = mockStatic(Jsoup.class)) {
            jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
            when(connection.get()).thenReturn(document);

            when(document.getElementsByClass("tgme_widget_message_text js-message_text")).thenReturn(textElements);
            when(document.getElementsByClass("tgme_widget_message_date")).thenReturn(timeElements);

            when(textElements.size()).thenReturn(1);
            when(timeElements.size()).thenReturn(1);

            when(textElements.get(0)).thenReturn(textElement);
            when(timeElements.get(0)).thenReturn(timeElement);

            when(textElement.text()).thenReturn("Новость без ключевых слов");
            when(timeElement.text()).thenReturn("12:00");

            // When
            parserNewsTask.parseAndSave();

            // Then
            verify(newsRepository, never()).save(any(News.class));
            verify(outboxRepository, never()).save(any(Outbox.class));
        }
    }

    @Test
    void parseAndSave_shouldHandleIOException() throws IOException {
        // Given
        try (MockedStatic<Jsoup> jsoupMockedStatic = mockStatic(Jsoup.class)) {
            jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
            when(connection.get()).thenThrow(new IOException("Connection error"));

            // When
            parserNewsTask.parseAndSave();

            // Then
            verify(newsRepository, never()).save(any(News.class));
            verify(outboxRepository, never()).save(any(Outbox.class));
        }
    }
}