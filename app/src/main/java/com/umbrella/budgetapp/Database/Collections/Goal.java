package com.umbrella.budgetapp.Database.Collections;

import com.google.firebase.Timestamp;

public class Goal {
    public static final String COLLECTION = "goal";
    public static final String USER = "userID";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String TARGET_AMOUNT = "targetAmount";
    public static final String SAVED_AMOUNT = "savedAmount";
    public static final String DESIRED_DATE = "desiredDate";
    public static final String START_DATE = "startDate";
    public static final String LAST_AMOUNT = "lastAmount";
    public static final String COLOR = "color";
    public static final String ICON = "icon";
    public static final String NOTE = "note";

    private String UserID;
    private String Name;
    private String Note;
    private int Status;
    private int Color;
    private int Icon;
    private String TargetAmount;
    private String SavedAmount;
    private String LastAmount;
    private Timestamp DesiredDate;
    private Timestamp StartDate;

    public enum StatusItem {
        ACTIVE,
        PAUSED,
        REACHED
    }

    public Goal() {
        //Empty constructor needed.
    }

    public Goal(String userID, String name, int status, String targetAmount, String savedAmount, String lastAmount, Timestamp desiredDate, Timestamp startDate, int color, int icon, String note) {
        UserID = userID;
        Name = name;
        Status = status;
        TargetAmount = targetAmount;
        SavedAmount = savedAmount;
        DesiredDate = desiredDate;
        StartDate = startDate;
        LastAmount = lastAmount;
        Color = color;
        Icon = icon;
        Note = note;
    }

    public String getUserID() {
        return UserID;
    }

    public String getName() {
        return Name;
    }

    public int getStatus() {
        return Status;
    }

    public String getTargetAmount() {
        return TargetAmount;
    }

    public String getSavedAmount() {
        return SavedAmount;
    }

    public String getLastAmount() {
        return LastAmount;
    }

    public Timestamp getDesiredDate() {
        return DesiredDate;
    }

    public Timestamp getStartDate() {
        return StartDate;
    }

    public int getColor() {
        return Color;
    }

    public int getIcon() {
        return Icon;
    }

    public String getNote() {
        return Note;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNote(String note) {
        Note = note;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public void setColor(int color) {
        Color = color;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }

    public void setTargetAmount(String targetAmount) {
        TargetAmount = targetAmount;
    }

    public void setSavedAmount(String savedAmount) {
        SavedAmount = savedAmount;
    }

    public void setLastAmount(String lastAmount) {
        LastAmount = lastAmount;
    }

    public void setDesiredDate(Timestamp desiredDate) {
        DesiredDate = desiredDate;
    }

    public void setStartDate(Timestamp startDate) {
        StartDate = startDate;
    }
}
