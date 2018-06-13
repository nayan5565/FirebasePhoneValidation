package com.example.nayan.myfirebasephonevalidation;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MUser {
    public String userId = "", name = "", imageUrl = "", phone = "";
    public String root = "", deviceToken = "";
    public int appVerCode;
    public double walletAmount;
    public String level1Id = "", level2Id = "", level3Id = "", level4Id = "", level5Id = "", level6Id = "", level7Id = "", level8Id = "", level9Id = "", level10Id = "";
    public boolean isActive;
    public int depositAmount;
    @ServerTimestamp
    public Date createdDate;
    @ServerTimestamp
    public Date lastMiningDate;
}
