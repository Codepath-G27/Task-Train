package com.eliasfang.calendify.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendRequest {

    Map<String, ArrayList<String>> requests = new HashMap<>();

    public FriendRequest() {
    }

    public FriendRequest( Map<String, ArrayList<String>> requests) {
        this.requests = requests;
    }


    public Map<String, ArrayList<String>> getRequests() {
        return requests;
    }
}
