package comp3011.assignment1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranscribeController {

    private final SpeechToTextService sttService;

    public TranscribeController(SpeechToTextService sttService) {
        this.sttService = sttService;
    }

    @PostMapping("/api/v1/transcribe")
    public ResponseEntity<String> receiveAudio(@RequestBody byte[] audioData) {
        String text = sttService.transcribe(audioData);
        return ResponseEntity.ok(text);
    }
}