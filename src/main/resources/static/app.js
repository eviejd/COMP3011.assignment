const recordButton = document.getElementById('recordButton');
const statusElement = document.getElementById('status');
const resultElement = document.getElementById('result');

let mediaRecorder;
let audioChunks = [];
let isRecording = false;

recordButton.addEventListener('click', async () => {
    if (!isRecording) {
        await startRecording();
    } else {
        stopRecording();
    }
});

async function startRecording() {
	// get access to the microphone
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    mediaRecorder = new MediaRecorder(stream);
    audioChunks = [];

    mediaRecorder.ondataavailable = (event) => {
		// store the recorded audio
        audioChunks.push(event.data);
    };

    mediaRecorder.onstop = async () => {
		// turn the recording into a file
        const audioBlob = new Blob(audioChunks, { type: 'audio/webm' });
        await uploadAudio(audioBlob);
		// stop the microphone after recording finishes
        stream.getTracks().forEach(track => track.stop());
    };

    mediaRecorder.start();
    isRecording = true;
    recordButton.textContent = 'Stop Recording';
    statusElement.textContent = 'Recording...';
}

function stopRecording() {
    mediaRecorder.stop();
    isRecording = false;
    recordButton.textContent = 'Start Recording';
    statusElement.textContent = 'Processing...';
}

async function uploadAudio(audioBlob) {
    try {
		// send the audio to the server
        const response = await fetch('/api/v1/transcribe', {
            method: 'POST',
            headers: { 'Content-Type': 'application/octet-stream' },
            body: audioBlob
        });

		// get the text from the response
		const text = await response.text();
        resultElement.textContent = text;
        statusElement.textContent = 'Ready';
    } catch (error) {
        resultElement.textContent = 'Error: ' + error.message;
        statusElement.textContent = 'Ready';
    }
}