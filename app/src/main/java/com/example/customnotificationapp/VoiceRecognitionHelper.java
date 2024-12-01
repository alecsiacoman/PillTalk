package com.example.customnotificationapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

public class VoiceRecognitionHelper{
    private Context context;
    private MainActivity mainActivity;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;

    public VoiceRecognitionHelper(Context context, MainActivity mainActivity){
        this.context = context;
        this.mainActivity = mainActivity;

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context);
        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener(this.context, this.mainActivity));

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    }

    public void startListening(){
        if(SpeechRecognizer.isRecognitionAvailable(context)){
            speechRecognizer.startListening(recognizerIntent);
        } else {
            Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show();
        }
    }
}
