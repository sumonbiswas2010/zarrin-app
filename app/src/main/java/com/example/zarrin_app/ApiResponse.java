package com.example.zarrin_app;

import java.util.List;

public class ApiResponse {
    public boolean success;
    public Data data;

    public static class Data {
        public String summary;
        public List<String> alerts;
        public List<String> tasks;
        public List<String> calender_events; // Make sure this is a List<String>, not a String
    }
}
