package comp3011.assignment1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConcurrencyTest {

    @LocalServerPort
    private int port;

    @Test
    void handlesTwoHundredConcurrentRequestsWithoutFailure() throws Exception {
        int requestCount = 250; // more requests than necessary to be sure

        RestClient restClient = RestClient.create();
        String url = "http://localhost:" + port + "/api/v1/global/stats";

        ExecutorService executor = Executors.newFixedThreadPool(requestCount);
        AtomicInteger successCount = new AtomicInteger(0);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < requestCount; i++) {
            tasks.add(() -> {
                String body = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(String.class);
                if (body != null) {
                    successCount.incrementAndGet();
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        for (Future<Void> future : futures) {
            future.get();
        }

        assertEquals(requestCount, successCount.get(),
                "All " + requestCount + " concurrent requests should succeed");
    }
}