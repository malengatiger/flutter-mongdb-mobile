import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

Future saveCollectionName(String token) async {
  debugPrint("✏️️ SharedPrefs saving token ..........");
  SharedPreferences prefs = await SharedPreferences.getInstance();
  prefs.setString("CollectionName", token);

  debugPrint("✏️️ CollectionName saved in prefs: 💙 💜   $token");
}

Future<String> getCollectionName() async {
  SharedPreferences prefs = await SharedPreferences.getInstance();
  var token = prefs.getString("CollectionName");
  debugPrint("✏️️ SharedPrefs - CollectionName from prefs: 🧡  $token");
  return token;
}

Future saveDatabaseName(String token) async {
  debugPrint("✏️️ SharedPrefs saving token ..........");
  SharedPreferences prefs = await SharedPreferences.getInstance();
  prefs.setString("DatabaseName", token);

  debugPrint("✏️️ DatabaseName saved in prefs: 💛 💚  $token");
}

Future<String> getDatabaseName() async {
  SharedPreferences prefs = await SharedPreferences.getInstance();
  var token = prefs.getString("DatabaseName");
  debugPrint("✏️️ SharedPrefs - DatabaseName from prefs:❤️ $token");
  return token;
}
Future saveAppID(String token) async {
  debugPrint("✏️️ SharedPrefs saving token ..........");
  SharedPreferences prefs = await SharedPreferences.getInstance();
  prefs.setString("appID", token);

  debugPrint("✏️️ AppID saved in prefs: ❤️ 🧡  $token");
}

Future<String> getAppID() async {
  SharedPreferences prefs = await SharedPreferences.getInstance();
  var token = prefs.getString("appID");
  debugPrint("✏️️ SharedPrefs - AppID from prefs: 💜 $token");
  return token;
}
