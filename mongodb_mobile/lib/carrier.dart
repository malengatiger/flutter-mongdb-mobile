import 'package:flutter/cupertino.dart';

///Object that carries data to Java and iOS
////*
/*
{
    "db": "testdb",
    "collection": "testCollection",
    "query": {
        "$gt": {"wealth": 9000},
        "$eq": {"lastName": "Obama"},
        "and": true,
        "or": false,
        "limit": 0
    }
}
 */
class Carrier {
  String db, collection, id;
  dynamic data, query;

  Carrier(
      {@required this.db,
      @required this.collection,
      this.data,
      this.query,
      this.id});

  Map<String, dynamic> toJson() {
    return {
      'db': db,
      'collection': collection,
      'data': data,
      'query': query,
      'id': id,
    };
  }
}
