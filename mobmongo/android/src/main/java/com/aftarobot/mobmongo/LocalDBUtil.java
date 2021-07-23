package com.aftarobot.mobmongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;


class LocalDBUtil {
    private static final String TAG = LocalDBUtil.class.getSimpleName();
    private static final Logger logger = Logger.getLogger(LocalDBUtil.class.getSimpleName());

    static String insert(MongoClient client, Map<String, Object> carrier) {
        Document document = new Document();
        Map<String, Object> dataMap = (Map) carrier.get("data");
        assert dataMap != null;
        document.putAll(dataMap);
        getCollection(client, carrier).insertOne(document);
        Object mb = document.get("_id");
        assert mb != null;
        return mb.toString();
    }

    static List<Object> getOne(MongoClient client, Map<String, Object> carrier) {

        Map<String, Object> idMap = (Map) carrier.get("id");
        assert idMap != null;
        String field = (String) idMap.get("field");
        String value = (String) idMap.get("value");
        Bson filter =  eq(field, value);
        FindIterable<Document> result = getCollection(client, carrier).find(filter);
        MongoCursor<Document> cursor = result.iterator();
        List<Object> list = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            list.add(doc.toJson());
        }
        return list;
    }

    static long update(MongoClient client, Map<String, Object> carrier) {
        Map<String, Object> idMap = (Map) carrier.get("id");
        assert idMap != null;
        String field = (String) idMap.get("field");
        String value = (String) idMap.get("value");
        Map<String, Object> dataMap = (Map) carrier.get("fields");
        assert dataMap != null;
        MongoCollection<Document> collection = getCollection(client, carrier);
        Document m1 = collection.find(new Document(field, value)).first();
        if (m1 != null) {
            Bson updated =  new Document(dataMap);
            Bson operation = new Document("$set", updated);
            UpdateResult result = collection.updateOne(m1,operation);

            return result.getMatchedCount();
        } else  {
            return 0;
        }

    }

    static long addToArray( MongoClient client,  Map<String, Object> carrier) {
        Map<String, Object> idMap = (Map) carrier.get("id");
        assert idMap != null;
        String field = (String) idMap.get("field");
        String value = (String) idMap.get("value");

        String arrayName  = (String) carrier.get("arrayName");
        Map data = (Map) carrier.get("data");
        assert data != null;
        assert arrayName != null;

        Document document = new Document(data);

        Bson filter =  eq(Objects.requireNonNull(field), value);
        UpdateResult result = getCollection(client, carrier).updateOne(filter, Updates.addToSet(arrayName, document));
        return result.getMatchedCount();
    }

    static List<Object> query(MongoClient client, Map<String, Object> carrier) {

        Bson mFilter = Helper.getQueryFilter(carrier);
        assert mFilter != null;
        FindIterable<Document> mongoIterable = getCollection(client, carrier)
                .find(mFilter);
        MongoCursor<Document> cursor = mongoIterable.iterator();
        List<Object> list = new ArrayList<>();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            list.add(doc.toJson());
        }
        return list;
    }

    static long delete( MongoClient client,  Map<String, Object> carrier) {
        Map<String, Object> idMap = (Map) carrier.get("id");
        assert idMap != null;
        String field = (String) idMap.get("field");
        String value = (String) idMap.get("value");

        Bson filter = eq(Objects.requireNonNull(field), value);
        DeleteResult result = getCollection(client, carrier).deleteMany(filter);
        return result.getDeletedCount();
    }
    static long deleteMany( MongoClient client,  Map carrier) {
        Bson filter = new BsonDocument();
        DeleteResult result = getCollection(client, carrier).deleteMany(filter);
        return result.getDeletedCount();
    }


    private static MongoCollection<Document> getCollection(MongoClient client, Map<String, Object> carrier) {
        String db = (String) carrier.get("db");
        String collection = (String) carrier.get("collection");
        assert collection != null;
        assert db != null;
        return client.getDatabase(db).getCollection(collection);
    }

    static List<Object> getAll(MongoClient client, Map<String, Object> carrier) {
        String db = (String) carrier.get("db");
        String collectionName = (String) carrier.get("collection");

        assert collectionName != null;
        assert db != null;
        MongoCollection<Document> collection = client.getDatabase(db).getCollection(collectionName);
        List<Object> list = new ArrayList<>();

        try (MongoCursor<Document> cur = collection.find().iterator()) {
            while (cur.hasNext()) {
                Document doc = cur.next();
                list.add(doc.toJson());
             }
            return list;
        }

    }

    static void createIndex(MongoClient client,  Map<String, Object> carrier) {
        String db = (String) carrier.get("db");
        String collection = (String) carrier.get("collection");
        Map<String, Object> index = (Map) carrier.get("index");
        assert db != null;
        assert collection != null;
        assert index != null;

        Document indexDocument = new Document(index);
        client.getDatabase(db).getCollection(collection).createIndex(indexDocument);
    }
}
