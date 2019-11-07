import 'dart:async';

import 'package:flutter/services.dart';

class Mobmongo {
  static const MethodChannel _channel =
      const MethodChannel('mobmongo');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
