package com.surffountain.browser.database;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class DatabaseConverters {

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) return null;
        return String.join(",", list);
    }

    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null || value.isEmpty()) return null;
        return Arrays.asList(value.split(","));
    }
}
