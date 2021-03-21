import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class MediaSocialShare {
  static const POST_SENT = "POST_SENT";
  static const FAIL_TO_POST = "FAIL_TO_POST";
  static const APP_NOT_FOUND = "APP_NOT_FOUND";

  static type(String appleStoreLink, String googlePlayLink) {
    return Platform.isIOS ? appleStoreLink : googlePlayLink;
  }

  static const MethodChannel _channel =
      const MethodChannel('media_social_share');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Map<dynamic, dynamic>> getFacebookUser() async {
    Map<dynamic, dynamic> result;
    try {
      result = await _channel.invokeMethod('getFacebookUser');
    } catch (e) {
      return null;
    }
    return result;
  }

  static Future<List<Map<dynamic, dynamic>>> getFacebookUserPages() async {
    List<Map<dynamic, dynamic>> result;
    try {
      result = await _channel.invokeListMethod('getFacebookUserPages');
    } catch (e) {
      return null;
    }
    return result;
  }

  static Future<String> shareOnFacebook(String url, String message,
      String accessToken, int time, String facebookId) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('url', () => url);
    arguments.putIfAbsent('message', () => message);
    arguments.putIfAbsent('accessToken', () => accessToken);
    arguments.putIfAbsent('time', () => time);
    arguments.putIfAbsent('facebookId', () => facebookId);
    try {
      return await _channel.invokeMethod('shareOnFacebook', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<String> shareStoryOnFacebook(
      String url, String facebookId) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('url', () => url);
    arguments.putIfAbsent('facebookId', () => facebookId);
    try {
      return await _channel.invokeMethod('shareStoryOnFacebook', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<String> shareStoryOnInstagram(String url) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('url', () => url);
    try {
      return await _channel.invokeMethod('shareStoryOnInstagram', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<String> sharePostOnInstagram(String url, String message) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('url', () => url);
    arguments.putIfAbsent('message', () => message);
    try {
      return await _channel.invokeMethod('sharePostOnInstagram', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<String> shareOnWhatsApp(String url, String message) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('url', () => url);
    arguments.putIfAbsent('message', () => message);
    try {
      return await _channel.invokeMethod('shareOnWhatsApp', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<String> shareOnWhatsAppBusiness(
      String url, String message) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('url', () => url);
    arguments.putIfAbsent('message', () => message);
    try {
      return await _channel.invokeMethod('shareOnWhatsAppBusiness', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<String> shareOnNative(String url, String message) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('url', () => url);
    arguments.putIfAbsent('message', () => message);
    try {
      return await _channel.invokeMethod('shareOnNative', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<String> shareContent(String content) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('content', () => content);
    try {
      return await _channel.invokeMethod('shareContent', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<String> openAppOnStore(String appUrl) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('appUrl', () => appUrl);
    dynamic result;
    try {
      result = await _channel.invokeMethod('openAppOnStore', arguments);
    } catch (e) {
      return (e as PlatformException).code;
    }
    return result;
  }

  static Future<String> shareLinkOnWhatsApp(String link) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('link', () => link);
    try {
      return await _channel.invokeMethod('shareLinkOnWhatsApp', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<String> shareLinkOnWhatsAppBusiness(String link) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('link', () => link);
    try {
      return await _channel.invokeMethod(
          'shareLinkOnWhatsAppBusiness', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }

  static Future<bool> checkPermissionToPublish() async {
    dynamic result;
    try {
      result = await _channel.invokeMethod('checkPermissionToPublish');
    } catch (e) {
      return false;
    }
    return result;
  }

  static Future<bool> shareLinkOnFacebook(String link) async {
    final Map<String, Object> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('link', () => link);
    try {
      return await _channel.invokeMethod('shareLinkOnFacebook', arguments);
    } on PlatformException catch (e) {
      throw e;
    }
  }
}
