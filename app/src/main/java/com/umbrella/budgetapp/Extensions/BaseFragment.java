package com.umbrella.budgetapp.Extensions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.ArrayRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.umbrella.budgetapp.MainActivity;
import com.umbrella.budgetapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    private Unbinder unbinder;
    private Toolbar toolbar;

    //Set a new Instance of App.
    protected static App app = App.getInstance();

    //Set a new Instance of FirebaseFirestore.
    @SuppressLint("StaticFieldLeak")
    protected static FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    /**
     * Enum for setting a pre-defined icon into the toolbar.
     */
    public enum ToolbarNavIcon {
        MENU (R.drawable.menu),
        BACK (R.drawable.back),
        CANCEL (R.drawable.cancel);

        private int Icon;

        public int getIcon() {
            return Icon;
        }

        ToolbarNavIcon(int icon) {
            Icon = icon;
        }
    }

    /**
     * Expanded constructor for Toolbar.
     * An constructor bases on the AndroidX Toolbar for implementing basic
     * function for the Toolbar.
     *
     * @Param Title For setting the title of a Fragment.
     */
    public void SetToolbar(String mTitle) {
        SetToolbar(mTitle, ToolbarNavIcon.MENU, 0);
    }

    /**
     * Expanded constructor for Toolbar.
     * An constructor bases on the AndroidX Toolbar for implementing basic
     * function for the Toolbar.
     *
     * @Param Title For setting the title of a Fragment.
     * @Param NavigationIcon For setting an icon instead of the Hamburger icon.
     */
    public void SetToolbar(String mTitle, ToolbarNavIcon mNavigationIcon) {
        SetToolbar(mTitle, mNavigationIcon, 0);
    }

    /**
     * Expanded constructor for Toolbar.
     * An constructor bases on the AndroidX Toolbar for implementing basic
     * function for the Toolbar.
     *
     * @Param Title For setting the title of a Fragment.
     * @Param NavigationIcon For setting an icon instead of the Hamburger icon.
     * @Param Menu For setting a menu with items and actions for the current Fragment.
     */
    public void SetToolbar(String mTitle, ToolbarNavIcon mNavigationIcon, int mMenu) {
        toolbar = ((MainActivity)requireActivity()).findViewById(R.id.toolbar);
        Objects.requireNonNull(((MainActivity)requireActivity()).getSupportActionBar()).setTitle(mTitle);
        toolbar.setNavigationIcon(mNavigationIcon.getIcon());
        if (mMenu != 0) toolbar.inflateMenu(mMenu); else toolbar.getMenu().clear();

    }

    /**
     * Event listener for OnClick on the Navigation Menu Icon.
     * @param listener The current OnClickListener event.
     */
    public void setNavigationOnClickListener(View.OnClickListener listener){
        toolbar.setNavigationOnClickListener(listener);
    }

    /**
     * Event listener for OnMenuItemClick on the Toolbar.
     * @param listener The current OnMenuItemClickListener.
     */
    public void setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener listener){
        toolbar.setOnMenuItemClickListener(listener);
    }


    /**
     * Constructor for binding an adapter to a RecyclerView.
     * @param recyclerView The recyclerView that is used.
     * @param adapter The adapter that binds the information in a list for recyclerView.
     */
    protected void bindRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter){
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Constructor for binding an adapter to a TabLayout.
     * @param adapter The adapter that is bound.
     * @param tabLayout The TabLayout that is used.
     * @param viewPager The ViewPager that contains the Fragments
     */
    protected void bindTabLayout(PagerAdapter adapter, TabLayout tabLayout, ViewPager viewPager){
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Constructor for binding an adapter to a Spinner.
     * @param spinner The Spinner that is used.
     * @param data The list of items used to populate the Spinner.
     */
    protected void bindSimpleSpinner(Spinner spinner, List<?> data) {
        ArrayAdapter<?> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Constructor for binding an adapter to a Spinner.
     * @param spinner The Spinner that is used.
     * @param data The list of items used to populate the Spinner.
     * @param layout The layout used to display each item.
     */
    protected void bindSimpleSpinner(Spinner spinner, List<?> data, @LayoutRes int layout) {
        ArrayAdapter<?> adapter = new ArrayAdapter<>(requireActivity(), layout, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Navigate to a destination from the current navigation graph. This supports both navigating
     * via an Action and directly navigating to a Destination.
     * @param resId an Action id or a Destination id to navigate to.
     */
    protected void navigate(int resId) {
        Navigation.findNavController(requireActivity(), R.id.nav_host).navigate(resId);
    }

    /**
     * Navigate via the given {@link NavDirections}.
     * @param directions directions that describe this navigation operation.
     */
    protected void navigate(@NonNull NavDirections directions) {
        Navigation.findNavController(requireActivity(), R.id.nav_host).navigate(directions);
    }

    /**
     * Navigate up to the previous fragment.
     */
    protected void navigateUp(){
        Navigation.findNavController(requireActivity(), R.id.nav_host).navigateUp();
    }

    /**
     * Finding a View inside a Fragment.
     * @param id The id of the View.
     * @param <T> The Type of Object the id represents.
     * @return The view or an Exception
     * @see Fragment#requireView()
     * @see View#requireViewById(int)
     */
    @NonNull
    @SuppressWarnings("unchecked")
    protected final <T extends View> T getObject(@IdRes int id) {
        View view = getView();

        if (view == null) {
            Log.d("_Test", "[BaseFragment] getObject() returned: " + "view was null. Mostly because the onCreateView was not called yet.");
        } else {
            if (id != View.NO_ID) {
                T v = view.findViewById(id);

                if (v == null) {
                    throw new IllegalArgumentException("ID does not reference a View inside this View");
                } else {
                    return v;
                }
            }
        }
        return (T) new View(requireContext());
    }

    /**
     * The enum for how the reformatText should return the String value.
     */
    protected enum ReformatType {
        TRIM,
        NON_SPACE
    }

    /**
     * Returns a String of an Editable text
     * @param text The Editable text that has to be checked.
     * @return The trimmed and replaced String.
     */
    protected String reformatText(Editable text) {
        return reformatText(text, ReformatType.TRIM);
    }

    /**
     * Returns a String of an Editable text.
     * @param text The Editable text that has to be checked.
     * @param type The ReformatType that should be returned.
     * @return The trimmed and replaced String.
     */
    protected String reformatText(Editable text, ReformatType type) {
        if (type.equals(ReformatType.NON_SPACE)) {
            return text.toString().trim().replace(" ", "");
        } else {
            return text.toString().trim();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Sets the Unbinder and connects with ButterKnife
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Removes all bound attributes and views.
        if (unbinder != null) unbinder.unbind();
    }

    /**
     * A string set that represents a string array.
     * @param resId The id of a string array list.
     * @return The array as a string list.
     */
    protected String[] getStringArray(@ArrayRes int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * A string list that represents a string array.
     * @param resId The id of a string array list.
     * @return A string list of the ArrayList.
     */
    protected List<String> getStringArrayList(@ArrayRes int resId) {
        return Arrays.asList(getStringArray(resId));
    }

    /**
     * A int set that represents an int array.
     * @param resId The id of a int array list.
     * @return The array as an int list.
     */
    protected int[] getIntegerArray(@ArrayRes int resId) {
        return getResources().getIntArray(resId);
    }

    /**
     * A Integer list that represents an int array.
     * @param resId The id of a int array.
     * @return A Integer list of the int ArrayList.
     */
    protected List<Integer> getIntegerArrayList(@ArrayRes int resId) {
        List<Integer> values = new ArrayList<>();

        for (int i: getIntegerArray(resId)) values.add(i);
        return values;
    }

    public enum Message {
        CREATED,
        UPDATED,
        DELETED
    }

    /**
     * Show snackbar when action has finished.
     * @param resource The string resource that should be displayed.
     */
    protected void showSnackbar(String resource) {
        Snackbar.make(requireView(),resource, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    /**
     * Show snackbar as result when action has finished.
     * @param succeeded Has the action succeeded?
     * @param name The name of what the action was about.
     * @param typeMessage Describe the action.
     */
    protected void showResultSnackbar(boolean succeeded, String name, Message typeMessage) {
        Snackbar.make(requireView(), getString(succeeded ?
                R.string.snackbar_success :
                R.string.snackbar_failed,
                name,
                typeMessage.name().toLowerCase()),
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

    /**
     * Show a toast with a string resource.
     * @param resource The string that should be displayed.
     */
    protected void showToast(String resource) {
        Toast.makeText(requireContext(), resource, Toast.LENGTH_SHORT).show();
    }

    public enum DateFormat {
        DATE,
        TIME,
        DATE_TIME
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
     * @see DateFormat
     */
    protected String toSimpleDateFormat(Date date, DateFormat format) {
        String form = "";
        switch (format) {
            case DATE: form = "dd-MM-yyyy"; break;
            case TIME: form = "HH:mm:ss"; break;
            case DATE_TIME: form = "dd-MM-yyyy HH:mm:ss"; break;
        }
        return new SimpleDateFormat(form, Locale.getDefault()).format(date);
    }

    /**
     * Function for creating default FloatingActionButtons in a FloatingActionMenu.
     * @param menu The FloatingActionMenu that represents this button.
     * @param listener The listener for click events.
     * @param labelText The text in the button's label.
     * @param icon The icon in the button.
     * @param tag The tag for finding the clicked button.
     */
    protected synchronized void addFab(FloatingActionMenu menu, View.OnClickListener listener, String labelText, int icon, Object tag) {
        FloatingActionButton fab = new FloatingActionButton(requireContext());
        fab.setButtonSize(FloatingActionButton.SIZE_MINI);
        fab.setLabelText(labelText);
        fab.setImageResource(icon);
        fab.setTag(tag);

        fab.setOnClickListener(listener);
        menu.addMenuButton(fab);
    }
}
