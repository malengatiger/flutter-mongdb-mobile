package com.example.mongodb_mobile;

import android.util.Log;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static com.mongodb.client.model.Filters.eq;


public class LocalDBUtil {
    private static final String TAG = LocalDBUtil.class.getSimpleName();

    static String insert( MongoClient client,  Map carrier) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ insert: document: " + carrier.toString());
        Document document = new Document();
        Map dataMap = (Map) carrier.get("data");
        assert dataMap != null;
        document.putAll(dataMap);
        Log.d(TAG, "insert: \uD83D\uDD35 \uD83D\uDD35   document before insert: " + document.toJson()  +"  \uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35  \n");
        getCollection(client, carrier).insertOne(document);
        Object mb = document.get("_id");
        Log.d(TAG, "insert: 🍎 🍎 document inserted; check generated id:  \uD83C\uDFC8  " + mb  +"   \uD83C\uDFC8 🍎 🍎 🍎 🍎 \n");
        assert mb != null;
        return mb.toString();
    }

    static Object getOne( MongoClient client,  Map carrier) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ getOne: carrier: " + carrier.toString());
        String id = (String) carrier.get("id");
        assert id != null;
        Bson filter =  eq("_id", new ObjectId(id));
        FindIterable result = getCollection(client, carrier).find(filter);
        MongoCursor cursor = result.iterator();
        List<Object> list = new ArrayList<>();
        int cnt = 0;
        while (cursor.hasNext()) {
            Document doc = (Document) cursor.next();
            list.add(doc.toJson());
            cnt++;
            Log.d(TAG, "🍎 getOne: doc: \uD83D\uDC99  #"+cnt+"  \uD83C\uDF6F  \uD83C\uDF6F  " + doc.toJson());
        }
        Log.d(TAG, "getOne: 🍎 🍎 documents found: " + list.size()  +"  🍎 🍎 🍎 🍎 \n");
        return list;
    }

    static long replace( MongoClient client,  Map carrier) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ replace: document: " + carrier.toString());
        String id = (String) carrier.get("id");
        Document document = new Document();
        Map dataMap = (Map) carrier.get("data");
        assert dataMap != null;
        document.putAll(dataMap);
        assert id != null;
        Bson filter =  eq("_id", new ObjectId(id));
        UpdateResult result = getCollection(client, carrier).replaceOne(filter, document);
        Log.d(TAG, "replace: \uD83C\uDFC0  MatchedCount: " + result.getMatchedCount() + " \uD83C\uDFC0 ModifiedCount: " + result.getModifiedCount()  + " \uD83D\uDD06 wasAcknowledged: " + result.wasAcknowledged());

        return result.getMatchedCount();
    }

    static long addToArray( MongoClient client,  Map carrier) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ addToArray: document: " + carrier.toString());
        String id = (String) carrier.get("id");
        String arrayName  = (String) carrier.get("arrayName");
        Map data = (Map) carrier.get("data");
        assert data != null;
        assert arrayName != null;
        assert id != null;

        Document document = new Document().append(sdf.format(new Date()), data);

        Bson filter =  eq("_id", new ObjectId(id));
        UpdateResult result = getCollection(client, carrier).updateOne(filter, Updates.addToSet(arrayName, document));
        Log.d(TAG, "addToArray: \uD83C\uDFC0  MatchedCount: " + result.getMatchedCount() + " \uD83C\uDFC0 ModifiedCount: " + result.getModifiedCount()  + " \uD83D\uDD06 wasAcknowledged: " + result.wasAcknowledged());

        return result.getMatchedCount();
    }

    static Object query(MongoClient client, Map carrier) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ query: carrier: " + carrier.toString());

        Bson mFilter = Helper.getQueryFilter(carrier);
        Log.d(TAG, "❤️  ❤️   ❤️ query: mFilter:  ❤️  ❤️ " + mFilter);
        assert mFilter != null;
        FindIterable mongoIterable = getCollection(client, carrier)
                .find(mFilter);
        MongoCursor cursor = mongoIterable.iterator();
        List<Object> list = new ArrayList<>();
        int cnt = 0;
        while (cursor.hasNext()) {
            Document doc = (Document) cursor.next();
            list.add(doc.toJson());
            cnt++;
            Log.d(TAG, "🍎 getAll: doc: \uD83D\uDC99  #"+cnt+"  \uD83C\uDF6F  \uD83C\uDF6F  " + doc.toJson());

        }
        Log.d(TAG, "query: 🍎 🍎 documents found: " + list.size()  +"  🍎 🍎 🍎 🍎 \n");
        return list;
    }

    static long delete( MongoClient client,  Map carrier) {
        Log.d(TAG, "\uD83C\uDF3F  ✂️️ delete:  ✂️ document: " + carrier.toString());
        String id = (String) carrier.get("id");
        assert id != null;
        Bson filter = eq("_id", new ObjectId(id));
        DeleteResult result = getCollection(client, carrier).deleteOne(filter);
        Log.d(TAG, "delete:  ✂️ ✂️ document deleted, deletedCount:  ✂️ " + result.getDeletedCount()  + " wasAcknowledged:  🍎️ "  + result.wasAcknowledged() +"   ✂️ ✂️ \n");
        return result.getDeletedCount();
    }

    private static MongoCollection getCollection( MongoClient client,  Map carrier) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ getCollection: carrier: " + carrier.toString());
        String db = (String) carrier.get("db");
        String collection = (String) carrier.get("collection");
        assert collection != null;
        assert db != null;
        return client.getDatabase(db).getCollection(collection);
    }

    static List<Object>  getAll( MongoClient client,  Map carrier) {
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

    static final Locale locale = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss:ss", locale);
}
