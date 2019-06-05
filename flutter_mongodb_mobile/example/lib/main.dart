import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
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
      await FlutterMongodbMobile.setAppID;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
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
          child: Text(
            '$_platformVersion\n',
            style: TextStyle(fontWeight: FontWeight.w900, fontSize: 24),
          ),
        ),
      ),
    );
  }
}
