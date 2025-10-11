package nam.nam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
                ArrayList<Task> listTask = parseTasks();
                for (Task task : listTask) {
                    System.out.println(task.toString());
                }
                break;
            case "update":
                try{
                    updateTask(Integer.parseInt(args[1]), args[2]);
                }catch (NumberFormatException numberFormatException){
                    throw new NumberFormatException();
                }
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

    // Extract data from the JSON file into a String
    private static String readFileContent(String path) {
        try {
            return Files.readString(Path.of(path)).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Split String content of the JSON into tables of String that contains objects JSON
    private static String[] splitIntoObjects(String content) {
        content = content.substring(1, content.length() - 1).trim(); // remove [ ]
        return content.split("\\},\\s*\\{");
        // Regex to “Find a place in the text where a closing curly brace "}"
        // is followed by a comma ",", then maybe some spaces/newlines, then an opening curly brace "{".”
    }


    private static Task parseSingleTask(String obj) {
        obj = obj.replace("{", "").replace("}", "").trim(); // remove all {}
        String[] fields = obj.split(",\\s*"); // Separate each field of the JSON object

        int id = 0;
        String description = "";
        Status status = Status.todo;
        LocalDateTime createdAt = null, updatedAt = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        for (String field : fields) {
            String[] kv = field.split(":", 2);
            String key = kv[0].replace("\"", "").trim(); // remove all ("") of the key
            String value = kv[1].replace("\"", "").trim(); // remove all ("") of the value

            switch (key) {
                case "id" -> id = Integer.parseInt(value);
                case "description" -> description = value;
                case "status" -> status = Status.valueOf(value);
                case "createdAt" -> createdAt = LocalDateTime.parse(value, formatter);
                case "updatedAt" -> updatedAt = LocalDateTime.parse(value, formatter);
            }
        }

        return new Task(id, description, status, createdAt, updatedAt);
    }

    public static ArrayList<Task> parseTasks() {
        String content = readFileContent(FILE_PATH);
        String[] objects = splitIntoObjects(content);

        ArrayList<Task> tasks = new ArrayList<>();
        for (String obj : objects) {
            tasks.add(parseSingleTask(obj));
        }
        return tasks;
    }

    public static void updateTask(int id, String description){
        if(id <= 0) {
            System.out.println("Operation Failed, there's no item id = 0 or negative value");
        }
        String content = readFileContent(FILE_PATH);
        String[] objects = splitIntoObjects(content);

        if (id > objects.length) {
            System.out.println("Operation Failed, ID not found in list.");
            return;
        }

        Task updatedTask = parseSingleTask(objects[id-1]);
        updatedTask.setDescription(description);
        // Replace in array String objects
        objects[id-1] = updatedTask.toJSON().replace("{", "").replace("}", "");

        // Rebuild the JSON array
        StringBuilder newJson = new StringBuilder("[\n");
        for (int i = 0; i < objects.length; i++) {
            newJson.append(objects[i]);
            if (i < objects.length - 1){
                newJson.append("},\n{");
            }
        }
        newJson.append("\n]");
        // Write back to file
        try{
            Path path = Path.of(FILE_PATH);
            Files.writeString(path, newJson);
            System.out.println("Task updated successfully.");
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}