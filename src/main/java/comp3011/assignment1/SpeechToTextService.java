package comp3011.assignment1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SpeechToTextService {

    // set up the openai api
    private final RestClient restClient;
    private final String apiKey;
    private final ServerStats serverStats;

    public SpeechToTextService(RestClient.Builder restClientBuilder,
            @Value("${openai.api.key}") String apiKey,
            ServerStats serverStats) {
        this.restClient = restClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
        this.serverStats = serverStats;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(SpeechToTextService.class);

    public String transcribe(byte[] audioBytes, String contentType) {
        String filename = filenameFor(contentType);

        // turn the audio into a file
        ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        // add the audio and model
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", audioResource);
        body.add("model", "gpt-4o-transcribe");

        // send audio to openai
        TranscriptionResponse response = restClient.post()
                .uri("/audio/transcriptions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(TranscriptionResponse.class);


        if (response == null) {
            logger.warn("OpenAI returned an empty response body");
            return "";
        }

        if (response.usage() != null) {
            serverStats.addTokens(response.usage().inputTokens(), response.usage().outputTokens());
        }

        logger.info("Transcription succeeded: {} input tokens, {} output tokens",
                response.usage() != null ? response.usage().inputTokens() : 0,
                response.usage() != null ? response.usage().outputTokens() : 0);

        return response.text();
    }
    
    // picks the right filename/extension based on what the client actually sent
    private String filenameFor(String contentType) {
        if (contentType == null) {
            return "recording.wav";
        }
        if (contentType.contains("webm")) {
            return "recording.webm";
        }
        if (contentType.contains("wav")) {
            return "recording.wav";
        }
        if (contentType.contains("ogg")) {
            return "recording.ogg";
        }
        return "recording.wav";
    }
}