package comp3011.assignment1;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

//one shared instance used across the app
@Component
public class ServerStats {

    private final Instant startTime = Instant.now();

    // atomic keeps updates safe when multiple threads use this at once
    private final AtomicLong inputTokens = new AtomicLong(0);
    private final AtomicLong outputTokens = new AtomicLong(0);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    public Instant getStartTime() {
        return startTime;
    }

    public void addTokens(long input, long output) {
        inputTokens.addAndGet(input);
        outputTokens.addAndGet(output);
    }

    public long getInputTokens() {
        return inputTokens.get();
    }

    public long getOutputTokens() {
        return outputTokens.get();
    }

    // changes to true once and safely ignores later calls
    public boolean markShuttingDown() {
        return shuttingDown.compareAndSet(false, true);
    }
}