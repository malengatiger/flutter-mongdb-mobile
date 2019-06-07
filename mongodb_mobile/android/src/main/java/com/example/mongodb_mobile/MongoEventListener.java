package com.example.mongodb_mobile;

import com.mongodb.stitch.core.services.mongodb.remote.ChangeEvent;

public interface MongoEventListener {
    void onChangeEvent(ChangeEvent changeEvent);
    void onError(String message);
}
