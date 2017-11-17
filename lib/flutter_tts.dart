import 'dart:async';

import 'package:flutter/services.dart';

class FlutterTts {
   static String text="";

  static const MethodChannel _channel =
      const MethodChannel('speech_recognition');
        
  static void speak(text){

        _channel.invokeMethod('speakText',text.toString());
  }

  static void stop(){

        _channel.invokeMethod('stopSpeak');

  }

  static void setSpeechRate(double num){


          _channel.invokeMethod('setSpeechRate',num.toString());
      
  }
  static void shutDown(){


          _channel.invokeMethod('shutDown');
      
  }


  static void setPitch(double num){


          _channel.invokeMethod('setPitch',num.toString());
      
  }



  static Future<bool> get isplaying =>
      _channel.invokeMethod('isplaying');
}
