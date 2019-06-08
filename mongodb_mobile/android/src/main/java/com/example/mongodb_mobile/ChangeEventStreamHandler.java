package com.example.mongodb_mobile;

import android.util.Log;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.StreamHandler;

public class ChangeEventStreamHandler implements StreamHandler {

    private EventChannel.EventSink eventSink = null;
    private static final String TAG = ChangeEventStreamHandler.class.getSimpleName();
    @Override
    public void onListen(Object o, final EventChannel.EventSink sink) {
        Log.d(TAG, "onListen: \uD83D\uDEBC \uD83D\uDEBC  \uD83D\uDEBC \uD83D\uDEBC  onListen, set up event sink ");
        eventSink = sink;
    }

    @Override
    public void onCancel(Object o) {
        Log.d(TAG, "onCancel: \uD83D\uDEBC \uD83D\uDEBC  \uD83D\uDEBC \uD83D\uDEBC  eventChannel cancelled");
        eventSink = null;
    }
    void send(Object changeEvent) {
        Log.d(TAG, "send: \uD83D\uDEBC \uD83D\uDEBC  \uD83D\uDEBC \uD83D\uDEBC  putting " +
                changeEvent + " on the event sink");
        eventSink.success(changeEvent);
    }

    public EventChannel.EventSink getEventSink() {
        return eventSink;
    }
}
