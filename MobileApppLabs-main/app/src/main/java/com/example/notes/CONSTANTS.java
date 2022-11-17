package com.example.notes;

import android.Manifest;

public class CONSTANTS {

    public static String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    public static class EXTRA {
        public static String CATEGORY_ID = "EXTRA_CATEGORY_ID";
        public static String TASK_ID = "EXTRA_TASK_ID";
    }

    public static class ACTIVITY_RESULT {
        public static int VIEW_TASK_UPDATE = 7;
        public static int LOCATION_UPDATE = 10;
    }

    public static class API_KEYS {
        public static final String YANDEX_MAP_KIT = "6d15ea3c-e87a-40d5-b41c-0c4e274d0220";
        public static final String OPEN_WEATHER_MAP = "4fb7418a1e89572a1ef4ce7a24b5c9d4";
    }

    public static class CACHE {
        private static int category_id = -1;
        private static int task_id = -1;

        public static int getCategoryId() {
            return category_id;
        }

        public static int getTaskId() {
            return task_id;
        }

        public static void setCategoryId(int category_id) {
            CACHE.category_id = category_id;
        }

        public static void setTaskId(int task_id) {
            CACHE.task_id = task_id;
        }
    }

    public static class WEATHER {
        public static String URL_TEMPLATE = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}&units=metric&lang=ru";

        public static String getURL(double lat, double lon) {
            return URL_TEMPLATE.replace("{lat}", String.valueOf(lat)).replace("{lon}", String.valueOf(lon)).replace("{API key}", CONSTANTS.API_KEYS.OPEN_WEATHER_MAP);
        }
    }

}
