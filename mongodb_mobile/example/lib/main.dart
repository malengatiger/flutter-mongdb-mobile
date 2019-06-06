import 'dart:async';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:mongodb_mobile/carrier.dart';
import 'package:mongodb_mobile/mongodb_mobile.dart';

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
      platformVersion = await MongodbMobile.platformVersion;
      debugPrint('_MyAppState: 🧩🧩🧩 Platform Version : 🍎  $platformVersion');
      var res = await MongodbMobile.setAppID({
        'appID': 'exampleApp',
        'type': 'local',
      });
      print(res);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;

    setState(() {});
  }

  Random random = Random(DateTime.now().millisecondsSinceEpoch);

  /// Add document to a collection
  Future insertDocument() async {
    debugPrint('\n\n💙 💙  inserting a typical document ....');
    dynamic result;
    try {
      var carrier = Carrier(db: 'testdb', collection: 'testCollection', data: {
        'name': 'Michelle',
        'lastName': 'Obama',
        'wealth': random.nextInt(10000) * 1.04,
        'date': DateTime.now().toUtc().toIso8601String(),
        'desc': '💙  🍎 🍎  serve with purpose  🍎 🍎 💙'
      });
      result = await MongodbMobile.insert(carrier);
      debugPrint(
          '\n\n🧩🧩🧩🧩🧩🧩  _MyAppState: insertDocument 🧩🧩🧩 document added : 🍎 statusCode: $result\n\n\n');
      showSnackbar(
          message: ' 🧩🧩🧩  Document inserted',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      result = 'Failed to get platform version.';
      var d =
          "\ud83d\udc99\ud83d\udc99\ud83d\udc99 serve with purpose \ud83d\udc99\ud83d\udc99\ud83d\udc99";
    }
  }

  /// Remove document from collection
  Future deleteDocument() async {
    showSnackbar(
        message: ' 🧩🧩🧩  Document delete under construction',
        scaffoldKey: _key,
        backgroundColor: Colors.black,
        textColor: Colors.yellow);
  }

  List documents = List();

  /// Get all documents from a collection
  Future getAllDocuments() async {
    debugPrint('\n\n💙 💙  getAllDocuments ....');
    dynamic result;
    try {
      var carrier = Carrier(db: 'testdb', collection: 'testCollection');
      documents = await MongodbMobile.getAll(carrier);
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState: getAllDocuments 🧩🧩🧩  retrieved : 🍎 ${documents.length} documents 🍎 \n\n\n');

      showSnackbar(
          message: ' 🍎 🍎 🍎  ${documents.length} documents found',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      result = 'Failed to get platform version.';
    }
  }

  /// Query Mongo database using collection properties
  Future query() async {
    debugPrint('\n\n💙 💙  getByProperty ....');
    try {
      var carrier = Carrier(db: 'testdb', collection: 'testCollection', query: {
        "gt": {"wealth": 7000},
        "eq": {"lastName": "Obama"},
        "and": true,
        "or": false,
        "limit": 0
      });
      var object = await MongodbMobile.query(carrier);
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState: query: 🧩🧩🧩  retrieved : 🍎 ${object.length} documents 🍎 \n\n\n');
      print(object);

      showSnackbar(
          message: ' 🍎 🍎 🍎  ${object.length} documents found',
          scaffoldKey: _key,
          backgroundColor: Colors.purple,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
    }
  }

  void showSnackbar(
      {@required GlobalKey<ScaffoldState> scaffoldKey,
      @required String message,
      @required Color textColor,
      @required Color backgroundColor}) {
    if (scaffoldKey.currentState == null) {
      print('AppSnackbar.showSnackbar --- currentState is NULL, quit ..');
      return;
    }
    scaffoldKey.currentState.removeCurrentSnackBar();
    scaffoldKey.currentState.showSnackBar(new SnackBar(
      content: _getText(message, textColor),
      duration: new Duration(seconds: 15),
      backgroundColor: backgroundColor,
    ));
  }

  static Widget _getText(
    String message,
    Color textColor,
  ) {
    return Text(
      message,
      overflow: TextOverflow.clip,
      style: new TextStyle(color: textColor),
    );
  }

  GlobalKey<ScaffoldState> _key = GlobalKey();
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        key: _key,
        appBar: AppBar(
          title: const Text('MongoDB Plugin Example App'),
          backgroundColor: Colors.deepOrange.shade300,
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
        backgroundColor: Colors.brown.shade50,
        floatingActionButton: FloatingActionButton(
          onPressed: () {},
          child: Icon(Icons.bug_report),
          backgroundColor: Colors.red.shade700,
          elevation: 24,
        ),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Card(
              elevation: 4,
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: ListView(
                  children: <Widget>[
                    Container(
                      width: 300,
                      child: RaisedButton(
                        onPressed: insertDocument,
                        elevation: 16,
                        color: Colors.pink.shade300,
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
                      height: 40,
                    ),
                    Container(
                      width: 300,
                      child: RaisedButton(
                        onPressed: getAllDocuments,
                        elevation: 16,
                        color: Colors.purple.shade300,
                        child: Padding(
                          padding: const EdgeInsets.all(20.0),
                          child: Text(
                            'Get All Documents',
                            style: TextStyle(color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                    SizedBox(
                      height: 40,
                    ),
                    Container(
                      width: 300,
                      child: RaisedButton(
                        onPressed: query,
                        elevation: 16,
                        color: Colors.teal.shade300,
                        child: Padding(
                          padding: const EdgeInsets.all(20.0),
                          child: Text(
                            'Query By Properties',
                            style: TextStyle(color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                    SizedBox(
                      height: 40,
                    ),
                    Container(
                      width: 300,
                      child: RaisedButton(
                        onPressed: deleteDocument,
                        elevation: 16,
                        color: Colors.blue.shade400,
                        child: Padding(
                          padding: const EdgeInsets.all(20.0),
                          child: Text(
                            'Delete Document',
                            style: TextStyle(color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
