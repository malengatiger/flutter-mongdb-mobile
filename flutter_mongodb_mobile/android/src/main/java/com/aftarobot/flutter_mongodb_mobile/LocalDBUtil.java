package com.aftarobot.flutter_mongodb_mobile;

import android.util.Log;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalDBUtil {
    private static final String TAG = LocalDBUtil.class.getSimpleName();

    public static Object insert(MongoClient client, Map carrier) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ insert: document: " + carrier.toString());
        String db = (String) carrier.get("db");
        String collection = (String) carrier.get("collection");

        Document document = new Document();
        Map dataMap = (Map) carrier.get("data");
        document.putAll(dataMap);
        assert collection != null;
        assert db != null;
        assert dataMap != null;
        client.getDatabase(db).getCollection(collection).insertOne(document);
        Log.d(TAG, "insert: 🍎 🍎 document inserted: " + document.toJson()  +"  🍎 🍎 🍎 🍎 \n");
        return 0;
    }
    public static List<Object>  getAll(MongoClient client, Map carrier) {
        Log.d(TAG, "\n🍎 getAll: get all documents in collection: " + carrier.toString() + "\n\n");
        String db = (String) carrier.get("db");
        String collectionName = (String) carrier.get("collection");

        assert collectionName != null;
        assert db != null;
        MongoCollection<Document> collection = client.getDatabase(db).getCollection(collectionName);
        List<Object> list = new ArrayList<>();
        Log.d(TAG, "\ngetAll: documents found: ☘ ️"  + collection.countDocuments() + " 🍎 🍎\n");
        try (MongoCursor<Document> cur = collection.find().iterator()) {
            while (cur.hasNext()) {
                Document doc = cur.next();
                list.add(doc.toJson());
                Log.d(TAG, "🍎 getAll: one doc:  \uD83C\uDF6F  \uD83C\uDF6F  " + doc.toJson());
            }
            Log.d(TAG, "getAll: returning  \uD83D\uDD06 \uD83D\uDD06 \uD83D\uDD06 \uD83D\uDD06 " + list.size() + " documents");
            return list;
        }

     }
    public static void hello() {
        Log.d(TAG, "hello: Aubrey !!!");
    }
}
