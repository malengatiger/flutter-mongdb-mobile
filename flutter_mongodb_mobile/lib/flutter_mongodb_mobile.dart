import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class FlutterMongodbMobile {
  static const MethodChannel _channel =
      const MethodChannel('flutter_mongodb_mobile');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    debugPrint(
        '🍎 🍎 🍎  Flutter MongoDB Mobile Platform Version : 🧩🧩🧩  $version');
    return version;
  }
}
