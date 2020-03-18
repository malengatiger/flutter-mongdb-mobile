package com.aftarobot.mobmongo;

import android.util.Log;

import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.or;

class Helper {
    private static final String TAG = Helper.class.getSimpleName();
    static Bson getQueryFilter(Map carrier)  {
        Map query = (Map) carrier.get("query");
        assert query != null;
        List<Bson> filters = new ArrayList<>();
        boolean isAnd = false;
        boolean isOr = false;
        Set keySet = query.keySet();
        for (Object key : keySet) {
            switch ((String) key) {
                case "and":
                    isAnd = (boolean) query.get(key);
                    break;
                case "or":
                    isOr = (boolean) query.get(key);
                    break;
                case "eq":
                    Map m1 = (Map) query.get(key);
                    assert m1 != null;
                    Set kSet = m1.keySet();
                    Bson filter = null;
                    for (Object mKey: kSet) {
                        filter = eq((String)mKey, m1.get(mKey));
                    }
                    filters.add(filter);
                    break;
                case "gt":
                    Map m2 = (Map) query.get(key);
                    assert m2 != null;
                    Set kSet2 = m2.keySet();
                    Bson filter2 = null;
                    for (Object mKey: kSet2) {
                        filter2 = gt((String) mKey, Objects.requireNonNull(m2.get(mKey)));
                    }
                    filters.add(filter2);
                    break;
                case "lt":
                    Map m3 = (Map) query.get(key);
                    assert m3 != null;
                    Set kSet3 = m3.keySet();
                    Bson filter3 = null;
                    for (Object mKey: kSet3) {
                        filter3 = lt((String) mKey, Objects.requireNonNull(m3.get(mKey)));
                    }
                    filters.add(filter3);
                    break;

            }

        }
        Bson mFilter = null;
        if (filters.size() == 1) {
            mFilter = filters.get(0);
        } else {
            if (isAnd) {
                mFilter = and(filters);
            }
            if (isOr) {
                mFilter = or(filters);
            }
        }
        assert mFilter != null;
        return mFilter;
    }

}
