package com.example.nayan.myfirebasephonevalidation;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MUser {
    public String userId = "", name = "", imageUrl = "", phone = "";
    public String  deviceToken = "";
    public boolean isActive;
    @ServerTimestamp
    public Date createdDate;
    @ServerTimestamp
    public Date lastMiningDate;
}
