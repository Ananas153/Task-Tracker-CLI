package nam.nam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    private static final String FILE_PATH = "C:/Users/ngoth/Desktop/Task-Tracker-CLI/src/main/resources/TODO_List.json";
    private static int taskCounter = 0;

    public static void main(String[] args) {
        InitFileAndCounter();
        switch (args[0]) {
            case "add":
                addTask(args[1]);
                break;
            case "list":
                break;
            case "update":
                break;
            case "delete":
                break;
            default:
                System.out.println("Unknown Command, Bye Bye <3");
        }

        System.out.println("Bye Bye <3");
    }

    private static void InitFileAndCounter() {
        File file = new File(FILE_PATH);
        try {
            if (!file.isFile()) {
                Files.writeString(file.toPath(), "[]");
                System.out.println("Created new TODO_List.json file");
            }
            // Read and count items
            String content = Files.readString(file.toPath()).trim();
            if (content.equals("[]") || content.isEmpty()) {
                taskCounter = 0;
            } else {
                // take out all "id" from the file content, leaving chunks of the String,
                // then store them into an array [], then count the number using length
                taskCounter = content.split("\"id\"").length - 1;
            }
            System.out.println("Loaded " + taskCounter + " tasks.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize task file", e);
        }
    }

    public static void addTask(String description) {
        taskCounter++;
        Task newTask = new Task(taskCounter, description, Status.todo, LocalDateTime.now(), LocalDateTime.now());
        try {
            Path path = Path.of(FILE_PATH);
            String content = Files.readString(path).trim();

            if (content.equals("[]")) {
                content = "[\n" + newTask.toJSON() + "\n]";
            } else {
                // Insert before the closing bracket
                int closingIndex = content.lastIndexOf("]");
                content = content.substring(0, closingIndex).trim();
                if (!content.trim().endsWith("[")) {
                    content = content.trim() + ",";
                }
                content += "\n" + newTask.toJSON() + "\n]";
            }
            Files.writeString(path, content);
            System.out.println("Task added successfully (ID = " + taskCounter + ")");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}