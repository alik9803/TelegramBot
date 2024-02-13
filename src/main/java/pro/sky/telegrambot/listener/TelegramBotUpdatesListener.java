package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.BotService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private BotService botService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message = update.message();
            if (message == null) {
                logger.error("Received unsupported message type" + update);
                return;
            }
            long chatId = message.chat().id();
            String taskMessage = message.text();
            if ("/info".equals(taskMessage)) {
                String messageTmp = "Введите задачу в формате <01.01.2022 20:00 Сделать работу>";
                SendMessage sendMessage = new SendMessage(chatId, messageTmp);
                telegramBot.execute(sendMessage);
                return;
            }
            if ("/start".equals(taskMessage)) {
                String messageTmp = "Для работы с ботом введите /info";
                SendMessage sendMessage = new SendMessage(chatId, messageTmp);
                telegramBot.execute(sendMessage);
                return;
            }
            if (botService.addTask(taskMessage, chatId)) {
                telegramBot.execute(new SendMessage(chatId, "Задача успешно добавлена."));
            } else {
                telegramBot.execute(new SendMessage(chatId, "Ошибка формата ввода."));
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        logger.info("Метод run() по рассписанию выполнился");
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        botService.getTaskInMinute(dateTime).forEach(task ->
                telegramBot.execute(new SendMessage(task.getChat_id(),
                        String.format("%s %s", task.getDate(), task.getNotification_task())))
        );
    }
}
