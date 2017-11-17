package com.hoppa.fluttertts;



import android.app.Activity;
import android.content.Intent;
import android.os.Build;


import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.speech.tts.TextToSpeech;


import java.util.ArrayList;
import java.util.Locale;

/**
 * SpeechRecognitionPlugin
 */
public class FlutterTtsPlugin implements MethodCallHandler {

  private static final String LOG_TAG = "SpeechRecognitionPlugin";
  private TextToSpeech t1;

  private MethodChannel speechChannel;
  String transcription = "";
  private boolean cancelled = false;
  private Intent recognizerIntent;
  private Activity activity;
  private boolean isLoaded = false;

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "speech_recognition");
    channel.setMethodCallHandler(new FlutterTtsPlugin(registrar.activity(), channel));
  }

  private FlutterTtsPlugin(Activity activity, MethodChannel channel) {
    this.speechChannel = channel;
    this.speechChannel.setMethodCallHandler(this);
    this.activity = activity;
    t1 = new TextToSpeech(activity.getApplicationContext(), onInitListener);


  }

  private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
    @Override
    public void onInit(int status) {
      if (status == TextToSpeech.SUCCESS) {
        int result = t1.setLanguage(Locale.getDefault());
        isLoaded = true;

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          Log.e("error", "This Language is not supported");
        }
      } else {
        Log.e("error", "Initialization Failed!");
      }
    }
  };
  private void speech(String textForReading) {
    ///
    int dividerLimit = 3900;
    if(textForReading.length() >= dividerLimit) {
      int textLength = textForReading.length();
      ArrayList<String> texts = new ArrayList<String>();
      int count = textLength / dividerLimit + ((textLength % dividerLimit == 0) ? 0 : 1);
      int start = 0;
      int end = textForReading.indexOf(" ", dividerLimit);
      for(int i = 1; i<=count; i++) {
        texts.add(textForReading.substring(start, end));
        start = end;
        if((start + dividerLimit) < textLength) {
          end = textForReading.indexOf(" ", start + dividerLimit);
        } else {
          end = textLength;
        }
      }
      for(int i=0; i<texts.size(); i++) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          t1.speak(texts.get(i), TextToSpeech.QUEUE_ADD, null, null);

        }
      }
    } else {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        t1.speak(textForReading, TextToSpeech.QUEUE_FLUSH, null, null);
      }
    }

  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {



    if (call.method.equals("speakText")) {


      speech(call.arguments.toString());




    }

    else if(call.method.equals("stopSpeak")){

      t1.stop();



    } else if(call.method.equals("setSpeechRate")) {

      float number = Float.parseFloat(call.arguments.toString());

      t1.setSpeechRate(number);
    }  else if(call.method.equals("setPitch")) {

      float number = Float.parseFloat(call.arguments.toString());

      t1.setPitch(number);
    } else if (call.method.equals("shutdown")) {

      t1.shutdown();
    }  else if (call.method.equals("isPlaying")) {
      result.success(t1.isSpeaking());

    } else

    {
      result.notImplemented();
    }
  }

}



