package com.aftarobot.flutter_mongodb_mobile;

import java.util.Map;

public class Carrier {
    String db, collection;
    Map data;

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String toString() {
        return db + " 🍎 " + collection + " 🍎 " + data.toString();
    }
}
