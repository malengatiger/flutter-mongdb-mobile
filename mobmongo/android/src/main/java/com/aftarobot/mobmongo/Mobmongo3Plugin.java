package com.aftarobot.mobmongo;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mongodb.client.MongoClient;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;
import com.mongodb.stitch.core.auth.StitchUserProfile;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * Mobmongo3Plugin
 */
public class Mobmongo3Plugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private static MongoClient mobileClient;
    private static EventChannel eventChannel;
    private static ChangeEventStreamHandler changeEventStreamHandler;
    private static final String TAG = Mobmongo3Plugin.class.getSimpleName(), MONGO_CHANGE_EVENTS = "mongo_change_events";


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "mobmongo");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
        try {
            setupLocalDatabase("localApp");
            switch (call.method) {
                case "deleteMany":
                    Map<String, Object> map = (Map) call.arguments();
                    long resultDelete = LocalDBUtil.deleteMany(mobileClient, map);
                    result.success(resultDelete);
                    break;
                case "query":
                    Map<String, Object> queryArgs = (Map) call.arguments;
                    Object list = LocalDBUtil.query(mobileClient, queryArgs);
                    result.success(list);
                    break;
                case "createIndex":
                    Map arguments = (Map) call.arguments;
                    LocalDBUtil.createIndex(mobileClient, arguments);
                    result.success("Index created");
                    break;

                case "insert":
                    Map insArgs = (Map) call.arguments;
                    String objectId = LocalDBUtil.insert(mobileClient, insArgs);
                    result.success(objectId);
                    break;
                case "addToArray":
                    Map arrArgs = (Map) call.arguments;
                    long arrResult = LocalDBUtil.addToArray(mobileClient, arrArgs);
                    result.success(arrResult);
                    break;
                case "update":
                    Map replaceArgs = (Map) call.arguments;
                    long replaceResult = LocalDBUtil.update(mobileClient, replaceArgs);
                    result.success(replaceResult);
                    break;
                case "delete":
                    Map deleteArgs = (Map) call.arguments;
                    Object deleteResult = LocalDBUtil.delete(mobileClient, deleteArgs);
                    result.success(deleteResult);
                    break;
                case "getAll":
                    Map getArgs = (Map) call.arguments;
                    Object resultList = LocalDBUtil.getAll(mobileClient, getArgs);
                    result.success(resultList);

                    break;
                case "getOne":
                    Map oneArgs = (Map) call.arguments;
                    Object document = LocalDBUtil.getOne(mobileClient, oneArgs);
                    result.success(document);
                    break;
                case "sync":
                    final Map syncArgs = (Map) call.arguments;
                    result.error("014", "\uD83C\uDF36  Sync not appropriate for \uD83C\uDF36 LOCAL \uD83C\uDF36  database", "");
                    break;
                case "getPlatformVersion":
                    result.success("\uD83C\uDF38 \uD83C\uDF38 \uD83C\uDF38 ANDROID " + android.os.Build.VERSION.RELEASE + " " + Build.DEVICE
                            + " " + Build.MODEL);
                    break;
                case "setAppID":
                    String appID = call.argument("appID");
                    setupLocalDatabase(appID);
                    break;
                default:
                    Log.e(TAG, "onMethodCall: \uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F \uD83D\uDC7F  method " + call.method + " is not implemented, Houston, help!");
                    result.notImplemented();
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "onMethodCall: ", e);
            result.error("002", e.getMessage(), "onMethodCall: error of some sort. Houston kinda error :(");
        }
    }

    private void setupLocalDatabase(String key) {
        if (mobileClient != null) return;
        StitchAppClient client = Stitch.initializeDefaultAppClient(key);
        mobileClient =
                client.getServiceClient(LocalMongoDbService.clientFactory);
        Log.d(TAG, "\uD83C\uDF38 \uD83C\uDF38 \uD83C\uDF38 \uD83C\uDF38 \uD83C\uDF38 \uD83C\uDF38" +
                "  setupLocalDatabase: We cool with LOCAL appID: " +
                "🍎 " + key + " 🍎 on the wild side   \uD83C\uDF38 \uD83C\uDF38 \uD83C\uDF38 \uD83C\uDF38");
    }

    private static final String LOCAL_DATABASE = "local";

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
