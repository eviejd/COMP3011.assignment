package comp3011.assignment1;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranscribeController {

    // service for converting recorded audio to text
    private final SpeechToTextService sttService;

    public TranscribeController(SpeechToTextService sttService) {
        this.sttService = sttService;
    }

    // receives the recorded audio from the frontend
    @PostMapping("/api/v1/transcribe")
    public ResponseEntity<String> receiveAudio(
            @RequestBody byte[] audioData,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType) {

        // send audio to openai stt service
        String text = sttService.transcribe(audioData, contentType);

        // return transcribed text to frontend
        return ResponseEntity.ok(text);
    }
}