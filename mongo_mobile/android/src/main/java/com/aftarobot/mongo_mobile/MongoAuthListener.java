package com.aftarobot.mongo_mobile;

import com.mongodb.stitch.core.auth.StitchUserProfile;

public interface MongoAuthListener {
    void onAuth(StitchUserProfile userProfile);
    void onError(String message);
}
