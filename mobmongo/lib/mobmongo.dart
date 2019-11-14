import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'carrier.dart';

class MobMongo {
  static const MethodChannel _channel = const MethodChannel('mobmongo');
  static const String LOCAL_DATABASE = "local",
      ATLAS_DATABASE = "atlas",
      CARRIER_DATABASE = "db",
      CARRIER_COLLECTION = "collection",
      MONGO_CHANGE_EVENTS = 'mongo_change_events';

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// set MongoDB appID
  static Future<String> setAppID(dynamic config) async {
    var res = await _channel.invokeMethod('setAppID', config);
    debugPrint(
        '🍎 MongodbMobile: 🍀  MongoDB Mobile should be connected, result: 🧩🧩🧩 $res');
    return '🧩🧩🧩 appID has been set';
  }

  /// start atlas sync collection
  static Future sync(Carrier carrier) async {
    var res = await _channel.invokeMethod('sync', carrier.toJson());
    return res;
  }

  /// insert one document intp collection
  static Future insert(Carrier carrier) async {
    var res = await _channel.invokeMethod('insert', carrier.toJson());
    return res;
  }

  /// delete one document from collection
  static Future delete(Carrier carrier) async {
    var res = await _channel.invokeMethod('delete', carrier.toJson());
    return res;
  }

  /// find one document from collection
  static Future getOne(Carrier carrier) async {
    var res = await _channel.invokeMethod('getOne', carrier.toJson());
    return res;
  }

  /// replace one document in collection
  static Future update(Carrier carrier) async {
    var res = await _channel.invokeMethod('update', carrier.toJson());
    return res;
  }

  /// add to nested array in document
  static Future addToArray(Carrier carrier) async {
    var res = await _channel.invokeMethod('addToArray', carrier.toJson());
    return res;
  }

  /// getAll - get all documents from collection
  static Future getAll(Carrier carrier) async {
    var res = await _channel.invokeMethod('getAll', carrier.toJson());
    return res;
  }

  /// query - get documents based on properties
  static Future query(Carrier carrier) async {
    var res = await _channel.invokeMethod('query', carrier.toJson());
    return res;
  }

  /// deleteMany - delete all docs in collection
  static Future deleteMany(Carrier carrier) async {
    var res = await _channel.invokeMethod('deleteMany', carrier.toJson());
    return res;
  }

  /// createIndex - create index on collection
  static Future createIndex(Carrier carrier) async {
    var res = await _channel.invokeMethod('createIndex', carrier.toJson());
    return res;
  }
}
