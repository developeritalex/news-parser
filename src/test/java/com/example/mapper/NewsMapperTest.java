package com.example.mapper;

import com.example.dto.NewsDTO;
import com.example.entity.News;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NewsMapperTest {

    private NewsMapper newsMapper;

    @BeforeEach
    void setUp() {
        // Инициализация маппера вручную
        newsMapper = Mappers.getMapper(NewsMapper.class);
    }

    @Test
    void toDto_ShouldMapEntityToDto() {
        // Arrange
        News entity = new News();
        entity.setId(1L);
        entity.setTime(LocalDateTime.now());
        entity.setKeywords("test, important");
        entity.setText("This is a test news article");

        // Act
        NewsDTO result = newsMapper.toDto(entity);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getTime(), result.getTime());
        assertEquals(entity.getKeywords(), result.getKeywords());
        assertEquals(entity.getText(), result.getText());
    }

    @Test
    void toEntity_ShouldMapDtoToEntity() {
        // Arrange
        NewsDTO dto = new NewsDTO();
        dto.setTime(LocalDateTime.now());
        dto.setKeywords("test, important");
        dto.setText("This is a test news article");

        // Act
        News result = newsMapper.toEntity(dto);

        // Assert
        assertNotNull(result);
        assertNull(result.getId()); // ID не устанавливали, должно быть null
        assertEquals(dto.getTime(), result.getTime());
        assertEquals(dto.getKeywords(), result.getKeywords());
        assertEquals(dto.getText(), result.getText());
    }
}
