package com.aftarobot.mobmongo;

import android.util.Log;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.StreamHandler;

public class ChangeEventStreamHandler implements StreamHandler {

    private EventChannel.EventSink eventSink = null;
    private static final String TAG = ChangeEventStreamHandler.class.getSimpleName();
    @Override
    public void onListen(Object o, final EventChannel.EventSink sink) {
                eventSink = sink;
    }

    @Override
    public void onCancel(Object o) {
        eventSink = null;
    }
    void send(Object changeEvent) {
        eventSink.success(changeEvent);
    }

    EventChannel.EventSink getEventSink() {
        return eventSink;
    }
}
