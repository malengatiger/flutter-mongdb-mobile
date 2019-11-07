import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mongo_mobile/mongo_mobile.dart';

void main() {
  const MethodChannel channel = MethodChannel('mongo_mobile');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await MongoMobile.platformVersion, '42');
  });
}
