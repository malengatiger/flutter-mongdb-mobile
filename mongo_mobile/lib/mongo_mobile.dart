import 'dart:async';

import 'package:flutter/services.dart';

class MongoMobile {
  static const MethodChannel _channel =
      const MethodChannel('mongo_mobile');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
