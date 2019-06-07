package com.example.mongodb_mobile;

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
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.core.auth.StitchUserProfile;
import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;

import java.util.Map;
import java.util.Objects;

/** MongodbMobilePlugin */
public class MongodbMobilePlugin implements MethodCallHandler {
  /** Plugin registration. */
  private static StitchAppClient client;
  private static MongoClient mobileClient;
  private static RemoteMongoClient  remoteMongoClient;
  private static final String TAG = MongodbMobilePlugin.class.getSimpleName(), CHANNEL_NAME = "aftarobot.com/mongo";


  private static Registrar mRegistrar;
  public static void registerWith(Registrar registrar) {
    mRegistrar = registrar;
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "mongodb_mobile");
    channel.setMethodCallHandler(new MongodbMobilePlugin());

  }
  
  
  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    
    try {
      switch (call.method) {
        case "setListeners":
          final Map listenerArgs = (Map) call.arguments;
          EventChannel eventChannel = new EventChannel(mRegistrar.messenger(), CHANNEL_NAME);
          Log.d(TAG, "onMethodCall: eventChannel obtained: \uD83E\uDDE9\uD83E\uDDE9\uD83E\uDDE9  eventChannel setStreamHandler: ".concat(eventChannel.toString()) );
          eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, final EventChannel.EventSink eventSink) {
              Log.d(TAG, "onListen: \uD83E\uDDE9\uD83E\uDDE9\uD83E\uDDE9  eventChannel listening ...  " + o);
              try {
                RemoteDBUtil.setListeners(listenerArgs, new MongoEventListener() {
                  @Override
                  public void onChangeEvent(ChangeEvent changeEvent) {
                    eventSink.success(changeEvent.getFullDocument());
                  }

                  @Override
                  public void onError(String message) {
                    eventSink.error("011", message, "");
                  }
                });
              } catch (Exception e) {
                Log.e(TAG, "onListen: ", e );
                result.error("012", e.getMessage(), "");
              }
            }

            @Override
            public void onCancel(Object o) {
              Log.e(TAG, "eventChannel onCancel: " + o );
            }
          });

          break;
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
        case "replace":
          Map replaceArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:replace:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + replaceArgs);
          if (mobileClient != null) {
            long replaceResult = LocalDBUtil.replace(mobileClient, replaceArgs);
            result.success(replaceResult);
          } else {
            RemoteDBUtil.replace(replaceArgs, new RemoteDBUtil.RemoteReplaceListener() {
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
          Log.d(TAG, "onMethodCall:getAll:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + getArgs);
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
          Log.d(TAG, "onMethodCall:getOne:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + oneArgs);
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
//        try {
          Log.d(TAG, "setupDatabase: 🍎 🍎  ATLAS ATLAS ATLAS   \uD83E\uDDE1 \uD83D\uDC9B");
          setupAtlasDatabase(result, appID);
//        } catch (IllegalStateException e) {
//          Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F onMethodCall:  MongoDB ATLAS mobile client already set up with appID, \uD83D\uDC7D \uD83D\uDC7D IGNORE \uD83D\uDC7D \uD83D\uDC7D");
//          result.success("MongoDB ATLAS mobile client already set up with appID: ".concat(appID));
//        }
      }

    } catch (Exception e) {
      Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F onMethodCall: failed to get MongoDB mobile client", e);
      result.error("MongoDB client setup failed", e.getMessage(), e.getLocalizedMessage());
    }
  }

  private void setupAtlasDatabase(final Result result, String appID) {

    Log.d(TAG, "\nsetupAtlasDatabase: ....... starting anon  auth ...  \uD83C\uDF38  \uD83C\uDF38  \uD83C\uDF38 ");
    try {
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
