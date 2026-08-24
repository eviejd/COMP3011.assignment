package comp3011.assignment1;
import java.time.Instant;

//returned as JSON with the server start time, current time and uptime
public record UptimeResponse(Instant utcServerStart, Instant utcNow, double serverUptimeSeconds) {}