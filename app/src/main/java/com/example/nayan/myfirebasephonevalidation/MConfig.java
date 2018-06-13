package com.example.nayan.myfirebasephonevalidation;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MConfig {
    public String notice;

    public int miningTime;
    public double rateInPercent;
    public double withdrawCharge;
    public double coinToMoney;
    public double minPercentForWithdraw;
    public int appVerCode;
    public boolean isUpdateMandatory, isMiningable, isWithdrawAvailable;
    @ServerTimestamp
    public Date today;
}
