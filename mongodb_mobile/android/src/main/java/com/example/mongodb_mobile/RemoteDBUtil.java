package com.example.mongodb_mobile;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.model.Updates;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.SyncFindIterable;
import com.mongodb.stitch.core.auth.StitchUserProfile;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;
import com.mongodb.stitch.core.services.mongodb.remote.ExceptionListener;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.SyncUpdateResult;

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

//   🧡 💛 💚 💙 💜
class RemoteDBUtil {
    private static StitchAppClient client;
    private static RemoteMongoClient remoteMongoClient;
    private static StitchUserProfile stitchUserProfile;
    private static final String TAG = RemoteDBUtil.class.getSimpleName(), ATLAS = "mongodb-atlas";
//

    static void anonymousAuth(String appID, final MongoAuthListener mongoAuthListener) throws Exception {
        Log.d(TAG, "anonymousAuth: 🍎 🍎  starting ...!  \uD83E\uDDE1 \uD83D\uDC9B");
        try {
            Log.d(TAG, "anonymousAuth: \uD83D\uDD8D \uD83D\uDD8D \uD83D\uDD8D about to getAppClient ...");
            try {
                client = Stitch.getAppClient(appID);
            } catch (Exception e) {
                Log.d(TAG, "anonymousAuth: attempt to initialize StitchAppClient ....");
                client = Stitch.initializeAppClient(appID);
            }
            Log.d(TAG, "anonymousAuth: \uD83D\uDD8D \uD83D\uDD8D \uD83D\uDD8D do we get here ??????? ...");
            if (client == null) {
                Log.d(TAG, "anonymousAuth: StitchAppClient is NULL; \uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 initializeAppClient ");
                client = Stitch.initializeAppClient(appID);
            } else {
                Log.d(TAG, "anonymousAuth: client already available: ".concat(client.toString()));
            }
            Log.d(TAG, "anonymousAuth: 🍎 🍎  StitchAppClient in hand, Senor!  \uD83E\uDDE1 \uD83D\uDC9B");
            remoteMongoClient =
                    client.getServiceClient(RemoteMongoClient.factory, ATLAS);
            Log.d(TAG, "onComplete: 🍎 🍎  RemoteMongoClient in hand, Senor!  \uD83E\uDDE1 \uD83D\uDC9B");

        } catch (IllegalStateException w) {
            Log.d(TAG, "anonymousAuth: \uD83E\uDDE9\uD83E\uDDE9\uD83E\uDDE9 client already initialized.  \uD83D\uDC4E");
        }
        if (client == null) {
            throw new Exception("\uD83C\uDF36 \uD83C\uDF36 \uD83C\uDF36  Unable to set up StitchAppClient, is NULL!  \uD83D\uDC4E   \uD83D\uDC4E   \uD83D\uDC4E");
        }
        client.getAuth().loginWithCredential(new AnonymousCredential())
                .addOnCompleteListener(new OnCompleteListener<StitchUser>() {
            @Override
            public void onComplete(Task<StitchUser> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Successfully logged in as user  🍎 🍎 : " + task.getResult().getId());
                    stitchUserProfile = task.getResult().getProfile();
                    Log.d(TAG, "onComplete: 🍎 🍎 stitchUserProfile obtained, Boss!  \uD83D\uDC99 \uD83D\uDC9C");
                    Log.d(TAG, "onComplete: ".concat(Objects.requireNonNull(stitchUserProfile.toString())) );
                    mongoAuthListener.onAuth(stitchUserProfile);

                } else {
                    Log.e(TAG, "Error \uD83C\uDF36 \uD83C\uDF36 \uD83C\uDF36 logging in  anonymously:", task.getException());
                    Log.e(TAG, "onComplete: \uD83C\uDF36 \uD83C\uDF36 \uD83C\uDF36  Unable to authorize mongodb-atlas. Task  unsuccessful ...");
                    mongoAuthListener.onError("Auth failed. mongodb-atlas not connecting, \uD83C\uDF36  Houston!");
                }
            }
        });

    }
    static void authorize(String email, String password, final MongoAuthListener mongoAuthListener) {
        // Log-in using an Anonymous authentication provider from Stitch
        Log.d(TAG, "\n\nauthorize: 🍎 🍎 ... connecting to Remote MongoDB Atlas .... \uD83E\uDDE9\uD83E\uDDE9\uD83E\uDDE9 ");

        UserPasswordCredential credential = new UserPasswordCredential(email, password);

        try {
            Stitch.getDefaultAppClient().getAuth().loginWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<StitchUser>() {

                        @Override public void onComplete(final Task<StitchUser> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Successfully logged in as user  🍎 🍎 : " + task.getResult().getId());
                                Log.d(TAG, "onComplete: 🍎 🍎 auth OK, setting up RemoteMongoClient: mongodb-atlas");
                                remoteMongoClient =
                                        client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
                                Log.d(TAG, "onComplete: 🍎 🍎  remoteMongoClient in hand, Senor!  \uD83E\uDDE1 \uD83D\uDC9B");
                                stitchUserProfile = task.getResult().getProfile();
                                Log.d(TAG, "onComplete: 🍎 🍎 stitchUserProfile obtained, Boss!  \uD83D\uDC99 \uD83D\uDC9C");
                                Log.d(TAG, "onComplete: ".concat(Objects.requireNonNull(stitchUserProfile.getName()))
                                        .concat(" ").concat(Objects.requireNonNull(stitchUserProfile.getEmail())));
                                mongoAuthListener.onAuth(stitchUserProfile);
                            } else {
                                Log.e(TAG, "Error \uD83C\uDF36 \uD83C\uDF36 \uD83C\uDF36 logging in with email/password auth:", task.getException());
                                Log.e(TAG, "onComplete: \uD83C\uDF36 \uD83C\uDF36 \uD83C\uDF36  Unable to authorize mongodb-atlas. Task  unsuccessful ...");
                                mongoAuthListener.onError("Auth failed. mongodb-atlas not connecting, \uD83C\uDF36  Houston!");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "authorize: we have run into some issues, Houston!", e);
            mongoAuthListener.onError(" \uD83C\uDF36  \uD83C\uDF36  \uD83C\uDF36 - There is a fuck up somewhere ...");
        }
    }


    static void query(Map carrier, final RemoteQueryListener remoteQueryListener) {
        Bson mFilter = Helper.getQueryFilter(carrier);
        assert mFilter != null;

        RemoteMongoCollection collection = getRemoteCollection(carrier);
        final List<Object> list = new ArrayList<>();
        SyncFindIterable iterable =  collection.sync().find(mFilter);
        cnt = 0;
        iterable.forEach(new Block() {

            @Override
            public void apply(Object o) {
                cnt++;
                Document document = (Document)o;
                Log.d(TAG, "\uD83E\uDDE9 \uD83E\uDDE9 query apply: #" + cnt + " - " + document.toJson());
                list.add(document.toJson());
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete( Task task) {
                Log.d(TAG, "\uD83E\uDDE9 query: 🍎 🍎 🍎 🍎 documents found after query: "
                        + list.size() + ", sending to listener ");
                if (task.isSuccessful()) {
                    remoteQueryListener.onQuery(list);
                } else {
                    remoteQueryListener.onError(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });

    }
/*
{ $set:
      {
        quantity: 500,
        details: { model: "14Q3", make: "xyz" },
        tags: [ "coats", "outerwear", "clothing" ]
      }
   }
 */
    static Map getUpdates(Map carrier) {

        Map<String, Object> props = new HashMap<>();
        Set<String> keys = carrier.keySet();
        for (String key: keys) {
            props.put(key,  carrier.get(key));
        }

        return props;
    }
    static void replace(Map carrier, final RemoteReplaceListener remoteReplaceListener) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ replace: document: " + carrier.toString());
        String id = (String) carrier.get("id");
        assert id != null;
        Bson filter = eq("_id", new ObjectId(id));
        assert filter != null;

        Map<String, Object> props = (Map) carrier.get("fields");
        Map<String, Object> map = new HashMap<>();
        map.put("$set", props);
        Bson document = new BasicDBObject(map);

        Task<SyncUpdateResult> task = getRemoteCollection(carrier).sync()
                .updateOne(filter, document);
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                SyncUpdateResult result = (SyncUpdateResult)o;
                Log.d(TAG, "\uD83D\uDC2C onSuccess: \uD83D\uDC2C \uD83D\uDC2C " +
                        "replace succeeded; modified: \uD83E\uDDA0 \uD83E\uDDA0" + result.getModifiedCount());
                remoteReplaceListener.onReplace(result.getModifiedCount());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.e(TAG, "onFailure: replace failed: ", e);
                remoteReplaceListener.onError(e.getMessage());
            }
        });

    }

    static void addToArray(Map carrier, final RemoteAddToArrayListener remoteInsertListener) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ addToArray:add to nested array element to document: " + carrier.toString());
        String id = (String) carrier.get("id");
        String arrayName = (String) carrier.get("arrayName");
        Map data = (Map) carrier.get("data");
        String key = (String) carrier.get("arrayKey");
        assert data != null;
        assert arrayName != null;
        assert id != null;

        Document document = new Document().append(key, data);
        Bson filter = eq("_id", new ObjectId(id));

        Task<RemoteUpdateResult> task = getRemoteCollection(carrier).updateOne(filter, Updates.addToSet(arrayName, document));
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                RemoteUpdateResult result = (RemoteUpdateResult)o;
                Log.d(TAG, "addToArray: success! \uD83C\uDFC0  task: " + result.getModifiedCount() + " \uD83C\uDFC0 ");
                remoteInsertListener.onAddToArray(result.getModifiedCount());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.e(TAG, "onFailure: addToArray failed: ", e);
                remoteInsertListener.onError(e.getMessage());
            }
        });

    }

    static void insert(Map carrier, final RemoteInsertListener remoteInsertListener) {
        Document doc = new Document();
        Map data = (Map) carrier.get("data");
        assert data != null;
        doc.putAll(data);
        final Task<RemoteInsertOneResult> res = getRemoteCollection(carrier).sync().insertOne(doc);
        res.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
            @Override
            public void onComplete(final Task<RemoteInsertOneResult> task) {
                if (task.isSuccessful()) {
                    String id = task.getResult().getInsertedId().asObjectId().getValue().toString();
                    Log.d(TAG, "onComplete: \uD83C\uDF3F ☘ \uD83C\uDF3F ☘ ".concat("Document inserted, id: ")
                            .concat(id).concat("  \uD83E\uDDE1 \uD83D\uDC9B \uD83D\uDC9A \uD83D\uDC99 \uD83D\uDC9C "));
                    remoteInsertListener.onInsert(id);
                } else {
                    String msg = "Error inserting document: ".concat(task.getResult().toString());
                    Log.e(TAG, msg);
                    remoteInsertListener.onError(msg);
                }
            }
        });
    }

    static void getOne(Map carrier, final RemoteGetOneListener remoteGetOneListener) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ getOne: carrier: " + carrier.toString());
        String id = (String) carrier.get("id");
        assert id != null;
        Bson filter = eq("_id", new ObjectId(id));
        RemoteMongoCollection collection = getRemoteCollection(carrier);
        SyncFindIterable iterable = collection.sync().find(filter);
        final List<Document> list = new ArrayList<>();
        cnt = 0;
        iterable.forEach(new Block() {

            @Override
            public void apply(Object o) {
                cnt++;
                Log.d(TAG, "\uD83E\uDDE9 \uD83E\uDDE9 apply: #" + cnt + " - " + o);
                Document document = (Document) o;
                list.add(document);
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete( Task task) {
                Log.d(TAG, "\uD83E\uDDE9 getOne: 🍎 🍎 🍎 🍎 documents found after iteration: "
                        + list.size() + ", sending to listener ");
                if (task.isSuccessful()) {
                    Document document = list.get(0);
                    remoteGetOneListener.onGetOne(document.toJson());
                } else {
                    remoteGetOneListener.onError(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });

    }

    static int cnt = 0;
    static void getAll(Map carrier, final RemoteGetAllListener remoteGetAllListener) {
        Log.d(TAG, "\n🍎 getAll: get all documents in collection: " + carrier.toString() + "\n\n");

        RemoteMongoCollection collection = getRemoteCollection(carrier);
        final List<Object> list = new ArrayList<>();
        SyncFindIterable iterable =  collection.sync().find();
        cnt = 0;
        iterable.forEach(new Block() {

            @Override
            public void apply(Object o) {
                cnt++;

                Document document = (Document)o;
                Log.d(TAG, "\uD83E\uDDE9 \uD83E\uDDE9 apply: #" + cnt + " - " + document.toJson());
                list.add(document.toJson());
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete( Task task) {
                Log.d(TAG, "\uD83E\uDDE9 getAll: 🍎 🍎 🍎 🍎 documents found after iteration: "
                        + list.size() + ", sending to listener ");
                if (task.isSuccessful()) {
                    remoteGetAllListener.onGetAll(list);
                } else {
                    remoteGetAllListener.onError(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });


    }

    static void syncCollection(final Map carrier, final SyncListener syncListener)  {
        RemoteMongoCollection collection = getRemoteCollection(carrier);

        collection.sync().configure(DefaultSyncConflictResolvers.remoteWins(), new ChangeEventListener() {
            @Override 
            public void onEvent(BsonValue documentId, ChangeEvent event) {
                        Log.d(TAG, "\n\uD83D\uDEBC \uD83D\uDEBC onEvent: \uD83D\uDEBC operationType: "
                                + event.getOperationType().name() + " documentID: "
                                + documentId.toString() + "  \uD83E\uDDE9\uD83E\uDDE9  doc: " + event.getFullDocument());

                        syncListener.onChangeEvent(event.toBsonDocument().toJson(), event.getFullDocument());
                    }
                }, new ExceptionListener() {
                    @Override
                    public void onError(BsonValue documentId, Exception error) {
                        Log.e(TAG, "onError: \uD83D\uDEBC \uD83D\uDEBC \uD83D\uDEBC documentId: " + documentId, error );
                        syncListener.onError(error.getMessage());
                    }
                });
        Log.d(TAG, "syncCollection: \uD83D\uDC8E \uD83D\uDC8E \uD83D\uDC8E sync set up for "
                +  carrier.get("collection"));
        syncListener.onSyncCreated();
    }
    static void delete(Map carrier, final RemoteDeleteListener remoteDeleteListener) {
        Log.d(TAG, "\uD83C\uDF3F  ✂️️ delete:  ✂️ document: " + carrier.toString());
        String id = (String) carrier.get("id");
        assert id != null;
        Bson filter = eq("_id", new ObjectId(id));
        RemoteMongoCollection collection = getRemoteCollection(carrier);
        Task task = collection.deleteOne(filter);
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                RemoteDeleteResult result = (RemoteDeleteResult)o;
                Log.d(TAG, "onSuccess:  \uD83C\uDF41  \uD83C\uDF41  \uD83C\uDF41  \uD83C\uDF41  " +
                        "delete happened OK, deleted: " + result.getDeletedCount());
                remoteDeleteListener.onDelete(result.getDeletedCount());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "onFailure: delete failed", e);
                remoteDeleteListener.onError(e.getMessage());

            }
        });

    }

    private static RemoteMongoCollection getRemoteCollection(Map carrier) {
        String db = (String) carrier.get("db");
        String colName = (String) carrier.get("collection");
        assert db != null;
        assert colName != null;

        RemoteMongoCollection collection = remoteMongoClient.getDatabase(db).getCollection(colName);
        Log.d(TAG, "\n\ngetRemoteCollection: collection:  \uD83C\uDFC8 ".concat(collection.getNamespace()
                .getFullName()).concat("  \uD83C\uDFC8 "));


        return collection;

    }

    public interface RemoteInsertListener {
        void onInsert(String id);

        void onError(String message);
    }

    public interface RemoteQueryListener {
        void onQuery(Object object);

        void onError(String message);
    }

    public interface RemoteAddToArrayListener {
        void onAddToArray(Object object);

        void onError(String message);
    }

    public interface RemoteReplaceListener {
        void onReplace(Object object);

        void onError(String message);
    }

    public interface RemoteDeleteListener {
        void onDelete(Object object);

        void onError(String message);
    }

    public interface RemoteGetOneListener {
        void onGetOne(Object object);

        void onError(String message);
    }

    public interface RemoteGetAllListener {
        void onGetAll(Object object);

        void onError(String message);
    }
    public interface SyncListener {
        void onChangeEvent(Object changeEvent, Object document);
        void onSyncCreated();
        void onError(String message);
    }

}
