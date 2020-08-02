package com.umbrella.budgetapp.Database.Collections;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class User {
    public static final String COLLECTION = "user";
    public static final String FULL_NAME = "fullName";
    public static final String EMAIL = "email";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String GENDER = "gender";
    public static final String LAST_CATEGORY = "lastUsedCategory";
    public static final String LAST_STORE = "lastUsedStore";
    public static final String PREF_HOME_CASHFLOW_FILTER = "prefHomeCashFlowFilter";
    public static final String PREF_HOME_RECORDS_FILTER = "prefHomeRecordsFilter";
    public static final String PREF_RECORDS_FILTER = "prefRecordsFilter";
    public static final String PREF_PLANNED_PAYMENTS_SORTING = "prefPlannedPaymentsSorting";
    public static final String PREF_STATISTICS_FILTER = "prefStatisticsFilter";

    private String FullName;
    private String Email;
    private Timestamp DateOfBirth;
    private int Gender;
    private DocumentReference LastUsedCategory;
    private DocumentReference LastUsedStore;
    private Integer PrefHomeCashFlowFilter;
    private Integer PrefHomeRecordsFilter;
    private String PrefRecordsFilter;
    private Integer PrefPlannedPaymentsSorting;
    private String PrefStatisticsFilter;

    public User() {
        //Empty constructor needed.
    }

    public User(String fullName, String email, Timestamp dateOfBirth, int gender, DocumentReference lastUsedCategory, DocumentReference lastUsedStore) {
        FullName = fullName;
        Email = email;
        DateOfBirth = dateOfBirth;
        Gender = gender;
        LastUsedCategory = lastUsedCategory;
        LastUsedStore = lastUsedStore;
    }

    public User(String fullName, String email, Timestamp dateOfBirth, int gender, DocumentReference lastUsedCategory, DocumentReference lastUsedStore, Integer prefHomeCashFlowFilter, Integer prefHomeRecordsFilter, String prefRecordsFilter, Integer prefPlannedPaymentsSorting, String prefStatisticsFilter) {
        FullName = fullName;
        Email = email;
        DateOfBirth = dateOfBirth;
        Gender = gender;
        LastUsedCategory = lastUsedCategory;
        LastUsedStore = lastUsedStore;
        PrefHomeCashFlowFilter = prefHomeCashFlowFilter;
        PrefHomeRecordsFilter = prefHomeRecordsFilter;
        PrefRecordsFilter = prefRecordsFilter;
        PrefPlannedPaymentsSorting = prefPlannedPaymentsSorting;
        PrefStatisticsFilter = prefStatisticsFilter;
    }

    public String getFullName() {
        return FullName;
    }

    public String getEmail() {
        return Email;
    }

    public Timestamp getDateOfBirth() {
        return DateOfBirth;
    }

    public int getGender() {
        return Gender;
    }

    public DocumentReference getLastUsedCategory() {
        return LastUsedCategory;
    }

    public DocumentReference getLastUsedStore() {
        return LastUsedStore;
    }

    public Integer getPrefHomeCashFlowFilter() {
        return PrefHomeCashFlowFilter;
    }

    public Integer getPrefHomeRecordsFilter() {
        return PrefHomeRecordsFilter;
    }

    public String getPrefRecordsFilter() {
        return PrefRecordsFilter;
    }

    public Integer getPrefPlannedPaymentsSorting() {
        return PrefPlannedPaymentsSorting;
    }

    public String getPrefStatisticsFilter() {
        return PrefStatisticsFilter;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setDateOfBirth(Timestamp dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public void setLastUsedCategory(DocumentReference lastUsedCategory) {
        LastUsedCategory = lastUsedCategory;
    }

    public void setLastUsedStore(DocumentReference lastUsedStore) {
        LastUsedStore = lastUsedStore;
    }

    public void setPrefHomeCashFlowFilter(Integer prefHomeCashFlowFilter) {
        PrefHomeCashFlowFilter = prefHomeCashFlowFilter;
    }

    public void setPrefHomeRecordsFilter(Integer prefHomeRecordsFilter) {
        PrefHomeRecordsFilter = prefHomeRecordsFilter;
    }

    public void setPrefRecordsFilter(String prefRecordsFilter) {
        PrefRecordsFilter = prefRecordsFilter;
    }

    public void setPrefPlannedPaymentsSorting(Integer prefPlannedPaymentsSorting) {
        PrefPlannedPaymentsSorting = prefPlannedPaymentsSorting;
    }

    public void setPrefStatisticsFilter(String prefStatisticsFilter) {
        PrefStatisticsFilter = prefStatisticsFilter;
    }
}
