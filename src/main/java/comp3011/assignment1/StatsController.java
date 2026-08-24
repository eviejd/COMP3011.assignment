package comp3011.assignment1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    private final ServerStats serverStats;

    public StatsController(ServerStats serverStats) {
        this.serverStats = serverStats;
    }

    @GetMapping("/api/v1/global/stats")
    public GlobalStatsResponse stats() {
	// stays at 0/0 until SpeechToTextService adds the token counts
        return new GlobalStatsResponse(serverStats.getInputTokens(), serverStats.getOutputTokens());
    }
}