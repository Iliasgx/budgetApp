package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.umbrella.budgetapp.Customs.DateTimePickerDialog;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.User;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdateUserProfile extends BaseFragment implements UpdateListeners, DateTimePickerDialog.setOnFinishListener {
    private static final String TAG = "User Profile";
    private User user;

    @BindView(R.id.data_Card_User_NameFirst) EditText firstName;
    @BindView(R.id.data_Card_User_NameLast) EditText lastName;
    @BindView(R.id.data_Card_User_Email) EditText email;
    @BindView(R.id.data_Card_User_BirthDay) TextView birthDay;
    @BindView(R.id.data_Card_User_Gender) Spinner gender;
    @BindView(R.id.data_Card_User_Img) ImageView img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("_Test", "[UpdateUserProfile] onViewCreated() ");

        bindSimpleSpinner(gender, getStringArrayList(R.array.gender));
        onLoad(Type.UPDATE);

        SetToolbar(getString(R.string.title_edit_user_profile), ToolbarNavIcon.CANCEL, R.menu.save);
        setOnMenuItemClickListener(v -> {
            onUpdate();
            return true;
        });
        setNavigationOnClickListener(v -> {
            clearFocus();
            navigateUp();
        });
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateUserProfile] onLoad() ");

        app.getUserDocument().get().addOnSuccessListener(data -> {
            user = Objects.requireNonNull(data.toObject(User.class));

            String[] fullName = user.getFullName().split("\\s+");
            firstName.setText(fullName[0]);
            lastName.setText(fullName.length > 1 ? fullName[1] : "");
            email.setText(user.getEmail());
            birthDay.setText(toSimpleDateFormat(user.getDateOfBirth().toDate(), DateFormat.DATE));
            gender.setSelection(user.getGender());
            Glide.with(this).load(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl()).into(img);
        }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        return null;
    }

    @Override
    public void onUpdate() {
        clearFocus();
        if (reformatText(firstName.getText()).isEmpty() | reformatText(firstName.getText()).contains(" ")) {
            showToast(getString(R.string.user_profile_firstName_error));
        } else if (reformatText(lastName.getText()).contains(" ")) {
            showToast(getString(R.string.user_profile_lastName_error));
        } else if (reformatText(email.getText()).isEmpty()) {
            showToast(getString(R.string.user_profile_email_error));
        } else {
            Log.d("_Test", "[UpdateUserProfile] onUpdate() ");

            user.setFullName(reformatText(firstName.getText()) + reformatText(lastName.getText()));
            user.setEmail(reformatText(email.getText()));
            user.setGender(gender.getSelectedItemPosition());
            user.setDateOfBirth(new Timestamp((Date)birthDay.getTag()));

            app.getUserDocument().set(user).addOnCompleteListener(c -> {
                showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                if (c.isSuccessful()) navigateUp();
            });
        }
    }

    @Override
    public void onDelete() {
        //User can't be deleted.
    }

    private void clearFocus() {
        firstName.clearFocus();
        lastName.clearFocus();
        email.clearFocus();
        Log.d("_Test", "[UpdateUserProfile] clearFocus() ");
    }

    @OnClick(R.id.data_Card_User_BirthDay)
    protected void updateDate() {
        Log.d("_Test", "[UpdateUserProfile] updateDate() ");
        Calendar c = Calendar.getInstance();
        c.setTime(user.getDateOfBirth().toDate());
        DateTimePickerDialog.getInstance(this, c, DateTimePickerDialog.Type.DATE, DateTimePickerDialog.DateOptions.BEFORE_TODAY);
    }

    @Override
    public void onFinishDialog(Calendar calendar) {
        Log.d("_Test", "[UpdateUserProfile] onFinishDialog() ");
        birthDay.setText(toSimpleDateFormat(calendar.getTime(), DateFormat.DATE));
        birthDay.setTag(calendar.getTime());
    }
}

