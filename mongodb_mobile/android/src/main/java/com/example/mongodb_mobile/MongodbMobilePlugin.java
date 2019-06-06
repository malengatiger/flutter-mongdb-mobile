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

// Necessary component for working with MongoDB Mobile
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;

import org.jetbrains.annotations.NotNull;

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
  public void onMethodCall(@NotNull  MethodCall call, @NotNull Result result) {
    
    try {
      switch (call.method) {
        case "query":
          Map queryArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:query:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + queryArgs);
          Object list = LocalDBUtil.query(mobileClient, queryArgs);
          result.success(list);
          break;
        case "insert":
          Map insArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:insert:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + insArgs);
          String objectId = LocalDBUtil.insert(mobileClient, insArgs);
          result.success(objectId);
          break;
        case "addToArray":
          Map arrArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:insert:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + arrArgs);
          long arrResult = LocalDBUtil.addToArray(mobileClient, arrArgs);
          result.success(arrResult);
          break;
        case "replace":
          Map replaceArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:replace:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + replaceArgs);
          long replaceResult = LocalDBUtil.replace(mobileClient, replaceArgs);
          result.success(replaceResult);
          break;
        case "delete":
          Map deleteArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:delete:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + deleteArgs);
          Object deleteResult = LocalDBUtil.delete(mobileClient, deleteArgs);
          result.success(deleteResult);
          break;
        case "getAll":
          Map getArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:getAll:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + getArgs);
          Object resultList = LocalDBUtil.getAll(mobileClient, getArgs);
          result.success(resultList);
          break;
        case "getOne":
          Map oneArgs = (Map) call.arguments;
          Log.d(TAG, "onMethodCall:getOne:  ..... arrArgs: \uD83C\uDF3F ☘ ️" + oneArgs);
          Object document = LocalDBUtil.getOne(mobileClient, oneArgs);
          result.success(document);
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
            switch (type) {
              case LOCAL_DATABASE:
                client = Stitch.initializeDefaultAppClient(key);
                Log.e(TAG, "\uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F setAppID: LOCAL MongoDB mobile client set up!  \uD83D\uDC7D \uD83D\uDC7D IGNORE \uD83D\uDC7D \uD83D\uDC7D");
                break;
              case ATLAS_DATABASE:
                result.error("003","MongoDB Atlas integration is under construction", "");
                return;

            }
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
      result.error("002", e.getMessage(), "Channel method error of some sort");
    }
  }
  private void setClient(String type) {
    switch (type) {
      case LOCAL_DATABASE:
        mobileClient =
                client.getServiceClient(LocalMongoDbService.clientFactory);
        Log.d(TAG, "🍎 onMethodCall:setClient: \uD83C\uDF40 LOCAL MongoDB client stood up!");
        break;
      case ATLAS_DATABASE:
        remoteMongoClient =
                client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        Log.d(TAG, "🍎 onMethodCall:setClient: \uD83C\uDF40 ATLAS MongoDB client stood up!");
        break;
      
    }
  }
  public static final String LOCAL_DATABASE = "local", ATLAS_DATABASE = "atlas";
}
