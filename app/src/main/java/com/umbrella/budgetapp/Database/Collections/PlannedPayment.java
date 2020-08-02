package com.umbrella.budgetapp.Database.Collections;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class PlannedPayment {
    public static final String COLLLECTION = "plannedPayment";
    public static final String USER = "userID";
    public static final String ACCOUNT = "accountID";
    public static final String NAME = "name";
    public static final String START_DATE = "startDate";
    public static final String FREQUENCY = "frequency";
    public static final String CATEGORY = "categoryID";
    public static final String TYPE = "type";
    public static final String PAYMENT_TYPE = "paymentType";
    public static final String PAYEE = "payee";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currencyID";
    public static final String NOTE = "note";
    public static final String REMINDER_OPTIONS = "reminderOptions";

    private String UserID;
    private String Name;
    private String Payee;
    private String Note;
    private DocumentReference AccountID;
    private DocumentReference CategoryID;
    private DocumentReference CurrencyID;
    private Timestamp StartDate;
    private Map<String, String> Frequency;
    private int Type;
    private int PaymentType;
    private int ReminderOptions;
    private String Amount;

    public PlannedPayment() {
        //Empty constructor needed
    }

    public PlannedPayment(String userID, DocumentReference accountID, String name, Timestamp startDate, Map<String, String> frequency, DocumentReference categoryID, int type, int paymentType, String payee, String amount, DocumentReference currencyID, String note, int reminderOptions) {
        UserID = userID;
        AccountID = accountID;
        Name = name;
        StartDate = startDate;
        Frequency = frequency;
        CategoryID = categoryID;
        Type = type;
        PaymentType = paymentType;
        Payee = payee;
        Amount = amount;
        CurrencyID = currencyID;
        Note = note;
        ReminderOptions = reminderOptions;
    }

    public String getUserID() {
        return UserID;
    }

    public DocumentReference getAccountID() {
        return AccountID;
    }

    public String getName() {
        return Name;
    }

    public Timestamp getStartDate() {
        return StartDate;
    }

    public Map<String, String> getFrequency() {
        return Frequency;
    }

    public DocumentReference getCategoryID() {
        return CategoryID;
    }

    public int getType() {
        return Type;
    }

    public int getPaymentType() {
        return PaymentType;
    }

    public String getPayee() {
        return Payee;
    }

    public String getAmount() {
        return Amount;
    }

    public DocumentReference getCurrencyID() {
        return CurrencyID;
    }

    public String getNote() {
        return Note;
    }

    public int getReminderOptions() {
        return ReminderOptions;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPayee(String payee) {
        Payee = payee;
    }

    public void setNote(String note) {
        Note = note;
    }

    public void setAccountID(DocumentReference accountID) {
        AccountID = accountID;
    }

    public void setCategoryID(DocumentReference categoryID) {
        CategoryID = categoryID;
    }

    public void setCurrencyID(DocumentReference currencyID) {
        CurrencyID = currencyID;
    }

    public void setStartDate(Timestamp startDate) {
        StartDate = startDate;
    }

    public void setFrequency(Map<String, String> frequency) {
        Frequency = frequency;
    }

    public void setType(int type) {
        Type = type;
    }

    public void setPaymentType(int paymentType) {
        PaymentType = paymentType;
    }

    public void setReminderOptions(int reminderOptions) {
        ReminderOptions = reminderOptions;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public static class setFrequency {
        private int Repeating;
        private int Period;
        private int Ending;
        private Timestamp EndingUntil;
        private int EndingEvents;
        private String Days;
        private int RecurrentMonth;

        public Map<String, String> setDaily(int Period, int Ending, Timestamp EndingUntil, int EndingEvents) {
            this.Repeating = 1;
            this.Period = Period;
            this.Ending = Ending;
            this.EndingUntil = EndingUntil;
            this.EndingEvents = EndingEvents;
            this.Days = "0000000";
            this.RecurrentMonth = 0;
            return setParams();
        }

        public Map<String, String> setWeekly(int Period, int Ending, Timestamp EndingUntil, int EndingEvents, String Days) {
            this.Repeating = 2;
            this.Period = Period;
            this.Ending = Ending;
            this.EndingUntil = EndingUntil;
            this.EndingEvents = EndingEvents;
            this.Days = Days;
            this.RecurrentMonth = 0;
            return setParams();
        }

        public Map<String, String> setMonthly(int Period, int Ending, Timestamp EndingUntil, int EndingEvents, int RecurrentMonth) {
            this.Repeating = 2;
            this.Period = Period;
            this.Ending = Ending;
            this.EndingUntil = EndingUntil;
            this.EndingEvents = EndingEvents;
            this.Days = "0000000";
            this.RecurrentMonth = RecurrentMonth;
            return setParams();
        }

        public Map<String, String> setYearly(int Period, int Ending, Timestamp EndingUntil, int EndingEvents) {
            this.Repeating = 1;
            this.Period = Period;
            this.Ending = Ending;
            this.EndingUntil = EndingUntil;
            this.EndingEvents = EndingEvents;
            this.Days = "0000000";
            this.RecurrentMonth = 0;
            return setParams();
        }

        private Map<String, String> setParams() {
            Map<String, String> map = new HashMap<>();
            map.put("repeating", String.valueOf(Repeating));
            map.put("period", String.valueOf(Period));
            map.put("ending", String.valueOf(Ending));
            map.put("endingUntil", String.valueOf(EndingUntil.toDate().getTime()));
            map.put("endingEvents", String.valueOf(EndingEvents));
            map.put("days", Days);
            map.put("recurrentMonth", String.valueOf(RecurrentMonth));
            return map;
        }

    }

}
