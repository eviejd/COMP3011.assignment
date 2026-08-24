package comp3011.assignment1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// store trancribsed text returned by stt api
@JsonIgnoreProperties(ignoreUnknown = true)
public record TranscriptionResponse(String text, Usage usage) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
            @JsonProperty("input_tokens") long inputTokens,
            @JsonProperty("output_tokens") long outputTokens
    ) {}
}

