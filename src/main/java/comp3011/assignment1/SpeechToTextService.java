package comp3011.assignment1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class SpeechToTextService {

	// set up the openai api
    private final RestClient restClient;
    private final String apiKey;

    public SpeechToTextService(RestClient.Builder restClientBuilder, @Value("${openai.api.key}") String apiKey) {
        this.restClient = restClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
    }

    public String transcribe(byte[] audioBytes) {
    	// turn the audio into a file
        ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
            	return "recording.webm";
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

     // return the transcribed text
        return response != null ? response.text() : "";
    }
}