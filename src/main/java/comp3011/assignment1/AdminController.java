package comp3011.assignment1;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
public class AdminController {

    private final ServerStats serverStats;
    private final ConfigurableApplicationContext context; // lets us shut the app down

    public AdminController(ServerStats serverStats, ConfigurableApplicationContext context) {
        this.serverStats = serverStats;
        this.context = context;
    }

    @GetMapping("/api/v1/admin/uptime")
    public UptimeResponse uptime() {
        Instant now = Instant.now();
        double seconds = Duration.between(serverStats.getStartTime(), now).toMillis() / 1000.0;
        return new UptimeResponse(serverStats.getStartTime(), now, seconds);
    }

    @PostMapping("/api/v1/admin/shutdown")
    public ResponseEntity<?> shutdown() {
        if (!serverStats.markShuttingDown()) {
            ErrorResponse error = new ErrorResponse(Instant.now(), 409, "Conflict",
                    "Graceful shutdown is already in progress.", "/api/v1/admin/shutdown");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        // wait a bit so the response gets sent before shutdown
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            context.close();
        }).start();

        return ResponseEntity.accepted().body(new ShutdownResponse("Graceful shutdown requested."));
    }
}