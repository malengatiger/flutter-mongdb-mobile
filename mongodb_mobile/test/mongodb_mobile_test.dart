import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mongodb_mobile/mongodb_mobile.dart';

void main() {
  const MethodChannel channel = MethodChannel('mongodb_mobile');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await MongodbMobile.platformVersion, '42');
  });
}
