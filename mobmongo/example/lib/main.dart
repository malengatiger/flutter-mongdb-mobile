import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:mobmongo/carrier.dart';
import 'package:mobmongo/mobmongo.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  debugPrint(
      '🍎 🍎 🍎  Flutter MongoDB Mobile Platform Example App starting ... : ${DateTime.now().toIso8601String()} 🧩🧩🧩');
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'MongoPluginExample',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.pink,
      ),
      home: MongoExamplePage(),
    );
  }
}

class MongoExamplePage extends StatefulWidget {
  @override
  _MongoExamplePageState createState() => _MongoExamplePageState();
}

class _MongoExamplePageState extends State<MongoExamplePage>
    implements ConfigListener {
  static const MONGO_CONN =
      "mongodb+srv://aubs:aubrey3@ar001-1xhdt.mongodb.net/ardb?retryWrites=true&w=majority";
  @override
  void initState() {
    super.initState();
//    setMongoAtlasAppID();
//    listenToMongoChangeEvents();
    _getState();
  }

  @override
  onConfigSaved() {
    isConfig = false;
    _getState();
  }

  _getState() async {
    appID = await getAppID();
    collectionName = await getCollectionName();
    databaseName = await getDatabaseName();
    databaseType = await getDatabaseType();

    if (appID == null || collectionName == null || databaseName == null) {
      setState(() {
        isConfig = true;
      });
      return;
    }
    _logWidgets = List();
    _logWidgets.add(LogDisplay(
        type: 1, message: 'Call Logging Started', date: DateTime.now()));
    setState(() {
      if (databaseType.contains('Mobile')) {
        setLocalMongoAppID();
        isRemote = false;
      } else {
        setMongoAtlasAppID();
        isRemote = true;
      }
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> setLocalMongoAppID() async {
    String platformVersion;
    try {
      platformVersion = await MobMongo.platformVersion;
      debugPrint('_MyAppState: 🧩🧩🧩 Platform Version : 🍎  $platformVersion');
      var res = await MobMongo.setAppID({
        'appID': 'exampleApp',
        'type': MobMongo.LOCAL_DATABASE,
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
      var res = await MobMongo.setAppID({
        'appID': 'routebuilder-scewg',
        'type': MobMongo.ATLAS_DATABASE,
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

  /// Add document to a collection
  Future insertDocument() async {
    debugPrint('\n\n💙 💙  inserting a  document ....');
    dynamic result;
    try {
      var clientID = DateTime.now().toIso8601String();
      var fIndex = random.nextInt(fNames.length - 1);
      var lIndex = random.nextInt(lNames.length - 1);
      var carrier =
          Carrier(db: databaseName, collection: collectionName, data: {
        'name': fNames.elementAt(fIndex),
        'lastName': lNames.elementAt(lIndex),
        'clientID': clientID,
        'wealth': random.nextInt(100000) * 1.04,
        'date': DateTime.now().toUtc().toIso8601String(),
        'desc': '🍎  serve with purpose  💙'
      });
      _logWidgets.add(LogDisplay(
          type: 1, message: 'inserting Document', date: DateTime.now()));
      result = await MobMongo.insert(carrier);
      clientIDs.add(clientID);
      debugPrint(
          '\n\n🧩🧩🧩🧩🧩🧩  _MyAppState: insertDocument 🧩🧩🧩 document added : 🍎 id: $result\n\n\n');

      _logWidgets.add(LogDisplay(
          type: 2, message: 'Document inserted', date: DateTime.now()));
      setState(() {});
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
      _logWidgets.add(
          LogDisplay(type: 1, message: 'Sync Atlas DB', date: DateTime.now()));
      var carrier = Carrier(db: databaseName, collection: collectionName);
      result = await MobMongo.sync(carrier);
      _logWidgets.add(LogDisplay(
          type: 2, message: 'Atlas DB sync  started', date: DateTime.now()));
      setState(() {});
      debugPrint(
          '\n\n🧩🧩🧩🧩🧩🧩  _MyAppState: syncCollection: 🧩🧩🧩  🍎 result: $result\n\n\n');
      showSnackbar(
          message: ' 🧩🧩🧩 Mobile - Atlas Sync started',
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
    if (clientIDs.isEmpty) {
      showSnackbar(
          scaffoldKey: _key,
          message: 'Please insert document first',
          textColor: Colors.yellow,
          backgroundColor: Colors.red);
      return;
    }
    dynamic result;
    try {
      var id = clientIDs.elementAt(0);
      var carrier = Carrier(
          db: databaseName,
          collection: collectionName,
          id: {
            'field': 'clientID',
            'value': id,
          },
          arrayName: "musicTracks",
          data: {
            'artist': 'Michael Jackson',
            'track': 'Dirty Diana',
            'date': new DateTime.now().toIso8601String(),
          });
      _logWidgets.add(LogDisplay(
          type: 1,
          message: 'Add element to nested array in Document',
          date: DateTime.now()));
      result = await MobMongo.addToArray(carrier);
      _logWidgets.add(LogDisplay(
          type: 2, message: 'Array element added', date: DateTime.now()));
      setState(() {});
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

  List<String> clientIDs = List();
  List documents = List();

  /// Get all documents from a collection
  Future getAllDocuments() async {
    debugPrint('\n\n💙 💙  getAllDocuments ....');
    try {
      _logWidgets.add(LogDisplay(
          type: 1, message: 'find all Documents', date: DateTime.now()));
      var carrier = Carrier(db: databaseName, collection: collectionName);
      documents = await MobMongo.getAll(carrier);
      _logWidgets.add(LogDisplay(
          type: 2,
          message: 'Documents  found: ${documents.length}',
          date: DateTime.now()));
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState: getAllDocuments 🧩🧩🧩  retrieved : 🍎 ${documents.length} documents 🍎 \n\n\n');

      var cnt = 0;
      documents.forEach((m) {
        cnt++;
        debugPrint(' 🧩🧩🧩 #$cnt  👌 $m');
//        debugPrint(' 🧩🧩🧩 #$cnt  👌 ${m['name']} ${m['lastName']}');
      });
      showSnackbar(
          message: '🍎 🍎 🍎  ${documents.length} documents found',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      showSnackbar(
          message: '🍎 🍎 🍎  ${documents.length} documents failed',
          scaffoldKey: _key,
          backgroundColor: Colors.red[700],
          textColor: Colors.yellow);
    }
  }

  /// Delete document from a collection
  Future delete() async {
    debugPrint('\n\n💙 💙  delete ....');
    if (clientIDs.isEmpty) {
      showSnackbar(
          scaffoldKey: _key,
          message: 'Please insert document first',
          textColor: Colors.yellow,
          backgroundColor: Colors.red);
      return;
    }
    try {
      var carrier = Carrier(
        db: databaseName,
        collection: collectionName,
        id: {
          'field': 'clientID',
          'value': clientIDs.elementAt(0),
        },
      );
      _logWidgets.add(LogDisplay(
          type: 1, message: 'delete Document', date: DateTime.now()));
      var res = await MobMongo.delete(carrier);
      _logWidgets.add(LogDisplay(
          type: 2, message: 'Document deleted', date: DateTime.now()));
      clientIDs.removeAt(0);
      debugPrint(
          '\n\n🍎 🍎 🍎 _MyAppState:delete: 🧩🧩🧩  deleted : 🍎  : $res 🍎 \n\n\n');
      setState(() {});
      showSnackbar(
          message: '🍎 🍎 🍎  document deleted',
          scaffoldKey: _key,
          backgroundColor: Colors.black,
          textColor: Colors.white);
    } on PlatformException catch (f) {
      print('👿👿👿👿👿👿👿👿 PlatformException 🍎 🍎 🍎 - $f');
      showSnackbar(
          message: '🍎 🍎 🍎  document delete failed',
          scaffoldKey: _key,
          backgroundColor: Colors.pink[700],
          textColor: Colors.white);
    }
  }

  /// get one document from a collection
  Future getOne() async {
    debugPrint('\n\n💙 💙  get one doc ....');
    if (clientIDs.isEmpty) {
      showSnackbar(
          scaffoldKey: _key,
          message: 'Please insert document first',
          textColor: Colors.yellow,
          backgroundColor: Colors.red);
      return;
    }
    try {
      _logWidgets.add(LogDisplay(
          type: 2,
          message: 'get 1 Document by property',
          date: DateTime.now()));
      var carrier = Carrier(
        db: databaseName,
        collection: collectionName,
        id: {
          'field': 'clientID',
          'value': clientIDs.elementAt(0),
        },
      );
      var res = await MobMongo.getOne(carrier);
      _logWidgets.add(LogDisplay(
          type: 2, message: 'Document obtained', date: DateTime.now()));
      setState(() {});
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
  Future updateDocument() async {
    debugPrint('\n\n💙 💙  replace  ....');
    if (clientIDs.isEmpty) {
      showSnackbar(
          scaffoldKey: _key,
          message: 'Please insert document first',
          textColor: Colors.yellow,
          backgroundColor: Colors.red[800]);
      return;
    }
    try {
      _logWidgets.add(LogDisplay(
          type: 2, message: 'update Document', date: DateTime.now()));
      print(
          '💙💙💙💙 updating document with clientID:  💙  ${clientIDs.elementAt(0)}');
      var carrier = Carrier(db: databaseName, collection: collectionName, id: {
        'field': 'clientID',
        'value': clientIDs.elementAt(0),
      }, fields: {
//        'name': 'Aubrey 👽 St. Vincent',
//        'lastName': 'Malabie 🦊🦊🦊 III',
        'wealth': 5555522.55,
        'clientID': clientIDs.elementAt(0),
        'date': DateTime.now().toUtc().toIso8601String(),
        'desc': '🐬   🍎 serve with UPDATED purpose  🍎  🐬 '
      });
      var res = await MobMongo.update(carrier);
      _logWidgets.add(LogDisplay(
          type: 2, message: 'Document updated', date: DateTime.now()));
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
      _logWidgets.add(
          LogDisplay(type: 1, message: 'start Query', date: DateTime.now()));
      var carrier =
          Carrier(db: databaseName, collection: collectionName, query: {
        "gt": {"wealth": 5000},
        "eq": {"lastName": lNames.elementAt(random.nextInt(lNames.length - 1))},
        "and": true,
        "or": false,
        "limit": 0
      });
      dynamic object = await MobMongo.query(carrier);
      _logWidgets.add(LogDisplay(
          type: 2,
          message: 'Documents found ${object.length}',
          date: DateTime.now()));
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

  EventChannel channel = EventChannel(MobMongo.MONGO_CHANGE_EVENTS);
  void listenToMongoChangeEvents() {
    // Consuming events on the Dart side.

    channel.receiveBroadcastStream().listen((dynamic event) {
      print(
          '\n\n🌺 🌺 🌺 Received change event from Mongo: 🦠  $event   🦠 🦠 🦠 \n\n');
      _logWidgets.add(LogDisplay(
          type: 2, message: '🧩 🧩 Mongo Change Event', date: DateTime.now()));
      setState(() {});
      var changeEvent = json.decode(event['changeEvent']);
      var document = json.decode(event['document']);
      print(changeEvent);
      print(document);
    }, onError: (dynamic error) {
      print('Received error: ${error.message}');
    });
    print(
        '\n\n🦠 🦠 🦠 Listening to Channel events for 🦠  Mongo Change Events\n');
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
  bool isRemote = false;
  BuildContext mContext;

  String appID, collectionName, databaseName, databaseType;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _key,
      appBar: AppBar(
        title: Center(
          child: Padding(
            padding: EdgeInsets.all(12),
            child: Text(
              'MongoDB Plugin',
              style: TextStyle(
                  fontWeight: FontWeight.w900,
                  fontSize: 20,
                  color: Colors.white),
            ),
          ),
        ),
        actions: <Widget>[
          IconButton(
            icon: Icon(Icons.settings),
            onPressed: () {
              setState(() {
                isConfig = true;
              });
            },
          ),
        ],
        backgroundColor:
            isRemote ? Colors.pink.shade300 : Colors.deepOrange.shade300,
        bottom: PreferredSize(
          preferredSize: Size.fromHeight(140),
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
                      (isRemote != null && isRemote)
                          ? 'MongoDB Atlas'
                          : 'Mobile Database',
                      style: TextStyle(
                          fontSize: 24,
                          color: Colors.white,
                          fontWeight: FontWeight.w900),
                    ),
                  ],
                ),
                SizedBox(
                  height: 20,
                ),
                Row(
                  children: <Widget>[
                    Text(
                      'Database Name:',
                      style: TextStyle(fontWeight: FontWeight.w700),
                    ),
                    SizedBox(
                      width: 8,
                    ),
                    Text(
                      (databaseName == null) ? '' : databaseName,
                      style: TextStyle(
                          fontSize: 18,
                          color: Colors.white,
                          fontWeight: FontWeight.w900),
                    ),
                  ],
                ),
                SizedBox(
                  height: 8,
                ),
                Row(
                  children: <Widget>[
                    Text(
                      'Collection Name:',
                      style: TextStyle(fontWeight: FontWeight.w700),
                    ),
                    SizedBox(
                      width: 8,
                    ),
                    Text(
                      (collectionName == null) ? '' : collectionName,
                      style: TextStyle(
                          fontSize: 18,
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
        onPressed: () {
          if (_logWidgets.isEmpty) {
            _showInfo();
          } else {
            _showLogs();
          }
        },
        child: Icon(Icons.bug_report),
        backgroundColor: (isRemote != null && isRemote)
            ? Colors.pink.shade600
            : Colors.deepOrange.shade600,
        elevation: 24,
      ),
      body: Stack(
        children: <Widget>[
          isConfig
              ? Padding(
                  padding: const EdgeInsets.all(12.0),
                  child: Config(
                    listener: this,
                  ),
                )
              : Center(
                  child: Padding(
                    padding: const EdgeInsets.all(12.0),
                    child: Card(
                      elevation: 8,
                      child: Padding(
                        padding: const EdgeInsets.all(20.0),
                        child: ListView(
                          children: <Widget>[
                            Center(
                              child: Text(
                                '8 Mongo API\'s',
                                style: TextStyle(
                                    fontSize: 16,
                                    fontWeight: FontWeight.w900,
                                    color: Colors.indigo.shade200),
                              ),
                            ),
                            SizedBox(
                              height: 8,
                            ),
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
                              height: 12,
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
                              height: 12,
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
                              height: 12,
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
                              height: 12,
                            ),
                            Container(
                              width: 260,
                              child: RaisedButton(
                                onPressed: updateDocument,
                                elevation: 16,
                                color: Colors.brown.shade400,
                                child: Padding(
                                  padding: const EdgeInsets.all(20.0),
                                  child: Text(
                                    'Update Document',
                                    style: TextStyle(color: Colors.white),
                                  ),
                                ),
                              ),
                            ),
                            SizedBox(
                              height: 12,
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
                              height: 12,
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
                                    'Add To Nested Array',
                                    style: TextStyle(color: Colors.white),
                                  ),
                                ),
                              ),
                            ),
                            SizedBox(
                              height: 12,
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
                              height: 12,
                            ),
                            Text(txt),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
        ],
      ),
    );
  }

  List<LogDisplay> _logWidgets = List();
  bool isConfig = false;

  var txt = '💚 💙 💜 Flutter MongoDB Mobile Plugin ' +
      'This project contains the source code for a plugin that enables Flutter apps to use the MongoDB Mobile embedded database. '
          'The example app included creates a database and a test collection and helps to load data.';

  var txt2 = 'For more, check out the source code in Github!';
  void _showInfo() {
    showDialog(
        context: context,
        builder: (_) => new AlertDialog(
              title: new Text(
                "MongoDB Plugin",
                style: TextStyle(
                    fontWeight: FontWeight.w900,
                    fontSize: 28,
                    color: Theme.of(context).primaryColor),
              ),
              content: Container(
                height: 260.0,
                child: Column(
                  children: <Widget>[
                    Padding(
                      padding: const EdgeInsets.all(20.0),
                      child: Column(
                        children: <Widget>[
                          Text(
                            info,
                            style: TextStyle(
                                fontWeight: FontWeight.normal, fontSize: 16),
                          ),
                          SizedBox(
                            height: 20,
                          ),
                          Text(
                            txt2,
                            style: TextStyle(
                                fontWeight: FontWeight.normal, fontSize: 16),
                          ),
                          SizedBox(
                            height: 12,
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              actions: <Widget>[
                Padding(
                  padding: const EdgeInsets.only(bottom: 20.0),
                  child: FlatButton(
                    onPressed: () {
                      Navigator.pop(context);
                    },
//                    color: Colors.blue.shade700,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        'CLOSE, Thanks!',
                        style: TextStyle(color: Colors.blue.shade600),
                      ),
                    ),
                  ),
                ),
              ],
            ));
  }

  void _showLogs() {
    Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => LogDisplayList(
            logDisplays: _logWidgets,
          ),
        ));
  }

  var info =
      '🍎  This app exercises all the API\'s from the Flutter mongodb_mobile plugin in the Dart Pub library 🍎 🍎 🍎';
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

enum DatabaseType { Atlas, Mobile }

class Config extends StatefulWidget {
  final ConfigListener listener;
  Config({@required this.listener});
  @override
  _ConfigState createState() => _ConfigState();
}

class _ConfigState extends State<Config> {
  DatabaseType _databaseType = DatabaseType.Atlas;
  TextEditingController _appIDController = TextEditingController();
  TextEditingController _dbController = TextEditingController();
  TextEditingController _collectionController = TextEditingController();
  String appID, collectionName, databaseName, databaseType;

  @override
  initState() {
    super.initState();
    _getState();
  }

  _getState() async {
    appID = await getAppID();
    collectionName = await getCollectionName();
    databaseName = await getDatabaseName();
    databaseType = await getDatabaseType();
    if (appID != null) {
      _appIDController.text = appID;
    }
    if (databaseName != null) {
      _dbController.text = databaseName;
    }
    if (collectionName != null) {
      _collectionController.text = collectionName;
    }
    if (databaseType != null && databaseType.contains('Mobile')) {
      _databaseType = DatabaseType.Mobile;
    } else {
      _databaseType = DatabaseType.Atlas;
    }
  }

  _onAppIDChanged(String value) async {
    print(value);
    appID = value;
    await saveAppID(appID);
  }

  _onDBChanged(String value) async {
    print(value);
    databaseName = value;
    await saveDatabaseName(databaseName);
  }

  _onCollectionChanged(String value) async {
    print(value);
    collectionName = value;
    await saveCollectionName(collectionName);
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: ListView(
          children: <Widget>[
            RadioListTile<DatabaseType>(
              title: const Text('Atlas DB'),
              value: DatabaseType.Atlas,
              groupValue: _databaseType,
              onChanged: (DatabaseType value) {
                saveDatabaseType('Atlas');
                setState(() {
                  _databaseType = value;
                });
                print('🍎 🍎 🍎  databaseName selected: $_databaseType');
              },
            ),
            RadioListTile<DatabaseType>(
              title: const Text('Mobile DB'),
              value: DatabaseType.Mobile,
              groupValue: _databaseType,
              onChanged: (DatabaseType value) {
                saveDatabaseType('Mobile');
                setState(() {
                  _databaseType = value;
                });
                print('🍎 🍎 🍎  databaseName selected: $_databaseType');
              },
            ),
            SizedBox(
              height: 24,
            ),
            TextField(
              controller: _appIDController,
              onChanged: _onAppIDChanged,
              keyboardType: TextInputType.text,
              decoration: InputDecoration(hintText: 'Enter AppID'),
            ),
            SizedBox(
              height: 12,
            ),
            TextField(
              controller: _dbController,
              onChanged: _onDBChanged,
              keyboardType: TextInputType.text,
              decoration: InputDecoration(hintText: 'Enter Database Name'),
            ),
            SizedBox(
              height: 12,
            ),
            TextField(
              controller: _collectionController,
              onChanged: _onCollectionChanged,
              keyboardType: TextInputType.text,
              decoration: InputDecoration(hintText: 'Enter Collection Name'),
            ),
            SizedBox(
              height: 24,
            ),
            RaisedButton(
              elevation: 8,
              color: Colors.blue[700],
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Text(
                  'Done Saving Config',
                  style: TextStyle(color: Colors.white),
                ),
              ),
              onPressed: () {
                print('🍎 🍎 🍎 save config pressed');
                widget.listener.onConfigSaved();
              },
            ),
          ],
        ),
      ),
    );
  }
}

abstract class ConfigListener {
  onConfigSaved();
}

Future saveCollectionName(String token) async {
  debugPrint("✏️️ SharedPrefs saving collection ..........");
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
  debugPrint("✏️️ SharedPrefs saving database ..........");
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
  debugPrint("✏️️ SharedPrefs saving appID ..........");
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

Future saveDatabaseType(String token) async {
  debugPrint("✏️️ SharedPrefs saving databaseType ..........");
  SharedPreferences prefs = await SharedPreferences.getInstance();
  prefs.setString("databaseType", token);

  debugPrint("✏️️ databaseType saved in prefs: ❤️ 🧡  $token");
}

Future<String> getDatabaseType() async {
  SharedPreferences prefs = await SharedPreferences.getInstance();
  var token = prefs.getString("databaseType");
  debugPrint("✏️️ SharedPrefs - databaseType from prefs: 💜 $token");
  return token;
}

class LogDisplay extends StatelessWidget {
  final int type;
  final String message;
  final DateTime date;
  LogDisplay(
      {@required this.type, @required this.message, @required this.date});
  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      child: ListTile(
        leading: Text(type == 1 ? '🍎' : '💙'),
        title: Text(message),
        subtitle: Text(date.toIso8601String()),
      ),
    );
  }
}

class LogDisplayList extends StatelessWidget {
  final List<LogDisplay> logDisplays;

  LogDisplayList({@required this.logDisplays});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('MongoDB Logs'),
      ),
      body: ListView.builder(
          itemCount: logDisplays.length,
          itemBuilder: (context, index) {
            return logDisplays.elementAt(index);
          }),
    );
  }
}
