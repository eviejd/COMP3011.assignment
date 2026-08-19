package comp3011.assignment1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// store trancribsed text returned by stt api
@JsonIgnoreProperties(ignoreUnknown = true)
public record TranscriptionResponse(String text) {}