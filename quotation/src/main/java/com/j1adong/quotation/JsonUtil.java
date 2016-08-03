package com.j1adong.quotation;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by J1aDong on 16/8/1.
 */
public class JsonUtil {
    public static String readLocalJson(Context context, String fileName) {
        String jsonStr;
        String resultStr = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(fileName)));
            while ((jsonStr = bufferedReader.readLine()) != null) {
                resultStr += jsonStr;
            }
        } catch (Exception e) {

        }
        return resultStr;
    }

    public static List<Quotation> getRandomLuoluo(Context context, String fileName) {
        List<Quotation> list = new Gson().fromJson(readLocalJson(context, fileName), new TypeToken<List<Quotation>>() {
        }.getType());
        return list;
    }
}
