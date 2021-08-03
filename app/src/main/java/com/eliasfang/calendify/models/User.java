package com.eliasfang.calendify.models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private String email = "";
    private String displayName = "";
    private String fmcToken = "";
    private Map<String, Map<String, String>> tasks;
    private List<String> friends;
    private String uid = "";


    public User() {
    }

    public User(String email, String displayName, String fmcToken, String uid) {
        this.email = email;
        this.displayName = displayName;
        this.fmcToken = fmcToken;
        this.tasks = new HashMap<String, Map<String, String>>();
        //tasks.put("-1", new HashMap<String, String>());
        this.friends = new ArrayList<String>();
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFmcToken() {
        return fmcToken;
    }

    public Map<String, Map<String, String>>  getTasks() {
        return tasks;
    }

    public List<String> getFriends() {
        return friends;
    }

    public String getUid() { return uid; }
}
