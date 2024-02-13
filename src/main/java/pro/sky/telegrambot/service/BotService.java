package pro.sky.telegrambot.service;

import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface BotService {
    boolean addTask(String taskStr, long chatId);

    List<NotificationTask> getTaskInMinute(LocalDateTime dateTime);
}