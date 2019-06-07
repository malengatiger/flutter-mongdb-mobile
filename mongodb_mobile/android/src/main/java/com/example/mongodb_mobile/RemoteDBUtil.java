package com.example.mongodb_mobile;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.client.model.Updates;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCursor;
import com.mongodb.stitch.android.services.mongodb.remote.SyncFindIterable;
import com.mongodb.stitch.core.auth.StitchUserProfile;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;
import com.mongodb.stitch.core.services.mongodb.remote.ExceptionListener;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.mongodb_mobile.LocalDBUtil.sdf;
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
            public void onComplete(@NonNull Task<StitchUser> task) {
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

    static void setListeners(Map carrier, final MongoEventListener mongoEventListener) throws Exception {
        Log.d(TAG, "setListeners: \uD83E\uDDE9\uD83E\uDDE9\uD83E\uDDE9  setting up listeners for mongo collections:  🍎 ");
        List<RemoteMongoCollection> collections = new ArrayList<>();
        List<String> names = (List<String>) carrier.get("collections");
        String db = (String) carrier.get("db");
        assert db != null;
        assert names != null;

        for (String name : names) {
            String colName = (String) carrier.get(name);
            assert colName != null;
            RemoteMongoCollection collection = remoteMongoClient.getDatabase(db).getCollection(colName);
            collections.add(collection);
        }

        for (final RemoteMongoCollection collection : collections) {
            Task configure = collection.sync().configure(DefaultSyncConflictResolvers.remoteWins(), new ChangeEventListener() {
                @Override
                public void onEvent(BsonValue documentId, ChangeEvent event) {
                    Log.d(TAG, "\uD83C\uDF3F ☘  onEvent:: " + collection.getNamespace().getCollectionName()
                            + "  event: getOperationType: " + event.getOperationType().name().concat(" - ").concat(documentId.toString()));
                    mongoEventListener.onChangeEvent(event);
                }
            }, new ExceptionListener() {
                @Override
                public void onError(BsonValue documentId, Exception error) {
                    Log.e(TAG, "onError: documentId: ".concat(collection.getNamespace()
                            .getCollectionName().concat(" ")).concat(documentId.toString()), error);
                    mongoEventListener.onError(error.getMessage());
                }
            });
            if (configure.isSuccessful()) {
                Log.d(TAG, " \uD83E\uDDE1 \uD83D\uDC9B \uD83D\uDC9A \uD83D\uDC99 \uD83D\uDC9C  setListeners: 🍎 🍎 configure successful");
            } else {
                Log.e(TAG, "setListeners: configure failed");
                mongoEventListener.onError("MongoDB listeners configuration failed");
            }
        }
    }

    static void query(Map carrier, final RemoteQueryListener remoteQueryListener) {
        Bson mFilter = Helper.getQueryFilter(carrier);
        assert mFilter != null;

        RemoteMongoCollection collection = getRemoteCollection(carrier);
        Task mongoIterable = collection.find(mFilter).iterator();
        mongoIterable.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                remoteQueryListener.onQuery(o);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                remoteQueryListener.onError(e.getMessage());
            }
        });

    }

    static void replace(Map carrier, final RemoteReplaceListener remoteReplaceListener) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ replace: document: " + carrier.toString());
        String id = (String) carrier.get("id");
        Document document = new Document();
        Map dataMap = (Map) carrier.get("data");
        assert dataMap != null;
        document.putAll(dataMap);
        assert id != null;
        Bson filter = eq("_id", new ObjectId(id));
        Task task = getRemoteCollection(carrier).sync().updateOne(filter, document);
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "onSuccess: replace succeeded");
                remoteReplaceListener.onReplace((long) o);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                Log.e(TAG, "onFailure: replace failed: ", e);
                remoteReplaceListener.onError(e.getMessage());
            }
        });

    }

    static void addToArray(Map carrier, final RemoteAddToArrayListener remoteInsertListener) {
        Log.d(TAG, "\uD83C\uDF3F ☘️ addToArray: document: " + carrier.toString());
        String id = (String) carrier.get("id");
        String arrayName = (String) carrier.get("arrayName");
        Map data = (Map) carrier.get("data");
        assert data != null;
        assert arrayName != null;
        assert id != null;

        Document document = new Document().append(sdf.format(new Date()), data);
        Bson filter = eq("_id", new ObjectId(id));

        Task task = getRemoteCollection(carrier).updateOne(filter, Updates.addToSet(arrayName, document));
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "addToArray: success! \uD83C\uDFC0  task: " + o + " \uD83C\uDFC0 ");
                remoteInsertListener.onAddToArray(o);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
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
                    String id = task.getResult().getInsertedId().toString();
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
        Task<RemoteMongoCursor> task = iterable.iterator();
        task.addOnSuccessListener(new OnSuccessListener<RemoteMongoCursor>() {
            @Override
            public void onSuccess(RemoteMongoCursor remoteMongoCursor) {
                List<Object> list = new ArrayList<>();
                while (remoteMongoCursor.hasNext().isSuccessful()) {
                    Task m = remoteMongoCursor.next();
                    list.add(m.getResult());
                }
                Log.d(TAG, "getOne: 🍎 🍎 documents found: " + list.size() + "  🍎 🍎 🍎 🍎 \n");
                remoteGetOneListener.onGetOne(list);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                remoteGetOneListener.onError(e.getMessage());
            }
        });

    }

    static void getAll(Map carrier, final RemoteGetAllListener remoteGetAllListener) {
        Log.d(TAG, "\n🍎 getAll: get all documents in collection: " + carrier.toString() + "\n\n");
        String db = (String) carrier.get("db");
        String collectionName = (String) carrier.get("collection");

        assert collectionName != null;
        assert db != null;

        RemoteMongoCollection collection = getRemoteCollection(carrier);
        Task<RemoteMongoCursor> task = collection.sync().find().iterator();
        task.addOnSuccessListener(new OnSuccessListener<RemoteMongoCursor>() {
            @Override
            public void onSuccess(RemoteMongoCursor remoteMongoCursor) {
                List<Object> list = new ArrayList<>();
                Task task1 = remoteMongoCursor.hasNext();
                if (task1.isSuccessful()) {
                    while (remoteMongoCursor.hasNext().isSuccessful()) {
                        list.add(remoteMongoCursor.next());
                    }
                    Log.d(TAG, "getAll: 🍎 🍎 documents found: " + list.size() + "  🍎 🍎 🍎 🍎 \n");
                    remoteGetAllListener.onGetAll(list);
                } else {
                    remoteGetAllListener.onError(task1.getResult().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                remoteGetAllListener.onError(e.getMessage());
            }
        });


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
                Log.d(TAG, "onSuccess: delete happened OK");
                remoteDeleteListener.onDelete(o);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
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
        Log.d(TAG, "getRemoteCollection: collection:  \uD83C\uDFC8 ".concat(collection.getNamespace().getFullName()).concat("  \uD83C\uDFC8 "));
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
    public interface SetAppListener {
        void onSetup();

        void onError(String message);
    }

}
