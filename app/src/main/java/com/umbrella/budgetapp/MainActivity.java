package com.umbrella.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Country;
import com.umbrella.budgetapp.Database.Collections.Currency;
import com.umbrella.budgetapp.Database.Collections.Debt;
import com.umbrella.budgetapp.Database.Collections.Goal;
import com.umbrella.budgetapp.Database.Collections.PlannedPayment;
import com.umbrella.budgetapp.Database.Collections.Record;
import com.umbrella.budgetapp.Database.Collections.ShoppingItem;
import com.umbrella.budgetapp.Database.Collections.ShoppingList;
import com.umbrella.budgetapp.Database.Collections.ShoppingListItem;
import com.umbrella.budgetapp.Database.Collections.Store;
import com.umbrella.budgetapp.Database.Collections.Template;
import com.umbrella.budgetapp.Database.Collections.User;
import com.umbrella.budgetapp.Database.Defaults.DefaultCategories;
import com.umbrella.budgetapp.Database.Defaults.DefaultCountries;
import com.umbrella.budgetapp.Extensions.App;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int ID = 1 << 4;

    private Unbinder unbinder;
    private App app = App.getInstance();

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private boolean newUser = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
   /*
        //TODO: Voorlopig
        setTheme(app.getUser() == null ? R.style.AppTheme_Launcher : R.style.AppTheme_NoActionBar);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            // TODO: set dark mode
        } else setTheme(R.style.AppTheme_NoActionBar);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout._activity);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        app.setContext(this);
        app.setUser("T5k5kjkudCR1hVhJjmCPeryo5Ai1");

        Log.d("_Test", "[MainActivity] onCreate() current user is: " + app.getUser());

        //Create SignInOptions by the ClientID.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        updateUI();

        List<DocumentReference> ref = new LinkedList<>();
        ref.add(app.getUserDocument().collection(Account.COLLECTION).document("0tpXFyuFV234ucuK42vi"));
        app.setSelectedAccounts(ref);
        app.setLastUsedCategory(app.getUserDocument().collection(Category.COLLECTION).document("smMt44di4f1iuiBU1r7b"));
        app.setLastUsedStore(FirebaseFirestore.getInstance().document("/store/TJ69U9V67Ankqnjbz6Up"));
        app.setHomeCashFlowFilter(7);
        app.setHomeRecordsFilter(7);
        app.setRecordsFilter("0 0 0 0 0 0 0 0 0");
        app.setPlannedPaymentsSorting(0);
        app.setStatisticsFilter("0 0 0 0 0 0 0 0 0");

        app.setDataPreferences(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        Log.d("_Test", "[MainActivity] onDestroy()");
    }

    /**
     * Sign the user in.
     */
    public void signIn() {
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, ID);
        Log.d("_Test", "[MainActivity] signIn()");
    }

    /**
     * Sign the user out.
     */
    public void signOut() {
        onReset();
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnSuccessListener(task -> updateUI());
        Log.d("_Test", "[MainActivity] signOut()");
    }

    /**
     * Revoke the user access.
     */
    public void revokeAccess() {
        onReset();
        mAuth.signOut();
        mGoogleSignInClient.revokeAccess().addOnSuccessListener(task -> updateUI());
        Log.d("_Test", "[MainActivity] revokeAccess() ");
    }

    /**
     * Reset login privileges.
     */
    private void onReset() {
        toolbar.setVisibility(View.GONE);
        setSupportActionBar(null);
        app.setUser(null);
        Log.d("_Test", "[MainActivity] onReset() ");
    }

    /**
     * Get the result for the user login.
     * @param requestCode The requested code.
     * @param resultCode The code that has been returned.
     * @param data The data containing the login.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ID) {
            GoogleSignInAccount account = null;
            try {
                account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            } catch (ApiException e) {
                Log.d("_Test", "[MainActivity] onActivityResult(): " + e.getCause());
                updateUI();
            } finally {
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            newUser = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getAdditionalUserInfo()).isNewUser();
                            updateUI();
                            Log.d("_Test", "[MainActivity] onActivityResult() is new user? = " + newUser);
                        } else {
                            Log.d("_Test", "[MainActivity] onActivityResult(): " + task.getException());
                            updateUI();
                        }
                    });
                }
            }
        }
    }

    /**
     * Checks every aspect of the user login.
     *
     * Synchronized method that handles the authentication, internet connection and possible cache. The result will navigate
     * to the correct methods for further background work.
     */
    private synchronized void updateUI() {
        /*if (mAuth.getCurrentUser() != null) { //Logged in
            app.setUser(mAuth.getCurrentUser().getUid());
            if (newUser) {
                addNewUser();
            } else {
               app.getUserDocument().collection(Account.COLLECTION).whereEqualTo(Account.POSITION, 0).limit(1).get().addOnSuccessListener(data -> {
                   if (data.getDocuments().get(0).getReference().getPath().contains(mAuth.getCurrentUser().getUid())) {
                       if (data.isEmpty()) { // Retrieved nothing
                           if (((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)).getActiveNetwork() == null) { // No internet
                               if (getPreferences(MODE_PRIVATE).getAll().isEmpty()) { // No cache
                                   showResult("No cache found. Please connect to the internet.");
                                   return;
                               } else {
                                   showResult("No connection. Can't synchronize records.");
                               }
                           }
                           createFistAccountAssistTool();
                       } else {
                           init();
                       }
                   } else { // Cache is not from current logged in user. Delete all other cache to preserve space on the user phone.
                       deleteCache(getApplicationContext().getCacheDir());
                       updateUI();
                   }
               });
            }
        } else Navigation.findNavController(this, R.id.nav_host).navigate(HomeDirections.homeToLogin());*/
        init();
    }

    /**
     * Create a new user in the app.
     */
    private void addNewUser() {
        final String defaultCurrency = "EUR";
        final Category defaultCategory = new DefaultCategories().getDefaultCategories().get(0);

        FirebaseUser fbUser = Objects.requireNonNull(mAuth.getCurrentUser());
        app.getUserDocument().set(
                new User(
                        fbUser.getDisplayName(),
                        fbUser.getEmail(),
                        Timestamp.now(),
                        0,
                        null,
                        null))
                .addOnSuccessListener(user -> {
                    DocumentReference me = app.getUserDocument();

                    Country country = new DefaultCountries().getDefaultCountries(this).stream()
                            .filter(item -> item.getCode().equals(defaultCurrency)).collect(Collectors.toList()).get(0);
                    me.collection(Currency.COLLECTION).add(new Currency(country.getCode(), country.getDefaultRate(), 0));

                    new DefaultCategories().getDefaultCategories().forEach(category ->
                            me.collection(Category.COLLECTION).add(category).addOnSuccessListener(success -> {
                                if (category.equals(defaultCategory)) me.update(User.LAST_CATEGORY, success);
                            }));

                    updateUI();
                });

        // TODO: set app data here too
    }

    /**
     * Create a assist tool to show the user how to define their first account.
     */
    private void createFistAccountAssistTool() {
        // TODO: create the assist tool
    }

    /**
     * Initialize the {@link App} data from app preferences.
     */
    private void init() {
        toolbar.setVisibility(View.VISIBLE);
        app.getUserDocument().get().addOnSuccessListener(data -> app.initData(this, data.toObject(User.class)));
        navigateUp();
    }

    /**
     * Show a new toast with a message.
     * @param message The displayed message.
     */
    private void showResult(CharSequence message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Delete all cache of the app.
     * @param dir The application cash file name.
     * @return If cache could be fully erased.
     */
    private boolean deleteCache(File dir) {
        if (getPreferences(MODE_PRIVATE).getAll().isEmpty()) {
            getPreferences(MODE_PRIVATE).edit().clear().apply();
        }

        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteCache(new File(dir, child));
                if (!success) return false;
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        /*
         * Open drawer when back is pressed.
         * If drawer is already open, this function will close it again.
         */
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.d("_Test", "[MainActivity] onBackPressed() drawer was open");
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                drawer.openDrawer(GravityCompat.START);
                Log.d("_Test", "[MainActivity] onBackPressed() drawer was closed and no stackTrace so open");
            } else {
                navigateUp();
                Log.d("_Test", "[MainActivity] onBackPressed() drawer was closed and go back");
            }
        }
    }

    public void navigate(int resId) {
        Navigation.findNavController(this, R.id.nav_host).navigate(resId);
    }

    public void navigateUp() {
        Navigation.findNavController(this, R.id.nav_host).navigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //All drawer menu items that can be clicked.
        switch (menuItem.getItemId()) {
            case R.id.menu_group1_item1:
                navigate(R.id.globalHome);
                break;
            case R.id.menu_group1_item2:
                navigate(R.id.homeToRecords);
                break;
            case R.id.menu_group1_item3:
                navigate(R.id.globalStatistics);
                break;
            case R.id.menu_group2_item1:
                navigate(R.id.globalPlannedPayments);
                break;
            case R.id.menu_group2_item2:
                navigate(R.id.globalDebts);
                break;
            case R.id.menu_group2_item3:
                navigate(R.id.globalGoals);
                break;
            case R.id.menu_group3_item1:
                navigate(R.id.globalShoppingLists);
                break;
            case R.id.menu_group3_item2:
                navigate(R.id.globalStores);
                break;
            case R.id.menu_group4_item1:
                navigate(R.id.globalSettings);
                break;
            case R.id.menu_group4_item2:
                navigate(R.id.globalImports);
                break;
        }
        
        Log.d("_Test", "[MainActivity] onNavigationItemSelected() ");

        //Close drawer after clicked on item.
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}