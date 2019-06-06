package com.example.mongodb_mobile;

import android.util.Log;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Filters.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.or;

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
    static Object query(MongoClient client, Map dbMap) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ query: dbMap: " + dbMap.toString());
        String db = (String) dbMap.get("db");
        String collection = (String) dbMap.get("collection");
        assert collection != null;
        assert db != null;
        Map query = (Map) dbMap.get("query");
        assert query != null;
        List<Bson> filters = new ArrayList<>();
        boolean isAnd = false;
        boolean isOr = false;
        Set<String> mset = query.keySet();
        for (String key : mset) {
             switch (key) {
                 case "and":
                     isAnd = (boolean) query.get(key);
                     break;
                 case "or":
                     isOr = (boolean) query.get(key);
                     break;
                 case "eq":
                     Map m1 = (Map) query.get(key);
                     assert m1 != null;
                     Set<String> kSet = m1.keySet();
                     Bson filter = null;
                     for (String mKey: kSet) {
                         filter = eq(mKey, m1.get(mKey));
                     }
                     filters.add(filter);
                     break;
                 case "gt":
                     Map m2 = (Map) query.get(key);
                     assert m2 != null;
                     Set<String> kSet2 = m2.keySet();
                     Bson filter2 = null;
                     for (String mKey: kSet2) {
                         filter2 = gt(mKey, m2.get(mKey));
                     }
                     filters.add(filter2);
                     break;
                 case "lt":
                     Map m3 = (Map) query.get(key);
                     assert m3 != null;
                     Set<String> kSet3 = m3.keySet();
                     Bson filter3 = null;
                     for (String mKey: kSet3) {
                         filter3 = lt(mKey, m3.get(mKey));
                     }
                     filters.add(filter3);
                     break;
             }

        }
        Bson mFilter = null;
        if (isAnd) {
            mFilter = and(filters);
        }
        if (isOr) {
            mFilter = or(filters);
        }
        Log.d(TAG, "❤️  ❤️   ❤️ query: mFilter:  ❤️  ❤️ " + mFilter);

//        Log.d(TAG, "\n\ngetByProperty:  ❤️  ❤️   ❤️ what is in here??  ❤️  ❤️ " + gt("wealth", 1299));;
//        FindIterable<Document>  mongoIterable = client.getDatabase(db).getCollection(collection)
//                .find(and(gt("wealth", 1299), eq("lastName", "Obama")));

        FindIterable<Document>  mongoIterable = client.getDatabase(db).getCollection(collection)
                .find(mFilter);
        MongoCursor<Document> cursor = mongoIterable.iterator();
        List<Object> list = new ArrayList<>();
        int cnt = 0;
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            list.add(doc.toJson());
            cnt++;
            Log.d(TAG, "🍎 getAll: doc: \uD83D\uDC99  #"+cnt+"  \uD83C\uDF6F  \uD83C\uDF6F  " + doc.toJson());

        }
        Log.d(TAG, "query: 🍎 🍎 documents found: " + list.size()  +"  🍎 🍎 🍎 🍎 \n");
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
