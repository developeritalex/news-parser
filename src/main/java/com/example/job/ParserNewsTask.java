package com.example.job;

import com.example.dto.NewsDTO;
import com.example.entity.News;
import com.example.entity.Outbox;
import com.example.mapper.NewsMapper;
import com.example.repository.NewsRepository;
import com.example.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Component
public class ParserNewsTask {
    private static final String URL = "https://telegram.me/s/TVrain";
    private static final String[] KEYWORDS = {"Трамп", "Путин", "Маск", "Арестович", "Зеленский", "Лавров"};

    private final NewsRepository newsRepository;
    private final NewsMapper mapper;
    private final OutboxRepository outboxRepository;

    @Transactional
    //@Scheduled(cron = "0 0 11,16,20 * * *")
    @Scheduled(fixedDelay = 3_600_000) //раз в час
    public void parseAndSave() {
        try {
            Document doc = Jsoup
                    .connect(URL)
                    .get();

            Elements texts = doc.getElementsByClass("tgme_widget_message_text js-message_text");
            Elements times = doc.getElementsByClass("tgme_widget_message_date");

            Set<String> uniqueEntries = new HashSet<>();
            for (int i = 0; i < texts.size(); i++) {
                Element newsElement = texts.get(i);
                String text = newsElement.text();
                String time = times.get(i).text();

                Set<String> foundKeywords = new HashSet<>();
                for (String keyword : KEYWORDS) {
                    if (text.contains(keyword)) {
                        foundKeywords.add(keyword);
                    }
                }
                if (!foundKeywords.isEmpty()) {
                    String logMessage = time + "ч " + foundKeywords + " " + text;
                    if (uniqueEntries.add(logMessage)) {
                        log.info(logMessage);
                        if (!newsRepository.existsByText(text)) {
                            NewsDTO newsDTO = new NewsDTO();
                            newsDTO.setTime(LocalDateTime.now());
                            newsDTO.setKeywords(foundKeywords.toString());
                            newsDTO.setText(text);
                            News newz = mapper.toEntity(newsDTO);
                            newsRepository.saveAndFlush(newz);

                            if (!outboxRepository.existsByText(text)) {
                                Outbox outbox = new Outbox();
                                outbox.setTime(newz.getTime());
                                outbox.setKeywords(newz.getKeywords());
                                outbox.setText(newz.getText());
                                outboxRepository.saveAndFlush(outbox);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error parsing URL", e);
        }
    }
}