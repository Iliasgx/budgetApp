package com.umbrella.budgetapp.Customs;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.umbrella.budgetapp.Database.Collections.Account;
import com.umbrella.budgetapp.Database.Collections.Currency;
import com.umbrella.budgetapp.Extensions.App;
import com.umbrella.budgetapp.R;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Spinners {

    /**
     * Private binder for the Spinner widget.
     * @param spinner The spinner that is used for the collection.
     * @param adapter The adapter with the collection of items.
     */
    private static void bindSpinner(Spinner spinner, ArrayAdapter<?> adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Public DocumentReference of the selected item. Mostly used when saving changes in a Fragment.
     * @param spinner The spinner that has to be checked.
     * @return The DocumentReference of the selected item.
     */
    public static DocumentReference getReference(Spinner spinner) {
        return ((QueryDocumentSnapshot)spinner.getSelectedItem()).getReference();
    }

    /**
     * Class for the color spinner.
     */
    public static class Colors {

        public enum Size {
            SMALL,
            LARGE
        }

        public Colors(@NonNull Fragment fragment, @NonNull Spinner spinner, @Nullable Size size) {
            List<Integer> currentList = new LinkedList<>();
            for (int i: fragment.getResources().getIntArray(R.array.colors)) currentList.add(i);

            bindSpinner(spinner, new ColorAdapter(fragment, currentList, (size != null ? size : Size.SMALL)));
        }

        private static class ColorAdapter extends ArrayAdapter<Integer> {
            private Fragment fragment;
            private List<Integer> items;
            private Size size;

            ColorAdapter(Fragment fragment, List<Integer> objects, Size size) {
                super(fragment.requireContext(), R.layout.custom_spinner_colors, 0, objects);
                this.fragment = fragment;
                this.items = objects;
                this.size = size;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, parent);
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, parent);
            }

            private View createItemView(int position, ViewGroup parent) {
                final View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.custom_spinner_colors, parent, false);

                View item = view.requireViewById(R.id.spinner_color);
                item.setLayoutParams(new LinearLayout.LayoutParams((size == Size.LARGE ? fragment.getResources().getDimensionPixelSize(R.dimen.spinner_colors_width_large) : fragment.getResources().getDimensionPixelSize(R.dimen.spinner_colors_width_small)), fragment.getResources().getDimensionPixelSize(R.dimen.spinners_colors_height)));
                item.setForeground(fragment.getResources().getDrawable(R.drawable._spinner_color_corners, getDropDownViewTheme()));
                item.setBackgroundColor(items.get(position));
                return view;
            }
        }
    }

    /**
     * Class for the icon spinner.
     */
    public static class Icons {
        private  static Integer[] size;

        public Icons(@NonNull Fragment fragment, @NonNull Spinner spinner) {
           size = new Integer[fragment.getResources().getIntArray(R.array.icons).length];
           bindSpinner(spinner, new IconAdapter(fragment));
        }

        private static class IconAdapter extends ArrayAdapter<Integer> {
            private Fragment fragment;
            private TypedArray array;

            IconAdapter(Fragment fragment) {
                super(fragment.requireContext(), R.layout.custom_spinner_icons, size);
                this.fragment = fragment;
                this.array = fragment.getResources().obtainTypedArray(R.array.icons);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, parent);
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, parent);
            }

            private View createItemView(int position, ViewGroup parent) {
                final View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.custom_spinner_icons, parent, false);

                ((ImageView)view.requireViewById(R.id.spinner_icon)).setImageResource(array.getResourceId(position,-1));
                return view;
            }
        }
    }

    /**
     * Class for the account spinner.
     */
    public static class Accounts {
        public static List<QueryDocumentSnapshot> list = new LinkedList<>();

        public Accounts(@NonNull Fragment fragment, @NonNull Spinner spinner) {
            App.getInstance().getUserDocument().collection(Account.COLLECTION).orderBy(Account.POSITION, Query.Direction.ASCENDING).get().addOnSuccessListener(hasAccounts -> {
                List<QueryDocumentSnapshot> snapList = new LinkedList<>();
                hasAccounts.forEach(snapList::add);

                list = snapList;
                bindSpinner(spinner, new AccountsAdapter(fragment, snapList));
            });
        }

        public int getPosition(DocumentReference id) {
            return list.stream().filter(item -> item.getReference().equals(id))
                    .collect(Collectors.toList()).get(0).toObject(Currency.class)
                    .getPosition();
        }

        public static void initializeAndSetDefault(Fragment fragment, Spinner spinner, DocumentReference id) {
            App.getInstance().getUserDocument().collection(Account.COLLECTION).orderBy(Account.POSITION, Query.Direction.ASCENDING).get().addOnSuccessListener(hasAccounts -> {
                List<QueryDocumentSnapshot> snapList = new LinkedList<>();
                hasAccounts.forEach(snapList::add);

                list = snapList;
                bindSpinner(spinner, new AccountsAdapter(fragment, snapList));

                spinner.setSelection(list.stream().filter(item -> item.getReference().equals(id))
                        .collect(Collectors.toList()).get(0).toObject(Currency.class)
                        .getPosition());
            });
        }

        private static class AccountsAdapter extends ArrayAdapter<QueryDocumentSnapshot> {
            private Fragment fragment;
            private List<QueryDocumentSnapshot> snaps;

            AccountsAdapter(Fragment fragment, List<QueryDocumentSnapshot> accounts) {
                super(fragment.requireContext(), R.layout.custom_spinner_items, accounts);
                this.fragment = fragment;
                this.snaps = accounts;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, parent);
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, parent);
            }

            private View createItemView(int position, ViewGroup parent) {
                final View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.custom_spinner_items, parent, false);

                ((TextView)view.requireViewById(R.id.custom_spinner_item)).setText(snaps.get(position).toObject(Account.class).getName());
                return view;
            }
        }
    }

    /**
     * Class for the account spinner.
     */
    public static class Currencies {
        public static List<QueryDocumentSnapshot> list = new LinkedList<>();

        public Currencies(@NonNull Fragment fragment, @NonNull Spinner spinner) {
            App.getInstance().getUserDocument().collection(Currency.COLLECTION).orderBy(Currency.POSITION, Query.Direction.ASCENDING).get().addOnSuccessListener(hasCurrencies -> {
                List<QueryDocumentSnapshot> snapList = new LinkedList<>();
                hasCurrencies.forEach(snapList::add);

                list = snapList;
                bindSpinner(spinner, new CurrenciesAdapter(fragment, snapList));
            });
        }

        public int getPosition(DocumentReference id) {
            return list.stream().filter(item -> item.getReference().equals(id))
                    .collect(Collectors.toList()).get(0).toObject(Currency.class)
                    .getPosition();
        }

        public static void initializeAndSetDefault(Fragment fragment, Spinner spinner, DocumentReference id) {
            App.getInstance().getUserDocument().collection(Currency.COLLECTION).orderBy(Currency.POSITION, Query.Direction.ASCENDING).get().addOnSuccessListener(hasCurrencies -> {
                List<QueryDocumentSnapshot> snapList = new LinkedList<>();
                hasCurrencies.forEach(snapList::add);

                list = snapList;
                bindSpinner(spinner, new CurrenciesAdapter(fragment, snapList));

                spinner.setSelection(list.stream().filter(item -> item.getReference().equals(id))
                        .collect(Collectors.toList()).get(0).toObject(Currency.class)
                        .getPosition());
            });
        }

        private static class CurrenciesAdapter extends ArrayAdapter<QueryDocumentSnapshot> {
            private Fragment fragment;
            private List<QueryDocumentSnapshot> snaps;

            CurrenciesAdapter(Fragment fragment, List<QueryDocumentSnapshot> currencies) {
                super(fragment.requireContext(), R.layout.custom_spinner_items, currencies);
                this.fragment = fragment;
                this.snaps = currencies;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, parent);
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, parent);
            }

            private View createItemView(int position, ViewGroup parent) {
                final View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.custom_spinner_items, parent, false);

                ((TextView)view.requireViewById(R.id.custom_spinner_item)).setText(snaps.get(position).toObject(Currency.class).getCountryID());
                return view;
            }

        }
    }
}
