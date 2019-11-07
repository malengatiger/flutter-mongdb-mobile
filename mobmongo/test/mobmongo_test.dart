import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mobmongo/mobmongo.dart';

void main() {
  const MethodChannel channel = MethodChannel('mobmongo');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Mobmongo.platformVersion, '42');
  });
}
