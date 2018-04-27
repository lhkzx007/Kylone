package com.kylone.utils;

import android.text.TextUtils;

/**
 * CreatedbyZackon2018/4/20
 */

public enum Conver {

    All("All", "0"),

    Action("Action", "1"),

    Adventure("Adventure", "2"),

    Anime("Anime", "3"),

    Family("Family", "4"),

    Comedy("Comedy", "5"),

    Biography("Biography", "6"),

    News("News", "7"),

    Local("Local", "8"),

    Global("Global", "9"),

    Musical("Musical", "10"),

    Kids("Kids", "11"),

    Documentary("Documentary", "12"),

    Movie("Movie", "13"),

    Series("Series", "14"),

    Horror("Horror", "15"),

    Custom("Custom", "16"),

    Drama("Drama", "17"),

    Sci_Fi("Sci-Fi", "18"),

    Fantasy("Fantasy", "19"),

    Thriller("Thriller", "20"),

    Urban("Urban", "21"),

    Sport("Sport", "22"),

    Romance("Romance", "23"),

    Other("Other", "24"),

    Trailer("Trailer", "43"),

    English("English", "46"),

    German("German", "47"),

    French("French", "48"),

    Russian("Russian", "49"),

    Chinese("Chinese", "50"),

    Thai("Thai", "51"),

    Arabic("Arabic", "52"),

    Turkish("Turkish", "53"),

    Japanese("Japanese", "54"),

    Korean("Korean", "55");
    String mName;
    String mId;

    Conver(String name, String id) {
        mName = name;
        mId = id;
    }

    public static String conver(String mId){
        Conver[] convers = Conver.values();
        for (Conver conver : convers) {
            if (TextUtils.equals(mId,conver.mId)){
                return conver.mName;
            }
        }
        return mId;
    }


}
