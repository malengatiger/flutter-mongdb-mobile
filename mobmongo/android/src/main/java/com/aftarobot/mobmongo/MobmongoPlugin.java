package com.aftarobot.mobmongo;

import android.os.Build;
import android.util.Log;

import com.mongodb.client.MongoClient;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;
import com.mongodb.stitch.core.auth.StitchUserProfile;

import org.bson.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** MobmongoPlugin */
public class MobmongoPlugin implements MethodCallHandler {
  private static StitchAppClient client;
  private static MongoClient mobileClient;
  private static EventChannel eventChannel;
  private static ChangeEventStreamHandler changeEventStreamHandler;
  private static final String TAG = MobmongoPlugin.class.getSimpleName(), MONGO_CHANGE_EVENTS = "mongo_change_events";
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "mobmongo");
    channel.setMethodCallHandler(new MobmongoPlugin());
  }


  @Override
  public void onMethodCall(MethodCall call, final Result result) {

    try {
      switch (call.method) {
        case "deleteMany":
          Map map = (Map) call.arguments;
          if (mobileClient != null) {
            long resultDelete = LocalDBUtil.deleteMany(mobileClient, map);
            result.success(resultDelete);
          }
          break;
        case "query":
          Map queryArgs = (Map) call.arguments;
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
        case "createIndex":
          Map arguments = (Map) call.arguments;
          if (mobileClient != null) {
            LocalDBUtil.createIndex(mobileClient, arguments);
            result.success("Index created");
          }
          break;

        case "insert":
          Map insArgs = (Map) call.arguments;
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
          if (mobileClient != null) {
            result.error("014", "\uD83C\uDF36  Sync not appropriate for \uD83C\uDF36 LOCAL \uD83C\uDF36  database", "");
          } else {
            handleSync(result, syncArgs);
          }
          break;
        case "getPlatformVersion":
//          Log.d(TAG, "onMethodCall: getPlatformVersion requested ....");
          result.success("ANDROID " + android.os.Build.VERSION.RELEASE + " " + Build.DEVICE
                  + " " + Build.MODEL);
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
          Map<String, Object> map = new HashMap<>();
          Document m = (Document)document;
          map.put("document", m.toJson());
          map.put("changeEvent", changeEvent);
          changeEventStreamHandler.getEventSink().success(map);
        }

      }

      @Override
      public void onSyncCreated() {
        result.success(syncArgs.get("collection") + " has \uD83E\uDDA0 \uD83E\uDDA0  MongoDB Atlas sync set up!  \uD83D\uDEBC \uD83D\uDEBC ");
      }

      @Override
      public void onError(String message) {
        result.error("015", message, "");
      }
    });
  }

  private void setupDatabase(Result result, String appID, String type) {
    try {
      if (type.equalsIgnoreCase(LOCAL_DATABASE)) {
        try {
          setupLocalDatabase(result, appID);
        } catch (IllegalStateException e) {
          Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F onMethodCall:  MongoDB mobile client already set up with appID, \uD83D\uDC7D \uD83D\uDC7D IGNORE \uD83D\uDC7D \uD83D\uDC7D");
          result.success("MongoDB LOCAL mobile client already set up with appID: ".concat(appID));
        }

      } else {
        setupAtlasDatabase(result, appID);
      }

    } catch (Exception e) {
      Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F onMethodCall: failed to get MongoDB mobile client", e);
      result.error("MongoDB client setup failed", e.getMessage(), e.getLocalizedMessage());
    }
  }

  private void setupAtlasDatabase(final Result result, String appID) {

    try {
      mobileClient = null;
      RemoteDBUtil.anonymousAuth( appID, new MongoAuthListener() {
        @Override
        public void onAuth(StitchUserProfile userProfile) {
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
    mobileClient =
            client.getServiceClient(LocalMongoDbService.clientFactory);

    result.success(" \uD83C\uDF38  \uD83C\uDF38  \uD83C\uDF38  onMethodCall: We cool with LOCAL appID: 🍎 " + key + " 🍎 on the wild side   \uD83C\uDF38 ");
  }

  private static final String LOCAL_DATABASE = "local", ATLAS_DATABASE = "atlas";
}
