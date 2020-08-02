package com.umbrella.budgetapp.Extensions;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.umbrella.budgetapp.Database.Collections.User;
import com.umbrella.budgetapp.MainActivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class App {
    private static final App Instance = new App();
    private static MainActivity context;
    private static Source source;

    public static App getInstance() {
        return Instance;
    }

    private App() {
    }

    /** User path of currentUser which is logged in. */
    private String user;
    /** Reference to the last used category in records. */
    private DocumentReference lastUsedCategory;
    /** Reference to the last used store in records. */
    private DocumentReference lastUsedStore;
    /** List of all selected accounts. Can be used to retrieve the first selected one to set as default. */
    private List<DocumentReference> selectedAccounts;
    /** Active filter of home cashflow. */
    private Integer homeCashFlowFilter;
    /** Active filter of home recordsView. */
    private Integer homeRecordsFilter;
    /** Active filter of records. */
    private String recordsFilter;
    /** Active sorting of planned payments. */
    private Integer plannedPaymentsSorting;
    /** Active filter of statistics. */
    private String statisticsFilter;

    public void setContext(MainActivity mainActivity) {
        context = mainActivity;
    }

    public MainActivity getContext() {
        return context;
    }

    public Source getSource() {
        return source;
    }

    /** Returns DocumentReference of current user. Can be used to retrieve any data from the user document or sub collections. */
    public DocumentReference getUserDocument() {
        if (user != null) {
            return FirebaseFirestore.getInstance().collection(User.COLLECTION).document(user);
        } else {
            throw new NullPointerException("User is not set.");
        }
    }

    /** Returns ID of current user */
    public String getUser() {
        return user;
    }

    /** Returns DocumentReference of the last used category */
    public DocumentReference getLastUsedCategory() {
        return lastUsedCategory;
    }

    /** Returns DocumentReference of the last used store */
    public DocumentReference getLastUsedStore() {
        return lastUsedStore;
    }

    /** Returns String list of all selected accounts ID's */
    public List<DocumentReference> getSelectedAccounts() {
        return selectedAccounts;
    }

    /** Returns the filter of home cashflow */
    public Integer getHomeCashFlowFilter() {
        return homeCashFlowFilter;
    }

    /** Returns the filter of home records */
    public Integer getHomeRecordsFilter() {
        return homeRecordsFilter;
    }

    /** Returns the filter of records. */
    public String getRecordsFilter() {
        return recordsFilter;
    }

    /** Returns the sorting method of planned payments */
    public Integer getPlannedPaymentsSorting() {
        return plannedPaymentsSorting;
    }

    /** Returns the filter of statistics */
    public String getStatisticsFilter() {
        return statisticsFilter;
    }

    /** Sets the current user ID */
    public void setUser(String mUser) {
        this.user = mUser;
        getUserDocument().get(source).addOnSuccessListener(gets -> {
            User us = gets.toObject(User.class);
            setLastUsedCategory(Objects.requireNonNull(us).getLastUsedCategory());
            setLastUsedStore(us.getLastUsedStore());
        });
    }

    /** Sets the last used category */
    public void setLastUsedCategory(DocumentReference LastUsedCategory) {
        this.lastUsedCategory = LastUsedCategory;
    }

    /** Sets the last used store */
    public void setLastUsedStore(DocumentReference LastUsedStore) {
        this.lastUsedStore = LastUsedStore;
    }

    /** Sets all the selected accounts into a String list */
    public void setSelectedAccounts(List<DocumentReference> selectedAccounts) {
        this.selectedAccounts = selectedAccounts;
    }

    /** Sets the filter of home cashflow. */
    public void setHomeCashFlowFilter(Integer homeCashFlowFilter) {
        this.homeCashFlowFilter = homeCashFlowFilter;
    }

    /** Sets the filter of home records. */
    public void setHomeRecordsFilter(Integer homeRecordsFilter) {
        this.homeRecordsFilter = homeRecordsFilter;
    }

    /** Sets the filter of records. */
    public void setRecordsFilter(String recordsFilter) {
        this.recordsFilter = recordsFilter;
    }

    /** Sets the sorting method of planned payments. */
    public void setPlannedPaymentsSorting(Integer plannedPaymentsSorting) {
        this.plannedPaymentsSorting = plannedPaymentsSorting;
    }

    /** Sets the filter of statistics. */
    public void setStatisticsFilter(String statisticsFilter) {
        this.statisticsFilter = statisticsFilter;
    }

    /** Method for splitting the filter string into readable strings */
    public String[] splitFilterString(String filterable) {
        return filterable.split("\\s+");
    }

    /** Method for putting the filter into a canonical string */
    public String matchFilterString(int type, int periodOption, int weekNr, Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(startDate);
        end.setTime(endDate);

        return type + " " +                                 // 1,2,3
               periodOption + " " +                         // {1} 1,2,3,4
               weekNr + " " +                               // {2} 1-52
               start.get(Calendar.DAY_OF_MONTH) + " " +     // {3 - start} day / month / year
               start.get(Calendar.MONTH) + " " +
               start.get(Calendar.YEAR) + " " +
               end.get(Calendar.DAY_OF_MONTH) + " " +       // {3 - end} day / month / year
               end.get(Calendar.MONTH) + " " +
               end.get(Calendar.YEAR);
    }

    /** Save all data into preferences */
    public void setDataPreferences(MainActivity activity) {
        Set<String> stringSet = new HashSet<>();
        selectedAccounts.forEach(acc -> stringSet.add(acc.getPath()));

        SharedPreferences.Editor preferences = activity.getPreferences(Context.MODE_PRIVATE).edit();
        preferences.putBoolean("hasCache", false);
        preferences.putStringSet("selectedAccounts", stringSet);
        preferences.putInt(User.PREF_HOME_CASHFLOW_FILTER, homeCashFlowFilter);
        preferences.putInt(User.PREF_HOME_RECORDS_FILTER, homeRecordsFilter);
        preferences.putString(User.PREF_RECORDS_FILTER, recordsFilter);
        preferences.putInt(User.PREF_PLANNED_PAYMENTS_SORTING, plannedPaymentsSorting);
        preferences.putString(User.PREF_STATISTICS_FILTER, statisticsFilter);
        preferences.apply();
    }

    /**
     * Initialize the app data with the information of the current user.
     *
     * Default data of the preferences is used but will be updated with database data.
     * @param mainActivity The MainActivity that contains the context structure.
     * @param user The user which data has to be updated.
     */
    public void initData(MainActivity mainActivity,  User user) {
        SharedPreferences preferences = mainActivity.getPreferences(Context.MODE_PRIVATE);

        Set<String> stringSet = preferences.getStringSet("selectedAccounts", Collections.emptySet());
        Objects.requireNonNull(stringSet);
        stringSet.forEach(set -> selectedAccounts.add(FirebaseFirestore.getInstance().document(set)));

        source = (preferences.getBoolean("hasCache", false) ? Source.CACHE : Source.DEFAULT);

        if (user == null) {
            homeCashFlowFilter = preferences.getInt(User.PREF_HOME_CASHFLOW_FILTER, 7);
            homeRecordsFilter = preferences.getInt(User.PREF_HOME_RECORDS_FILTER, 7);
            recordsFilter = preferences.getString(User.PREF_RECORDS_FILTER, "0 0 0 0 0 0 0 0 0");
            plannedPaymentsSorting = preferences.getInt(User.PREF_PLANNED_PAYMENTS_SORTING, 0);
            statisticsFilter = preferences.getString(User.PREF_STATISTICS_FILTER, "");
        } else {
            lastUsedCategory = user.getLastUsedCategory();
            lastUsedStore = user.getLastUsedStore();
            homeCashFlowFilter = user.getPrefHomeCashFlowFilter();
            homeRecordsFilter = user.getPrefHomeRecordsFilter();
            recordsFilter = user.getPrefRecordsFilter();
            plannedPaymentsSorting = user.getPrefPlannedPaymentsSorting();
            statisticsFilter = user.getPrefStatisticsFilter();
        }
    }
}