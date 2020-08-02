package com.umbrella.budgetapp.Extensions;

import android.content.res.TypedArray;
import android.util.Log;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.umbrella.budgetapp.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class CloudRecyclerAdapter<T, VH extends ViewHolder> extends Adapter<VH> implements ChangeEventListener, LifecycleObserver {
    private final ObservableSnapshotArray<T> mSnapshots;

    public CloudRecyclerAdapter(@NonNull FirestoreRecyclerOptions<T> options) {
        this.mSnapshots = options.getSnapshots();
        if (options.getOwner() != null) {
            options.getOwner().getLifecycle().addObserver(this);
        }
    }

    @OnLifecycleEvent(Event.ON_START)
    public void startListening() {
        if (!this.mSnapshots.isListening(this)) {
            this.mSnapshots.addChangeEventListener(this);
        }
    }

    @OnLifecycleEvent(Event.ON_STOP)
    public void stopListening() {
        this.mSnapshots.removeChangeEventListener(this);
        this.notifyDataSetChanged();
    }

    @OnLifecycleEvent(Event.ON_DESTROY)
    void cleanup(LifecycleOwner source) {
        source.getLifecycle().removeObserver(this);
    }

    @NonNull
    public ObservableSnapshotArray<T> getSnapshots() {
        return this.mSnapshots;
    }

    @NonNull
    private T getItem(int position) {
        return this.mSnapshots.get(position);
    }

    public int getItemCount() {
        return this.mSnapshots.isListening(this) ? this.mSnapshots.size() : 0;
    }

    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
        switch(type) {
            case ADDED:
                this.notifyItemInserted(newIndex);
                break;
            case CHANGED:
                this.notifyItemChanged(newIndex);
                break;
            case REMOVED:
                this.notifyItemRemoved(oldIndex);
                break;
            case MOVED:
                this.notifyItemMoved(oldIndex, newIndex);
                break;
            default:
                throw new IllegalStateException("Incomplete case statement");
        }
    }

    public void onDataChanged() {
    }

    public void onError(@NonNull FirebaseFirestoreException e) {
        Log.w("CloudRecyclerView", "onError", e);
    }

    public void onBindViewHolder(@NonNull VH holder, int position) {
        this.onBindViewHolder(holder, position, this.getItem(position));
    }

    protected abstract void onBindViewHolder(@NonNull VH holder, int position, @NonNull T t);

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public interface OnItemClickListener {
        /**
         * Interface for handling the OnItemClick event.
         * @param documentSnapshot The current Firestore document.
         * @param position The current position in the RecyclerView.
         */
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    /**
     * Method for implementing the OnItemClickListener interface.
     * @param listener Current OnItemClickListener
     * @see OnItemClickListener
     */
    public abstract void setOnItemClickListener(OnItemClickListener listener);

    protected static String[] getArray(int ArrType){
        return App.getInstance().getContext().getResources().getStringArray(ArrType);
    }

    /**
     * Get a String item from the String-Array.
     * @param ArrType Select the array.
     * @param position Position in the array.
     * @return The item at position in the ArrType array.
     */
    @SuppressWarnings("SameParameterValue")
    protected static String getItem(int ArrType, int position){
        return getArray(ArrType)[position];
    }

    protected static int[] getDrawableArray(int ArrType){
        return App.getInstance().getContext().getResources().getIntArray(ArrType);
    }

    /**
     * Get a Drawable such as an icon or color from the Integer-Array.
     * @param ArrType Select the array.
     * @param position Position in the array.
     * @return The item at position in the ArrType array.
     */
    @SuppressWarnings("SameParameterValue")
    protected static int getDrawable(int ArrType, int position){
        return getDrawableArray(ArrType)[position];
    }

    /**
     * A Integer list that represents an int array.
     * @param resId The id of an int array.
     * @return A Integer list containing the array elements.
     */
    protected List<Integer> getIntegerArrayList(@ArrayRes int resId) {
        List<Integer> values = new ArrayList<>();

        for (int i: App.getInstance().getContext().getResources().getIntArray(resId)) values.add(i);
        return values;
    }

    /**
     * A string list that represents a string array.
     * @param resId The id of a string array list.
     * @return A string list of the ArrayList.
     */
    protected List<String> getStringArrayList(@ArrayRes int resId) {
        return Arrays.asList(App.getInstance().getContext().getResources().getStringArray(resId));
    }

    /**
     * A TypedArray list that represents an int array.
     * @param resId Theid of an int array.
     * @return A TypedArray list containing the array elements.
     */
    protected TypedArray getTypedArray(@ArrayRes int resId) {
        return App.getInstance().getContext().getResources().obtainTypedArray(resId);
    }

    /**
     * Represents a SimpleDateFormat as a readable date and/or time.
     *
     * <p>Formats:
     *      <p> DATE
     *      <p> TIME
     *      <p> DATE_TIME
     * @param date The Date to represent.
     * @param format The format that can be used.
     *
     * @return The readable string.
     * @see SimpleDateFormat
     * @see BaseFragment.DateFormat
     */
    protected String toSimpleDateFormat(Date date, BaseFragment.DateFormat format) {
        String form = "";
        switch (format) {
            case DATE: form = "dd-MM-yyyy"; break;
            case TIME: form = "HH:mm:ss"; break;
            case DATE_TIME: form = "dd-MM-yyyy HH:mm:ss"; break;
        }
        return new SimpleDateFormat(form, Locale.getDefault()).format(date);
    }
}
