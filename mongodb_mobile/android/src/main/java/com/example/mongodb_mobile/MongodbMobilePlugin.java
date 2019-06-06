package com.example.mongodb_mobile;

import android.os.Build;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
// Base Stitch Packages
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;

// Packages needed to interact with MongoDB and Stitch
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

// Necessary component for working with MongoDB Mobile
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;

import java.util.Map;

/** MongodbMobilePlugin */
public class MongodbMobilePlugin implements MethodCallHandler {
  /** Plugin registration. */
  private static StitchAppClient client;
  private static MongoClient mobileClient;
  private static RemoteMongoClient  remoteMongoClient;
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final String TAG = MongodbMobilePlugin.class.getSimpleName();
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "mongodb_mobile");
    channel.setMethodCallHandler(new MongodbMobilePlugin());
  }
  
  
  @Override
  public void onMethodCall(MethodCall call, Result result) {
    
    try {
      switch (call.method) {
        case "getByProperty":
          Map args0 = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:getByProperty:  ..... args: \uD83C\uDF3F ☘ ️" + args0);
          Object object0 = LocalDBUtil.getByProperty(mobileClient, args0);
          result.success(object0);
          break;
        case "insert":
          Map args = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:insert:  ..... args: \uD83C\uDF3F ☘ ️" + args);
          Object object = LocalDBUtil.insert(mobileClient, args);
          result.success(object);
          break;
        case "getAll":
          Map args2 = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:getAll:  ..... args: \uD83C\uDF3F ☘ ️" + args2);
          Object object2 = LocalDBUtil.getAll(mobileClient, args2);
          result.success(object2);
          break;
        case "getPlatformVersion":
          Log.d(TAG, "onMethodCall: getPlatformVersion requested ....");
          result.success("ANDROID " + android.os.Build.VERSION.RELEASE + " " + Build.DEVICE
                                 + " " + Build.MODEL);
          Log.d(TAG, "🍎 onMethodCall: getPlatformVersion: 🧩🧩🧩 MANUFACTURER: " + Build.MANUFACTURER);
          break;
        case "setAppID":
          String key = call.argument("appID");
          String type = call.argument("type");
          if (type == null) {
            Log.e(TAG, "onMethodCall: \uD83D\uDC7F MongoDB client type missing, should be local or atlas");
            result.error("001", "\uD83D\uDC7F MongoDB client type missing, should be local or atlas", "");
            return;
          }
          
          Log.d(TAG, "🍎 onMethodCall: appID received : " + key + ", setting up MongoDB client");
          try {
            client = Stitch.initializeDefaultAppClient(key);
          } catch (IllegalStateException e) {
            Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F registerWith:  MongoDB mobile client already set up with appID, \uD83D\uDC7D \uD83D\uDC7D IGNORE \uD83D\uDC7D \uD83D\uDC7D");
          } catch (Exception e) {
            Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F registerWith: failed to get MongoDB mobile client", e);
            result.error("MongoDB client failed", e.getMessage(), e.getLocalizedMessage());
            return;
          }
          Log.d(TAG, "🍎 🍎 onMethodCall: setAppID 🧩🧩🧩 " + key + "  🧩🧩🧩");
          setClient(type);
          result.success(" \uD83C\uDF38  \uD83C\uDF38  \uD83C\uDF38  We cool with appID: 🍎 " + key + " 🍎 on the wild side   \uD83C\uDF38 ");
          break;
        default:
          Log.e(TAG, "onMethodCall: \uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F something broke, Houston!");
          result.notImplemented();
          break;
      }
    } catch (Exception e) {
      Log.e(TAG, "onMethodCall: ", e);
      result.error("002", "Channel method error of some sort", "");
    }
  }
  private void setClient(String type) {
    switch (type) {
      case "local":
        mobileClient =
                client.getServiceClient(LocalMongoDbService.clientFactory);
        Log.d(TAG, "🍎 onMethodCall:setClient: \uD83C\uDF40 local MongoDB client stood up!");
        break;
      case "atlas":
        remoteMongoClient =
                client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        Log.d(TAG, "🍎 onMethodCall:setClient: \uD83C\uDF40 atlas MongoDB client stood up!");
        break;
      
    }
  }
}
