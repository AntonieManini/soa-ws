package com.github.gkislin.common.util;

import java.util.Map;

/**
 * User: gkislin
 * Date: 17.02.14
 */
public class ParamUtil {
    public static boolean getBoolean(String key, Map<String, String> map, boolean defValue) {
        String val = map.get(key);
        return (val == null) ? defValue : Boolean.valueOf(val);
    }

}
