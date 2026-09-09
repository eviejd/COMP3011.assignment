# Assignment 1: Speech-to-text web application

A Spring Boot web app that lets users record audio in their browser and turn it into text using OpenAI's speech-to-text API. Also includes admin and statistics endpoints

**Browser**: Records audio from the user's microphone, sends it to the backend, and displays the transcribed text

**Spring Boot**: Receives the audio and sends it to OpenAI using TranscribeController and SpeechToTextService

**OpenAI speech-to-text API**: Takes the audio file and returns the transcribed text

The browser sends the audio to our backend rather than directly to OpenAI. This also means the OpenAI API key is kept safely on the backend

### Design choices

**Dependency injection**

The controllers and services get what they need through their constructors instead of creating everything themselves. This makes the code easier to test and lets Spring handle setting everything up. It also means the ConcurrencyTest can start the app and use all of its required components without extra setup


**API key handling**

The OpenAI API key is stored in the OPENAI_API_KEY environment variable. It is loaded when the app starts and passed to SpeechToTextService. The key is never hardcoded, logged, or sent to the browser


**Error handling**

GlobalExceptionHandler handles errors for the whole application instead of having separate try/catch blocks in every controller method. It turns things like invalid input, OpenAI errors, and unexpected errors into consistent JSON responses that match the assignment's YAML specification


**Concurrency**

The app handles more than 200 requests at the same time without major delays or failures


**Handling simultaneous requests**

ServerStats uses AtomicLong and AtomicBoolean to safely update shared values when multiple requests happen at the same time. The shutdown endpoint uses compareAndSet(false, true) so that if multiple shutdown requests arrive together, only one can successfully start the shutdown. The others receive a 409 Conflict response. This is tested by ShutdownRaceConditionTest, which sends 100 shutdown attempts at the same time and checks that exactly one succeeds. The actual shutdown happens on a separate thread with a short delay. This gives the server enough time to send the successful response before it closes


### Configuration

The project uses Spring profiles to keep local and TITAN settings separate:

* application-local.properties
* application-titan.properties


### Testing

The project includes these tests:

**Assignment1ApplicationTests**: Checks that the application starts correctly

**ConcurrencyTest**: Sends 250 requests at the same time and checks that none fail

**ShutdownRaceConditionTest**: Sends 100 shutdown requests at the same time and checks that only one succeeds
