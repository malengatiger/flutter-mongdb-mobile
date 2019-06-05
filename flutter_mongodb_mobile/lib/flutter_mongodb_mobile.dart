import 'dart:async';

import 'package:flutter/foundation.dart';
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

  static Future<String> get setAppID async {
    await _channel.invokeMethod('setAppID', 'myStichAppID');
    debugPrint(
        '🍎 MongoDB Stitch AppID has been set on the wild side: 🧩🧩🧩 ');
    return '🧩🧩🧩 appID has been set';
  }
}
