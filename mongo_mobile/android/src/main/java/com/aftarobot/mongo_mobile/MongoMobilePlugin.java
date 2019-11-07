package com.aftarobot.mongo_mobile;

import android.os.Build;
import android.util.Log;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
// Base Stitch Packages

import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;

// Packages needed to interact with MongoDB and Stitch
import com.mongodb.client.MongoClient;

// Necessary component for working with MongoDB Mobile
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;
import com.mongodb.stitch.core.auth.StitchUserProfile;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** MongodbMobilePlugin */
public class MongoMobilePlugin implements MethodCallHandler {
  /** Plugin registration. */
  private static StitchAppClient client;
  private static MongoClient mobileClient;
  private static EventChannel eventChannel;
  private static ChangeEventStreamHandler changeEventStreamHandler;
  private static final String TAG = MongoMobilePlugin.class.getSimpleName(), MONGO_CHANGE_EVENTS = "mongo_change_events";


  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "mongo_mobile");
    channel.setMethodCallHandler(new MongoMobilePlugin());
    Log.d(TAG, "registerWith:  \uD83D\uDEBC \uD83D\uDEBC creating event channel ......");
    eventChannel = new EventChannel(registrar.messenger(), MONGO_CHANGE_EVENTS);
    changeEventStreamHandler = new ChangeEventStreamHandler();
    eventChannel.setStreamHandler(changeEventStreamHandler);
    Log.d(TAG, "registerWith:  \uD83D\uDEBC \uD83D\uDEBC  method and event channels established   \uD83D\uDEBC \uD83D\uDEBC ");

  }


  @Override
  public void onMethodCall(MethodCall call, final Result result) {

    try {
      switch (call.method) {

        case "query":
          Map queryArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:query:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + queryArgs);
          if (mobileClient != null) {
            Object list = LocalDBUtil.query(mobileClient, queryArgs);
            result.success(list);
          } else {
            RemoteDBUtil.query(queryArgs, new RemoteDBUtil.RemoteQueryListener() {
              @Override
              public void onQuery(Object object) {
                result.success(object);
              }

              @Override
              public void onError(String message) {
                result.error("005", message, "");
              }
            });
          }
          break;
        case "insert":
          Map insArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:insert:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + insArgs);
          if (mobileClient != null) {
            String objectId = LocalDBUtil.insert(mobileClient, insArgs);
            result.success(objectId);
          } else {
            RemoteDBUtil.insert(insArgs, new RemoteDBUtil.RemoteInsertListener() {
              @Override
              public void onInsert(String id) {
                result.success(id);
              }
              @Override
              public void onError(String message) {
                result.error("004", message, "");
              }
            });
          }
          break;
        case "addToArray":
          Map arrArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:insert:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + arrArgs);
          if (mobileClient != null) {
            long arrResult = LocalDBUtil.addToArray(mobileClient, arrArgs);
            result.success(arrResult);
          } else {
            RemoteDBUtil.addToArray(arrArgs, new RemoteDBUtil.RemoteAddToArrayListener() {
              @Override
              public void onAddToArray(Object object) {
                result.success(object);
              }

              @Override
              public void onError(String message) {
                result.error("005", message, "");
              }
            });
          }
          break;
        case "update":
          Map replaceArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:update:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + replaceArgs);
          if (mobileClient != null) {
            long replaceResult = LocalDBUtil.update(mobileClient, replaceArgs);
            result.success(replaceResult);
          } else {
            RemoteDBUtil.update(replaceArgs, new RemoteDBUtil.RemoteReplaceListener() {
              @Override
              public void onReplace(Object object) {
                result.success(object);
              }

              @Override
              public void onError(String message) {
                result.error("006", message, "");
              }
            });
          }
          break;
        case "delete":
          Map deleteArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:delete:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + deleteArgs);
          if (mobileClient != null) {
            Object deleteResult = LocalDBUtil.delete(mobileClient, deleteArgs);
            result.success(deleteResult);
          } else {
            RemoteDBUtil.delete(deleteArgs, new RemoteDBUtil.RemoteDeleteListener() {
              @Override
              public void onDelete(Object object) {
                result.success(object);
              }

              @Override
              public void onError(String message) {
                result.error("007", message, "");
              }
            });
          }
          break;
        case "getAll":
          Map getArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:getAll:  ..... getArgs: \uD83C\uDF3F ☘ ️" + getArgs);
          if (mobileClient != null) {
            Object resultList = LocalDBUtil.getAll(mobileClient, getArgs);
            result.success(resultList);
          } else {
            RemoteDBUtil.getAll(getArgs, new RemoteDBUtil.RemoteGetAllListener() {
              @Override
              public void onGetAll(Object object) {
                result.success(object);
              }

              @Override
              public void onError(String message) {
                result.error("008", message, "");
              }
            });
          }
          break;
        case "getOne":
          Map oneArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:getOne:  ..... oneArgs: \uD83C\uDF3F ☘ ️" + oneArgs);
          if (mobileClient != null) {
            Object document = LocalDBUtil.getOne(mobileClient, oneArgs);
            result.success(document);
          } else {
            RemoteDBUtil.getOne(oneArgs, new RemoteDBUtil.RemoteGetOneListener() {
              @Override
              public void onGetOne(Object object) {
                result.success(object);
              }

              @Override
              public void onError(String message) {
                result.error("009", message, "");
              }
            });
          }
          break;
        case "sync":
          final Map syncArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:sync:  ..... syncArgs: \uD83C\uDF3F ☘ ️" + syncArgs);
          if (mobileClient != null) {
            result.error("014", "\uD83C\uDF36  Sync not appropriate for \uD83C\uDF36 LOCAL \uD83C\uDF36  database", "");
          } else {
            handleSync(result, syncArgs);
          }
          break;
        case "getPlatformVersion":
          Log.d(TAG, "onMethodCall: getPlatformVersion requested ....");
          result.success("ANDROID " + android.os.Build.VERSION.RELEASE + " " + Build.DEVICE
                  + " " + Build.MODEL);
          Log.d(TAG, "🍎 onMethodCall: getPlatformVersion: 🧩🧩🧩 MANUFACTURER: " + Build.MANUFACTURER);
          break;
        case "setAppID":
          String appID = call.argument("appID");
          String type = call.argument("type");
          if (type == null) {
            Log.e(TAG, "onMethodCall: \uD83D\uDC7F MongoDB client type missing, should be local or atlas");
            result.error("001", "\uD83D\uDC7F MongoDB client type missing, should be local or atlas", "");
            return;
          }
          setupDatabase(result, appID, type);
          break;
        default:
          Log.e(TAG, "onMethodCall: \uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F  method "+ call.method  + " is not implemented, Houston, help!");
          result.notImplemented();
          break;
      }
    } catch (Exception e) {
      Log.e(TAG, "onMethodCall: ", e);
      result.error("002", e.getMessage(), "onMethodCall: error of some sort. Houston kinda error :(");
    }
  }

  private void handleSync(final Result result, final Map syncArgs) {

    RemoteDBUtil.syncCollection(syncArgs, new RemoteDBUtil.SyncListener() {

      @Override
      public void onChangeEvent(Object changeEvent, Object document) {

        if (changeEventStreamHandler.getEventSink() == null) {
          Log.e(TAG, "\uD83E\uDDA0 \uD83E\uDDA0 onChangeEvent: \uD83D\uDC7F ERROR - \uD83D\uDC7F \uD83D\uDC7F  eventSink inside handler is NULL!" );
        }  else {
          Log.d(TAG, "\uD83E\uDDA0 \uD83E\uDDA0 onChangeEvent:  \uD83D\uDEBC \uD83D\uDEBC sending changeEvent to changeEventStreamHandler ... ..... ");
          Map<String, Object> map = new HashMap<>();
          Document m = (Document)document;
          map.put("document", m.toJson());
          map.put("changeEvent", changeEvent);
          changeEventStreamHandler.getEventSink().success(map);
          Log.d(TAG, "\uD83E\uDDA0 \uD83E\uDDA0 onChangeEvent: \uD83C\uDF3A \uD83C\uDF3A  event sink has accepted a change event");
        }

      }

      @Override
      public void onSyncCreated() {
        Log.d(TAG, "onSyncCreated: \uD83D\uDCA7 \uD83D\uDCA7 \uD83D\uDCA7 \uD83D\uDCA7 \uD83D\uDCA7");
        result.success(syncArgs.get("collection") + " has \uD83E\uDDA0 \uD83E\uDDA0  MongoDB Atlas sync set up!  \uD83D\uDEBC \uD83D\uDEBC ");
      }

      @Override
      public void onError(String message) {
        result.error("015", message, "");
      }
    });
  }

  private void setupDatabase(Result result, String appID, String type) {
    Log.d(TAG, "🍎 setupDatabase: appID : " + appID + ", setting up database with type: ".concat(type));
    try {
      if (type.equalsIgnoreCase(LOCAL_DATABASE)) {
        try {
          Log.d(TAG, "setupDatabase: 🍎 🍎  LOCAL LOCAL LOCAL   \uD83E\uDDE1 \uD83D\uDC9B");
          setupLocalDatabase(result, appID);
        } catch (IllegalStateException e) {
          Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F onMethodCall:  MongoDB mobile client already set up with appID, \uD83D\uDC7D \uD83D\uDC7D IGNORE \uD83D\uDC7D \uD83D\uDC7D");
          result.success("MongoDB LOCAL mobile client already set up with appID: ".concat(appID));
        }

      } else {
        Log.d(TAG, "setupDatabase: 🍎 🍎  ATLAS ATLAS ATLAS   \uD83E\uDDE1 \uD83D\uDC9B");
        setupAtlasDatabase(result, appID);
      }

    } catch (Exception e) {
      Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F onMethodCall: failed to get MongoDB mobile client", e);
      result.error("MongoDB client setup failed", e.getMessage(), e.getLocalizedMessage());
    }
  }

  private void setupAtlasDatabase(final Result result, String appID) {

    Log.d(TAG, "\nsetupAtlasDatabase: ....... starting anon  auth ...  \uD83C\uDF38  \uD83C\uDF38  \uD83C\uDF38 ");
    try {
      mobileClient = null;
      RemoteDBUtil.anonymousAuth( appID, new MongoAuthListener() {
        @Override
        public void onAuth(StitchUserProfile userProfile) {
          Log.d(TAG, "onAuth: userProfile received : ".concat(Objects.requireNonNull(userProfile.toString())));
          result.success("MongoDB Atlas auth succeeded. \uD83E\uDDE1 \uD83D\uDC9B  Yebo!  \uD83E\uDDE1 \uD83D\uDC9B");
        }

        @Override
        public void onError(String message) {
          Log.e(TAG, "onError: ".concat(message) );
          result.error("003","MongoDB Atlas integration failed", message);
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      result.error("012", e.getMessage(), "");
    }
  }

  private void setupLocalDatabase(Result result, String key) {
    client = Stitch.initializeDefaultAppClient(key);
    Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F initializeAppClient: LOCAL MongoDB mobile client set up!  \uD83D\uDC7D \uD83D\uDC7D IGNORE \uD83D\uDC7D \uD83D\uDC7D");
    Log.d(TAG, "🍎 🍎 onMethodCall: initializeAppClient 🧩🧩🧩 " + key + "  🧩🧩🧩");
    mobileClient =
            client.getServiceClient(LocalMongoDbService.clientFactory);
    Log.d(TAG, "🍎 onMethodCall:setClient: \uD83C\uDF40 LOCAL MongoDB client stood up!");
    result.success(" \uD83C\uDF38  \uD83C\uDF38  \uD83C\uDF38  onMethodCall: We cool with LOCAL appID: 🍎 " + key + " 🍎 on the wild side   \uD83C\uDF38 ");
  }

  private static final String LOCAL_DATABASE = "local", ATLAS_DATABASE = "atlas";
}


