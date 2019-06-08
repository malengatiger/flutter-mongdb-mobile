import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:mongodb_mobile/carrier.dart';
import 'package:mongodb_mobile/mongodb_mobile.dart';

void main() {
  debugPrint(
      '🍎 🍎 🍎  Flutter MongoDB Mobile Platform Example App starting ... : 🧩🧩🧩');
  runApp(MongoExampleApp());
}

class MongoExampleApp extends StatefulWidget {
  @override
  _MongoExampleAppState createState() => _MongoExampleAppState();
}

class _MongoExampleAppState extends State<MongoExampleApp> {
  static const MONGO_CONN =
      "mongodb+srv://aubs:aubrey3@ar001-1xhdt.mongodb.net/ardb?retryWrites=true&w=majority";
  @override
  void initState() {
    super.initState();
    setMongoAtlasAppID();
    listenToMongoChangeEvents();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> setLocalMongoAppID() async {
    String platformVersion;
    try {
      platformVersion = await MongodbMobile.platformVersion;
      debugPrint('_MyAppState: 🧩🧩🧩 Platform Version : 🍎  $platformVersion');
      var res = await MongodbMobile.setAppID({
        'appID': 'exampleApp',
        'type': MongodbMobile.LOCAL_DATABASE,
      });
      print(res);
      showSnackbar(
          scaffoldKey: _key,
          message: '🏀 🏀 🏀  LOCAL DB connection succeeded',
          textColor: Colors.white,
          backgroundColor: Colors.teal.shade900);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      showSnackbar(
          scaffoldKey: _key,
          message: '👽 Local database connection failed 👽 👽 👽 ',
          textColor: Colors.yellow,
          backgroundColor: Colors.pink.shade900);
    }
    if (!mounted) return;

    setState(() {});
  }

  static const API_KEY =
      "HkcniZshpeSsJFgHxFYvBbGppUZyOEDFyrVwzsjqSXzluBy16r90EBTU5esygnuW";
  Future<void> setMongoAtlasAppID() async {
    try {
      debugPrint('\n\n 🍎 🍎 🍎  setting remote MongoDB Stitch App ID ....');
      var res = await MongodbMobile.setAppID({
        'appID': 'routebuilder-scewg',
        'type': MongodbMobile.ATLAS_DATABASE,
        'email': 'hacker1@admin',
        'password': 'aubrey3'
      });
      print(res);
      showSnackbar(
          scaffoldKey: _key,
          message: '❤️ 🧡 💛 💚 💙 💜 Mongo Atlas connected',
          textColor: Colors.white,
          backgroundColor: Colors.teal.shade900);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      showSnackbar(
          scaffoldKey: _key,
          message: '👽 Atlas connection failed 👽 👽 👽 ',
          textColor: Colors.yellow,
          backgroundColor: Colors.pink.shade900);
    }
    if (!mounted) return;

    setState(() {});
  }

  Random random = Random(DateTime.now().millisecondsSinceEpoch);
  static const DB = 'ardb', COLLECTION = "testCollection";

  /// Add document to a collection
  Future insertDocument() async {
    debugPrint('\n\n💙 💙  inserting a  document ....');
    dynamic result;
    try {
      var fIndex = random.nextInt(fNames.length - 1);
      var lIndex = random.nextInt(lNames.length - 1);
      var carrier = Carrier(db: DB, collection: COLLECTION, data: {
        'name': fNames.elementAt(fIndex),
        'lastName': lNames.elementAt(lIndex),
        'wealth': random.nextInt(100000) * 1.04,
        'date': DateTime.now().toUtc().toIso8601String(),
        'desc': '🍎  serve with purpose  💙'
      });
      result = await MongodbMobile.insert(carrier);
      debugPrint(
          '\n\n🧩🧩🧩🧩🧩🧩  _MyAppState: insertDocument 🧩🧩🧩 document added : 🍎 id: $result\n\n\n');

      showSnackbar(
          message: ' 🧩🧩🧩  Document inserted',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      showSnackbar(
          message: ' 😡  😡 Document insert failed',
          scaffoldKey: _key,
          backgroundColor: Colors.red.shade700,
          textColor: Colors.yellow);
    }
  }

  /// Sync collection
  Future syncCollection() async {
    debugPrint('\n\n💙 💙  syncCollection  ....');
    dynamic result;
    try {
      var carrier = Carrier(db: DB, collection: COLLECTION);
      result = await MongodbMobile.sync(carrier);
      debugPrint(
          '\n\n🧩🧩🧩🧩🧩🧩  _MyAppState: syncCollection: 🧩🧩🧩  🍎 result: $result\n\n\n');
      showSnackbar(
          message: ' 🧩🧩🧩  Document inserted',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      showSnackbar(
          message: ' 😡  😡 Sync Collection failed',
          scaffoldKey: _key,
          backgroundColor: Colors.red.shade700,
          textColor: Colors.yellowAccent);
    }
  }

  /// Add document to a collection
  Future addToArray() async {
    debugPrint('\n\n💙 💙 addToArray nested in  document ....');
    dynamic result;
    try {
      var carrier = Carrier(
          db: DB,
          collection: COLLECTION,
          id: "5cfbb82e6bc8314a900082bd",
          arrayName: "musicTracks",
          arrayKey: new DateTime.now().millisecondsSinceEpoch.toString(),
          data: {
            'artist': 'Michael Jackson',
            'track': 'Earth Song',
            'date': new DateTime.now().toIso8601String(),
          });
      result = await MongodbMobile.addToArray(carrier);
      debugPrint(
          '\n\n🧩🧩🧩🧩🧩🧩 _MyAppState: addToArray 🧩🧩🧩 element added to nested array : 🍎 result: $result\n\n\n');
      showSnackbar(
          message: ' 🧩🧩🧩  element added to nested array',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.lightBlue);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      showSnackbar(
          scaffoldKey: _key,
          message: f.message,
          textColor: Colors.yellow,
          backgroundColor: Colors.red);
    }
  }

  List documents = List();

  /// Get all documents from a collection
  Future getAllDocuments() async {
    debugPrint('\n\n💙 💙  getAllDocuments ....');
    try {
      var carrier = Carrier(db: DB, collection: COLLECTION);
      documents = await MongodbMobile.getAll(carrier);
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState: getAllDocuments 🧩🧩🧩  retrieved : 🍎 ${documents.length} documents 🍎 \n\n\n');

      var cnt = 0;
      documents.forEach((m) {
        cnt++;
        debugPrint(' 🧩🧩🧩 #$cnt  👌 $m');
      });
      showSnackbar(
          message: '🍎 🍎 🍎  ${documents.length} documents found',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
    }
  }

  /// Delete document from a collection
  Future delete() async {
    debugPrint('\n\n💙 💙  delete ....');
    try {
      var carrier = Carrier(
          db: DB, collection: COLLECTION, id: '5cfb9f236bc831a4b48e8643');
      var res = await MongodbMobile.delete(carrier);
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState:delete: 🧩🧩🧩  deleted : 🍎  : $res 🍎 \n\n\n');

      showSnackbar(
          message: '🍎 🍎 🍎  document deleted',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
    }
  }

  /// get one document from a collection
  Future getOne() async {
    debugPrint('\n\n💙 💙  get one doc ....');
    try {
      var carrier = Carrier(
          db: DB, collection: COLLECTION, id: '5cfba1dc6bc83128d683431b');
      var res = await MongodbMobile.getOne(carrier);
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState:getOne: 🧩🧩🧩  get one : 🍎 : $res 🍎 \n\n\n');

      showSnackbar(
          message: '🍎 🍎 🍎  document retrieved',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
    }
  }

  /// Replace document from a collection
  Future replace() async {
    debugPrint('\n\n💙 💙  replace  ....');
    try {
      var carrier = Carrier(
          db: DB,
          collection: COLLECTION,
          id: '5cf8a0c16bc831de7f4c9b85',
          data: {
            'name': 'Aubrey St Vincent',
            'lastName': 'Malabie III',
            'wealth': 650000.09,
            'date': DateTime.now().toUtc().toIso8601String(),
            'desc': '💙 serve with UPDATED purpose 💙'
          });
      var res = await MongodbMobile.replace(carrier);
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState:replace: 🧩🧩🧩  replaced : 🍎 1 document : $res 🍎 \n\n\n');

      showSnackbar(
          message: '🍎 🍎 🍎  document replaced',
          scaffoldKey: _key,
          backgroundColor: Colors.indigo.shade800,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
    }
  }

  /// Query Mongo database using collection properties
  Future query() async {
    debugPrint('\n\n💙 💙  getByProperty ....');
    try {
      var carrier = Carrier(db: DB, collection: COLLECTION, query: {
        "gt": {"wealth": 5000},
        "eq": {"lastName": lNames.elementAt(random.nextInt(lNames.length - 1))},
        "and": true,
        "or": false,
        "limit": 0
      });
      dynamic object = await MongodbMobile.query(carrier);
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState: query: 🧩🧩🧩  retrieved : 🍎 ${object.length} documents 🍎 see below: \n\n\n');
      var cnt = 0;
      object.forEach((m) {
        cnt++;
        debugPrint(' 🥬🥬🥬 #$cnt  👌 $m');
      });

      showSnackbar(
          message: ' 🍎 🍎 🍎  ${object.length} documents found',
          scaffoldKey: _key,
          backgroundColor: Colors.purple,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
    }
  }

  void listenToMongoChangeEvents() {
    // Consuming events on the Dart side.
    const channel = EventChannel(MongodbMobile.MONGO_CHANGE_EVENTS);
    channel.receiveBroadcastStream().listen((dynamic event) {
      print(
          '\n\n🌺 🌺 🌺 Received change event from Mongo: 🦠  $event   🦠 🦠 🦠 \n\n');
      var changeEvent = json.decode(event['changeEvent']);
      var document = json.decode(event['document']);
      print(changeEvent);
      print(document);
    }, onError: (dynamic error) {
      print('Received error: ${error.message}');
    });
    print('🦠 🦠 🦠 Listening to Channel events for 🦠  Mongo Change Events');
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
  bool isRemote = true;
  void _onSwitchChanged(bool status) {
    debugPrint('onSwitchChanged:  🚼 🚼 $status');
    setState(() {
      isRemote = status;
    });

    if (isRemote) {
      setMongoAtlasAppID();
    } else {
      setLocalMongoAppID();
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        key: _key,
        appBar: AppBar(
          title: Center(
            child: Padding(
              padding: EdgeInsets.all(12),
              child: Text(
                'MongoDB',
                style: TextStyle(
                    fontWeight: FontWeight.w900,
                    fontSize: 20,
                    color: Colors.white),
              ),
            ),
          ),
          backgroundColor:
              isRemote ? Colors.pink.shade300 : Colors.deepOrange.shade300,
          bottom: PreferredSize(
            preferredSize: Size.fromHeight(80),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                children: <Widget>[
                  SizedBox(
                    height: 8,
                  ),
                  Row(
                    children: <Widget>[
                      Text(
                        'MODE',
                        style: TextStyle(color: Colors.white),
                      ),
                      SizedBox(
                        width: 12,
                      ),
                      Switch(
                        onChanged: _onSwitchChanged,
                        activeColor: Colors.blue.shade800,
                        value: isRemote,
                      ),
                      SizedBox(
                        width: 12,
                      ),
                      Text(
                        isRemote ? 'MongoDB Atlas' : 'Local Database',
                        style: TextStyle(
                            fontSize: 24,
                            color: Colors.white,
                            fontWeight: FontWeight.w900),
                      ),
                    ],
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
          backgroundColor:
              isRemote ? Colors.pink.shade600 : Colors.deepOrange.shade600,
          elevation: 24,
        ),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(12.0),
            child: Card(
              elevation: 8,
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: ListView(
                  children: <Widget>[
                    Container(
                      width: 260,
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
                      height: 20,
                    ),
                    Container(
                      width: 260,
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
                      height: 20,
                    ),
                    Container(
                      width: 260,
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
                      height: 20,
                    ),
                    Container(
                      width: 260,
                      child: RaisedButton(
                        onPressed: delete,
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
                    SizedBox(
                      height: 20,
                    ),
                    Container(
                      width: 260,
                      child: RaisedButton(
                        onPressed: replace,
                        elevation: 16,
                        color: Colors.brown.shade400,
                        child: Padding(
                          padding: const EdgeInsets.all(20.0),
                          child: Text(
                            'Replace Document',
                            style: TextStyle(color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                    SizedBox(
                      height: 20,
                    ),
                    Container(
                      width: 260,
                      child: RaisedButton(
                        onPressed: getOne,
                        elevation: 16,
                        color: Colors.indigo.shade400,
                        child: Padding(
                          padding: const EdgeInsets.all(20.0),
                          child: Text(
                            'Get One Document',
                            style: TextStyle(color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                    SizedBox(
                      height: 20,
                    ),
                    Container(
                      width: 260,
                      child: RaisedButton(
                        onPressed: addToArray,
                        elevation: 16,
                        color: Colors.orange.shade500,
                        child: Padding(
                          padding: const EdgeInsets.all(20.0),
                          child: Text(
                            'Add To Array',
                            style: TextStyle(color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                    SizedBox(
                      height: 20,
                    ),
                    isRemote
                        ? Container(
                            width: 260,
                            child: RaisedButton(
                              onPressed: syncCollection,
                              elevation: 16,
                              color: Colors.lime.shade700,
                              child: Padding(
                                padding: const EdgeInsets.all(20.0),
                                child: Text(
                                  'Sync Collection',
                                  style: TextStyle(color: Colors.white),
                                ),
                              ),
                            ),
                          )
                        : Container(),
                    SizedBox(
                      height: 20,
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

  List<String> fNames = [
    "John",
    "Vusi",
    "Lulu",
    "Kgabi",
    "Peter",
    "Cyril",
    "Nancy",
    "Donald",
    'Rogers',
    'Lesego',
    'Leslie',
    'Fatima',
    "Catherine",
    'Musapa',
    'Benjamin',
    'Rachel',
    'Georgia',
    'California',
    "Obby",
    "Tiger"
  ];
  List<String> lNames = [
    "Marule",
    "Woods",
    "Obama",
    'Trump',
    'van der Merwe',
    'Sithole',
    "Ramaphosa",
    "Malenga",
    "Jackson",
    "Maringa",
    "Johnson",
    "Petersen",
    "Bhengu"
  ];
}
