import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class FlutterMongodbMobile {
  static const MethodChannel _channel =
      const MethodChannel('flutter_mongodb_mobile');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    debugPrint(
        '🍎 🍎  FlutterMongodbMobile:platformVersion 🍎  Flutter MongoDB Mobile Platform Version : 🧩🧩🧩  $version');
    return version;
  }

  static Future<String> setAppID(String appID) async {
    var res = await _channel.invokeMethod('setAppID', {'appID': appID});
    debugPrint(
        '🍎 FlutterMongodbMobile:setAppID   🍀  MongoDB Stitch AppID has been set on the wild side: 🧩🧩🧩 $res');
    return '🧩🧩🧩 appID has been set';
  }
}
