package com.umbrella.budgetapp.Database.Collections;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Objects;

public class Record implements Parcelable {
    public static final String COLLECTION = "record";
    public static final String USER = "userID";
    public static final String DESCRIPTION = "description";
    public static final String AMOUNT = "amount";
    public static final String CATEGORY = "categoryID";
    public static final String CURRENCY = "currency";
    public static final String TYPE = "type";
    public static final String ACCOUNT = "accountID";
    public static final String PAYMENT_TYPE = "paymentType";
    public static final String PAYEE = "payee";
    public static final String STORE = "storeID";
    public static final String DATETIME = "dateTime";
    public static final String LOCATION = "location";

    private String UserID;
    private String Description;
    private String Payee;
    private DocumentReference CategoryID;
    private DocumentReference AccountID;
    private DocumentReference StoreID;
    private DocumentReference CurrencyID;
    private int Type;
    private int PaymentType;
    private String Amount;
    private Timestamp DateTime;
    private GeoPoint Location;

    public Record() {
        //Empty constructor needed.
    }

    public Record(Parcel in) {
        UserID = in.readString();
        Description = in.readString();
        Payee = in.readString();
        CategoryID = FirebaseFirestore.getInstance().document(Objects.requireNonNull(in.readString()));
        AccountID = FirebaseFirestore.getInstance().document(Objects.requireNonNull(in.readString()));
        StoreID = FirebaseFirestore.getInstance().document(Objects.requireNonNull(in.readString()));
        CurrencyID = FirebaseFirestore.getInstance().document(Objects.requireNonNull(in.readString()));
        Type = in.readInt();
        PaymentType = in.readInt();
        Amount = in.readString();
        DateTime = in.readParcelable(Timestamp.class.getClassLoader());
        Location = new GeoPoint(in.readDouble(), in.readDouble());
    }

    public Record(String userID, String description, DocumentReference categoryID, String amount, DocumentReference currencyID, int type, DocumentReference accountID, int paymentType, String payee, DocumentReference storeID, Timestamp dateTime, GeoPoint location) {
        UserID = userID;
        Description = description;
        CategoryID = categoryID;
        Amount = amount;
        CurrencyID = currencyID;
        Type = type;
        AccountID = accountID;
        PaymentType = paymentType;
        Payee = payee;
        StoreID = storeID;
        DateTime = dateTime;
        Location = location;
    }

    public String getUserID() {
        return UserID;
    }

    public String getDescription() {
        return Description;
    }

    public DocumentReference getCategoryID() {
        return CategoryID;
    }

    public String getAmount() {
        return Amount;
    }

    public int getType() {
        return Type;
    }

    public DocumentReference getAccountID() {
        return AccountID;
    }

    public DocumentReference getCurrencyID() {
        return CurrencyID;
    }

    public int getPaymentType() {
        return PaymentType;
    }

    public String getPayee() {
        return Payee;
    }

    public DocumentReference getStoreID() {
        return StoreID;
    }

    public Timestamp getDateTime() {
        return DateTime;
    }

    public GeoPoint getLocation() {
        return Location;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setPayee(String payee) {
        Payee = payee;
    }

    public void setCategoryID(DocumentReference categoryID) {
        CategoryID = categoryID;
    }

    public void setAccountID(DocumentReference accountID) {
        AccountID = accountID;
    }

    public void setCurrencyID(DocumentReference currencyID) {
        CurrencyID = currencyID;
    }

    public void setStoreID(DocumentReference storeID) {
        StoreID = storeID;
    }

    public void setType(int type) {
        Type = type;
    }

    public void setPaymentType(int paymentType) {
        PaymentType = paymentType;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public void setDateTime(Timestamp dateTime) {
        DateTime = dateTime;
    }

    public void setLocation(GeoPoint location) {
        Location = location;
    }

    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(UserID);
        parcel.writeString(Description);
        parcel.writeString(Payee);
        parcel.writeString(CategoryID.getPath());
        parcel.writeString(AccountID.getPath());
        parcel.writeString(StoreID.getPath());
        parcel.writeString(CurrencyID.getPath());
        parcel.writeInt(Type);
        parcel.writeInt(PaymentType);
        parcel.writeString(Amount);
        parcel.writeParcelable(DateTime, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeDouble(Location.getLatitude());
        parcel.writeDouble(Location.getLongitude());
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel source) {
            return new Record(source);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };


}
