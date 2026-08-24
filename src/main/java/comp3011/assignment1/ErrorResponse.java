package comp3011.assignment1;
import java.time.Instant;

//json response for errors
public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {}