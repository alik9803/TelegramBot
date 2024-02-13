package pro.sky.telegrambot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.TaskRepository;
import pro.sky.telegrambot.service.BotServiceImp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class BotServiceImpTest {

    private static final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private static final String VALID_TASK_STRING = "10.10.2024 12:00 Описание задачи";
    private static final String INVALID_TASK_STRING = "Недопустимая строка задачи";

    @Mock
    private TaskRepository repository;

    private BotServiceImp botService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        botService = new BotServiceImp(repository);
    }

    @Test
    public void addTask_ValidTaskString_ReturnsTrue() {
        Matcher matcher = PATTERN.matcher(VALID_TASK_STRING);
        when(repository.save(any(NotificationTask.class))).thenReturn(null);

        boolean result = botService.addTask(VALID_TASK_STRING, 12345L);

        assertTrue(result);
        verify(repository, times(1)).save(any(NotificationTask.class));
    }

    @Test
    public void addTask_InvalidTaskString_ReturnsFalse() {

        boolean result = botService.addTask(INVALID_TASK_STRING, 12345L);


        assertFalse(result);
        verify(repository, never()).save(any(NotificationTask.class));
    }

    @Test
    public void getTaskInMinute_ValidDateTime_ReturnsListOfTasks() {

        LocalDateTime dateTime = LocalDateTime.parse("10.10.2024 12:00", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        List<NotificationTask> expectedTasks = List.of(new NotificationTask(12345L, "Описание задачи", dateTime));
        when(repository.getTaskInMinute(dateTime)).thenReturn(expectedTasks);

        List<NotificationTask> result = botService.getTaskInMinute(dateTime);

        assertEquals(expectedTasks, result);
        verify(repository, times(1)).getTaskInMinute(dateTime);
    }
}