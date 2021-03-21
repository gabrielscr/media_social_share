import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:media_social_share/media_social_share.dart';

void main() {
  const MethodChannel channel = MethodChannel('media_social_share');

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
    expect(await MediaSocialShare.platformVersion, '42');
  });
}
