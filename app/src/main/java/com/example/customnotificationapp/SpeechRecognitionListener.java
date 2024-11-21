package com.example.customnotificationapp;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import java.util.ArrayList;

public class SpeechRecognitionListener implements RecognitionListener{

    private Context context;
    private MainActivity mainActivity;

    public SpeechRecognitionListener(Context context, MainActivity mainActivity){
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Toast.makeText(context, "Ready to listen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {
        Toast.makeText(context, "Listening...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        Toast.makeText(context, "Processing...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int error) {
        String errorMessage = "Error in speech recognition";
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage = "Client-side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "Insufficient permissions";
                break;
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if(matches != null && !matches.isEmpty()){
            String spokenText = matches.get(0);
            onVoiceCommandRecognized(spokenText);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public void onVoiceCommandRecognized(String command) {
        Toast.makeText(context, "Command recognized: " + command, Toast.LENGTH_SHORT).show();
        mainActivity.handleVoiceCommand(command);
    }

}
