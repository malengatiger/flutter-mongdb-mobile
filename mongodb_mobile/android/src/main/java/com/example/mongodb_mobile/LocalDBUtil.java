package com.example.mongodb_mobile;

import android.util.Log;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

public class LocalDBUtil {
    private static final String TAG = LocalDBUtil.class.getSimpleName();

    static Object insert(MongoClient client, Map carrier) {
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
    static Object getByProperty(MongoClient client, Map carrier) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ getByProperty: map: " + carrier.toString());
        String db = (String) carrier.get("db");
        String collection = (String) carrier.get("collection");
        String id = (String) carrier.get("id");

        assert collection != null;
        assert db != null;
        assert id != null;

        FindIterable<Document>  mongoIterable = client.getDatabase(db).getCollection(collection)
                .find(and(gt("wealth", 1299), eq("lastName", "Obama")));
        MongoCursor<Document> cursor = mongoIterable.iterator();
        List<Object> list = new ArrayList<>();
        int cnt = 0;
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            list.add(doc.toJson());
            cnt++;
            Log.d(TAG, "🍎 getAll: doc: \uD83D\uDC99  #"+cnt+"  \uD83C\uDF6F  \uD83C\uDF6F  " + doc.toJson());

        }
        Log.d(TAG, "getByProperty: 🍎 🍎 documents found: " + list.size()  +"  🍎 🍎 🍎 🍎 \n");
        return list;
    }

    /**
     * @param client
     * @param carrier
     * @return
     */
    static List<Object>  getAll(MongoClient client, Map carrier) {
        Log.d(TAG, "\n🍎 getAll: get all documents in collection: " + carrier.toString() + "\n\n");
        String db = (String) carrier.get("db");
        String collectionName = (String) carrier.get("collection");

        assert collectionName != null;
        assert db != null;
        MongoCollection<Document> collection = client.getDatabase(db).getCollection(collectionName);
        List<Object> list = new ArrayList<>();
        Log.d(TAG, "\ngetAll: documents found: ☘ ️"  + collection.countDocuments() + " 🍎 🍎\n");
        int cnt = 0;
        try (MongoCursor<Document> cur = collection.find().iterator()) {
            while (cur.hasNext()) {
                Document doc = cur.next();
                list.add(doc.toJson());
                cnt++;
                Log.d(TAG, "🍎 getAll: doc: \uD83D\uDC99  #"+cnt+"  \uD83C\uDF6F  \uD83C\uDF6F  " + doc.toJson());
            }
            Log.d(TAG, "getAll: returning  \uD83D\uDD06 \uD83D\uDD06 \uD83D\uDD06 \uD83D\uDD06 " + list.size() + " documents");
            return list;
        }

    }
    public static void hello() {
        Log.d(TAG, "hello: Aubrey !!!");
    }
}
