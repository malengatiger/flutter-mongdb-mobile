package com.example.mongodb_mobile;

import android.util.Log;

import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.or;

class Helper {
    private static final String TAG = Helper.class.getSimpleName();

    static Bson getQueryFilter(Map carrier)  {
        Log.d(TAG, "\uD83C\uDF3F ☘️ query: carrier: " + carrier.toString());

        Map query = (Map) carrier.get("query");
        assert query != null;
        List<Bson> filters = new ArrayList<>();
        boolean isAnd = false;
        boolean isOr = false;
        Set<String> mset = query.keySet();
        for (String key : mset) {
            switch (key) {
                case "and":
                    isAnd = (boolean) query.get(key);
                    break;
                case "or":
                    isOr = (boolean) query.get(key);
                    break;
                case "eq":
                    Map m1 = (Map) query.get(key);
                    assert m1 != null;
                    Set<String> kSet = m1.keySet();
                    Bson filter = null;
                    for (String mKey: kSet) {
                        filter = eq(mKey, m1.get(mKey));
                    }
                    filters.add(filter);
                    break;
                case "gt":
                    Map m2 = (Map) query.get(key);
                    assert m2 != null;
                    Set<String> kSet2 = m2.keySet();
                    Bson filter2 = null;
                    for (String mKey: kSet2) {
                        filter2 = gt(mKey, m2.get(mKey));
                    }
                    filters.add(filter2);
                    break;
                case "lt":
                    Map m3 = (Map) query.get(key);
                    assert m3 != null;
                    Set<String> kSet3 = m3.keySet();
                    Bson filter3 = null;
                    for (String mKey: kSet3) {
                        filter3 = lt(mKey, m3.get(mKey));
                    }
                    filters.add(filter3);
                    break;
            }

        }
        Bson mFilter = null;
        if (isAnd) {
            mFilter = and(filters);
        }
        if (isOr) {
            mFilter = or(filters);
        }
        Log.d(TAG, "❤️  ❤️   ❤️ query: mFilter:  ❤️  ❤️ " + mFilter);
        assert mFilter != null;
        return mFilter;
    }
}
