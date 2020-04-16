package com.visal.phraze.viewmodels;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.visal.phraze.R;

//Class which performs an asynchronous task to convert text to speech
public class SpeechTask extends AsyncTask<String, Void, String> {

    private static final String API_KEY = "FkcJ3Vew18INQ0K4voxc41b6sh4UCxhR_udlUiivfvL7";
    private static final String URL = "https://api.eu-gb.text-to-speech.watson.cloud.ibm.com/instances/a10ccd75-1141-4c16-8c4b-d1701d8b7ef5";
    private StreamPlayer player;
    private TextToSpeech textService;
    private ImageButton button;
    private ProgressBar progress;

    public SpeechTask(ImageButton button, ProgressBar progress) {
        player = new StreamPlayer();
        textService = initTextToSpeechService();
        this.button = button;
        this.progress = progress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        button.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                .text(strings[0])
                .voice(SynthesizeOptions.Voice.EN_US_LISAVOICE)
                .accept(HttpMediaType.AUDIO_WAV)
                .build();
        player.playStream(textService.synthesize(synthesizeOptions).execute().getResult());
        return "Did sythesize";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progress.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
    }

    private TextToSpeech initTextToSpeechService() {
        Authenticator authenticator = new
                IamAuthenticator(API_KEY);
        TextToSpeech service = new TextToSpeech(authenticator);
        service.setServiceUrl(URL);
        return service;
    }
}
