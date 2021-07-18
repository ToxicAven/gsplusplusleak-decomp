// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.player;

import com.google.common.collect.Maps;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import org.apache.commons.io.IOUtils;
import java.net.URL;
import java.util.Map;

public class NameUtil
{
    private static final Map<String, String> uuidNameCache;
    
    public static String resolveName(String uuid) {
        uuid = uuid.replace("-", "");
        if (NameUtil.uuidNameCache.containsKey(uuid)) {
            return NameUtil.uuidNameCache.get(uuid);
        }
        final String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
        try {
            final String nameJson = IOUtils.toString(new URL(url));
            if (nameJson != null && nameJson.length() > 0) {
                final JSONArray jsonArray = (JSONArray)JSONValue.parseWithException(nameJson);
                if (jsonArray != null) {
                    final JSONObject latestName = jsonArray.get(jsonArray.size() - 1);
                    if (latestName != null) {
                        return latestName.get("name").toString();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    static {
        uuidNameCache = Maps.newConcurrentMap();
    }
}
