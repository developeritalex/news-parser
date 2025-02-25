package com.example.mapper;

import com.example.dto.NewsDTO;
import com.example.entity.News;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    News toEntity(NewsDTO dto);
    NewsDTO toDto(News entity);
}
