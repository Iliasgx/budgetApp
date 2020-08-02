package com.umbrella.budgetapp.Layouts.UpdateFragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umbrella.budgetapp.Customs.Spinners;
import com.umbrella.budgetapp.Customs.UpdateListeners;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Extensions.BaseFragment;
import com.umbrella.budgetapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;

public class UpdateCategory extends BaseFragment implements UpdateListeners {
    private static final String TAG = "Category";
    private String ID;
    private Type type;
    private Category category;
    private TypedArray array;

    @BindView(R.id.data_Card_Category_Image) ImageView img;
    @BindView(R.id.data_Card_Category_Name) EditText name;
    @BindView(R.id.data_Card_Category_Color) Spinner color;
    @BindView(R.id.data_Card_Category_Icon) Spinner icon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ID = UpdateAccountArgs.fromBundle(requireArguments()).getAccountID();

        Log.d("_Test", "[UpdateCategory] onViewCreated() ID is: " + ID);

        new Spinners.Colors(this, color, Spinners.Colors.Size.SMALL);
        new Spinners.Icons(this, icon);

        array = getResources().obtainTypedArray(R.array.icons);

        type = onLoad(ID == null ? Type.CREATE : Type.UPDATE);

        SetToolbar(getString(type.equals(Type.UPDATE) ?
                R.string.title_edit_category :
                R.string.title_add_category),
                ToolbarNavIcon.CANCEL,
                type.equals(Type.UPDATE) ?
                R.menu.save_delete :
                R.menu.save);

        setOnMenuItemClickListener(v -> {
            switch (v.getItemId()) {
                case R.id.menuLayout_SaveDelete_Delete: onDelete(); break;
                case R.id.menuLayout_SaveDelete_Save: onUpdate(); break;
            }
            return true;
        });
        setNavigationOnClickListener(v -> {
            name.clearFocus();
            navigateUp();
        });
    }

    /*
     * Hide keyboard if focus is lost.
     */
    @OnFocusChange(R.id.data_Card_Category_Name)
    protected void onFocusChange() {
        Log.d("_Test", "[UpdateCategory] onFocusChange() ");
        if (!name.hasFocus()) name.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public Type onLoad(Type type) {
        Log.d("_Test", "[UpdateCategory] onLoad() type is: " + type.name());

        if (type == Type.UPDATE) {
            firestore.document(ID).get().addOnSuccessListener(data -> {
                category = Objects.requireNonNull(data.toObject(Category.class));

                name.setText(category.getName());
                color.setSelection(category.getColor());
                icon.setSelection(category.getIcon());
                img.setImageResource(array.getResourceId(icon.getSelectedItemPosition(), 0));
                img.setBackgroundColor(getResources().getIntArray(R.array.colors)[color.getSelectedItemPosition()]);
            }).addOnFailureListener(f -> showSnackbar(getString(R.string.snackbar_notLoad)));
        } else category = new Category();
        return type;
    }

    @Override
    public void onUpdate() {
        name.clearFocus();
        if (reformatText(name.getText()).isEmpty()) {
            showToast(getString(R.string.toast_error_empty));
        } else {
            Log.d("_Test", "[UpdateCategory] onUpdate() ");

            category.setName(reformatText(name.getText()));
            category.setColor(color.getSelectedItemPosition());
            category.setIcon(icon.getSelectedItemPosition());

            if (type == Type.CREATE) {
                app.getUserDocument().collection(Category.COLLECTION).add(category).addOnCompleteListener(c -> {
                   showResultSnackbar(c.isSuccessful(), TAG, Message.CREATED);
                   if (c.isSuccessful()) navigateUp();
                });
            } else {
                firestore.document(ID).set(category).addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.UPDATED);
                    if (c.isSuccessful()) navigateUp();
                });
            }
        }
    }

    @Override
    public void onDelete() {
        Log.d("_Test", "[UpdateCategory] onDelete() ");
        name.clearFocus();
        firestore.document(ID).delete()
                .addOnCompleteListener(c -> {
                    showResultSnackbar(c.isSuccessful(), TAG, Message.DELETED);
                    if (c.isSuccessful()) navigateUp();
                });
    }

    @OnItemSelected(R.id.data_Card_Category_Color)
    public void changedColor(int position) {
        Log.d("_Test", "[UpdateCategory] changedColor() ");
        img.setBackgroundColor(getResources().getIntArray(R.array.colors)[position]);
    }

    @OnItemSelected(R.id.data_Card_Category_Icon)
    public void changeImage(int position) {
        Log.d("_Test", "[UpdateCategory] changeImage() ");
        img.setImageResource(array.getResourceId(position, 0));
    }

}