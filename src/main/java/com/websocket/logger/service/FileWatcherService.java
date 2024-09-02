package com.websocket.logger.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.RandomAccessFile;

@Component
public class FileWatcherService {
    private long offset = 0;

    private final RandomAccessFile randomAccessFile;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public FileWatcherService() throws IOException {
        randomAccessFile = new RandomAccessFile("log.txt", "r");
        offset = initializeOffset();
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 5000)
    public void sendUpdates() throws IOException {
        long fileLength = randomAccessFile.length();
        randomAccessFile.seek(offset);

        while (randomAccessFile.getFilePointer() < fileLength) {
            String latestFileData = randomAccessFile.readLine();
            String encodedString = objectMapper.writeValueAsString(latestFileData)
                    .replace("\\u0000", "")
                    .replace("\"", "");
            String payload = "{\"content\":\"" + encodedString + "\"}";
            simpMessagingTemplate.convertAndSend("/topic/log", payload);
        }
        offset = fileLength;
    }

    private long initializeOffset() throws IOException {
        int numberOfLines = 0;

        while (randomAccessFile.readLine() != null)
            numberOfLines++;

        if (numberOfLines > 10) {
            offset = numberOfLines - 10;
        }
        return offset;
    }
}
