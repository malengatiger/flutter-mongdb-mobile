import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_mongodb_mobile/Carrier.dart';

class FlutterMongodbMobile {
  static const MethodChannel _channel =
      const MethodChannel('flutter_mongodb_mobile');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    debugPrint(
        '🍎 🍎  FlutterMongodbMobile:platformVersion 🍎  Flutter MongoDB Mobile Platform Version : 🧩🧩🧩  $version');
    return version;
  }

  static Future<String> setAppID(dynamic config) async {
    var res = await _channel.invokeMethod('setAppID', config);
    debugPrint(
        '🍎 FlutterMongodbMobile: 🍀  Stitch AppID has been set, result: 🧩🧩🧩 $res');
    return '🧩🧩🧩 appID has been set';
  }

  static Future insert(Carrier carrier) async {
    var res = await _channel.invokeMethod('insert', carrier.toJson());
    debugPrint(
        '\n\n🍎 FlutterMongodbMobile: 🍀  record inserted, result: 🧩🧩🧩🧩🧩🧩🧩🧩 statusCode:  $res  🧩🧩🧩🧩🧩🧩🧩🧩🧩🧩🧩🧩');
    return res;
  }

  static Future getAll(Carrier carrier) async {
    var res = await _channel.invokeMethod('getAll', carrier.toJson());
    debugPrint(
        '\n\n🍎 FlutterMongodbMobile: 🍀  records retrieved, result from call: \n🧩🧩🧩🧩🧩🧩🧩🧩🧩🧩🧩🧩 '
        '\n\n$res\n🧩🧩🧩🧩🧩🧩🧩🧩🧩🧩🧩🧩\n');
    return res;
  }
}
