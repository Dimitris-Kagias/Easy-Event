package com.example.easyevent;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class JsonHelper {

    public static List<User> loadUsers(Context context) {
        Gson gson = new Gson();
        String jsonFileString = getJsonDataFromAsset(context, "user.json");
        Type listUserType = new TypeToken<List<User>>() {}.getType();
        return gson.fromJson(jsonFileString, listUserType);
    }

    private static String getJsonDataFromAsset(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonString;
    }
}
