import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_mongodb_mobile/Carrier.dart';
import 'package:flutter_mongodb_mobile/flutter_mongodb_mobile.dart';

void main() {
  debugPrint(
      '🍎 🍎 🍎  Flutter MongoDB Mobile Platform Example App starting ... : 🧩🧩🧩');
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterMongodbMobile.platformVersion;
      debugPrint('_MyAppState: 🧩🧩🧩 Platform Version : 🍎  $platformVersion');
      var res = await FlutterMongodbMobile.setAppID({
        'appID': 'exampleApp',
        'type': 'local',
      });
      print(res);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Future insertDocument() async {
    debugPrint('\n\n💙 💙  inserting sample document ....');
    dynamic result;
    try {
      var carrier = Carrier(db: 'testdb', collection: 'testCollection2', data: {
        'name': 'Tiger Malenga',
        'lastName': 'Malabie',
        'wealth': 9800000000.09,
        'date': DateTime.now().toUtc().toIso8601String(),
        'desc': '💙💙💙 serve with purpose 💙💙💙'
      });
      result = await FlutterMongodbMobile.insert(carrier);
      debugPrint(
          '\n\n🧩🧩🧩🧩🧩🧩  _MyAppState: insertDocument 🧩🧩🧩 document added : 🍎 statusCode: $result\n\n\n');
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      result = 'Failed to get platform version.';
      var d =
          "\ud83d\udc99\ud83d\udc99\ud83d\udc99 serve with purpose \ud83d\udc99\ud83d\udc99\ud83d\udc99";
    }
  }

  List documents = List();
  Future getAllDocuments() async {
    debugPrint('\n\n💙 💙  getAllDocuments ....');
    dynamic result;
    try {
      var carrier = Carrier(db: 'testdb', collection: 'testCollection2');
      documents = await FlutterMongodbMobile.getAll(carrier);
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState: getAllDocuments 🧩🧩🧩  retrieved : 🍎 ${documents.length} documents 🍎 \n\n\n');
      setState(() {});
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      result = 'Failed to get platform version.';
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('MongoDB Mobile Example App'),
          backgroundColor: Colors.deepOrange.shade600,
          bottom: PreferredSize(
            preferredSize: Size.fromHeight(100),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                children: <Widget>[
                  Text(
                    'Checking out Flutter MongoDB Plugin',
                    style: TextStyle(color: Colors.white),
                  ),
                ],
              ),
            ),
          ),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              SizedBox(
                height: 100,
              ),
              Text(
                '$_platformVersion\n',
                style: TextStyle(fontWeight: FontWeight.w900, fontSize: 24),
              ),
              Container(
                width: 300,
                child: RaisedButton(
                  onPressed: insertDocument,
                  elevation: 16,
                  color: Colors.pink.shade700,
                  child: Padding(
                    padding: const EdgeInsets.all(20.0),
                    child: Text(
                      'Insert One Document',
                      style: TextStyle(color: Colors.white),
                    ),
                  ),
                ),
              ),
              SizedBox(
                height: 20,
              ),
              Container(
                width: 300,
                child: RaisedButton(
                  onPressed: getAllDocuments,
                  elevation: 16,
                  color: Colors.purple.shade700,
                  child: Column(
                    children: <Widget>[
                      Padding(
                        padding: const EdgeInsets.all(20.0),
                        child: Text(
                          'Get All Documents',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              SizedBox(
                height: 8,
              ),
              documents.isEmpty
                  ? Container()
                  : Text(
                      documents.isEmpty ? "" : "${documents.length} documents",
                      style: TextStyle(color: Colors.purple, fontSize: 20),
                    ),
            ],
          ),
        ),
      ),
    );
  }
}
