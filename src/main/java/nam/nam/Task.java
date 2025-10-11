package nam.nam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    private String description;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(int id, String description, Status status, LocalDateTime createAt, LocalDateTime updateAt){
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createAt;
        this.updatedAt = updateAt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createAt=" + createdAt +
                ", updateAt=" + updatedAt +
                '}';
    }

    public String toJSON(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format(
                "{\n  \"id\": %d,\n  \"description\": \"%s\",\n  \"status\": \"%s\",\n  \"createdAt\": \"%s\",\n  \"updatedAt\": \"%s\"\n}",
                id, description, status.name(),
                createdAt.format(formatter),
                updatedAt.format(formatter)
        );
    }
}

