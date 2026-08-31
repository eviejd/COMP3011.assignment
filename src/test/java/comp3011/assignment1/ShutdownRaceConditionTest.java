package comp3011.assignment1;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShutdownRaceConditionTest {

    @Test
    void onlyOneThreadCanWinTheShutdownRace() throws Exception {
        ServerStats serverStats = new ServerStats();
        int threadCount = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger winners = new AtomicInteger(0);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                // every thread tries at the same moment; only one should get true back
                if (serverStats.markShuttingDown()) {
                    winners.incrementAndGet();
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);
        executor.shutdown();
        for (Future<Void> future : futures) {
            future.get();
        }

        assertEquals(1, winners.get(),
                "Exactly one thread should win, even with " + threadCount + " threads racing at once");
    }
}