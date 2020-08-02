package com.umbrella.budgetapp.Customs;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.umbrella.budgetapp.Database.Collections.Category;
import com.umbrella.budgetapp.Database.Collections.Store;
import com.umbrella.budgetapp.Extensions.App;
import com.umbrella.budgetapp.Extensions.CloudRecyclerAdapter;
import com.umbrella.budgetapp.R;

import java.util.LinkedList;
import java.util.List;

public class DialogSelectionList {

    /**
     * Class for showing an interactive dialog with all the categories.
     */
    public static class CategoryDialog extends DialogFragment {
        private DaoList adapter;
        private static OnCategoryItemSelected listener;

        public static void getInstance(@NonNull Fragment fragment, OnCategoryItemSelected onCategoryItemSelected) {
            CategoryDialog dialog = new CategoryDialog();
            dialog.setTargetFragment(fragment, 300);
            dialog.show(fragment.getParentFragmentManager(), "VALUE");
            listener = onCategoryItemSelected;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_recycler_view, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Query query = App.getInstance().getUserDocument().collection(Category.COLLECTION);
            FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();

            adapter = new DaoList(options);

            RecyclerView rec = view.requireViewById(R.id.fragmentRecyclerView);

            rec.setHasFixedSize(false);
            rec.setLayoutManager(new LinearLayoutManager(requireActivity()));
            rec.setAdapter(adapter);

            adapter.setOnItemClickListener((which, pos) -> {
                listener.onSelectCategory(which);
                dismiss();
            });
        }

        @Override
        public void onStart() {
            super.onStart();
            adapter.startListening();
        }

        @Override
        public void onStop() {
            super.onStop();
            adapter.stopListening();
        }

        public interface OnCategoryItemSelected {
            void onSelectCategory(DocumentSnapshot snapshot);
        }

        private class DaoList extends CloudRecyclerAdapter<Category, DaoList.ModelHolder> {
            private OnItemClickListener listener;

            private TypedArray arrayIcons;
            private List<Integer> listColors;

            DaoList(@NonNull FirestoreRecyclerOptions<Category> options) {
                super(options);

                listColors = new LinkedList<>();
                for (int i : getResources().getIntArray(R.array.colors)) listColors.add(i);
                arrayIcons = getResources().obtainTypedArray(R.array.icons);
            }

            @NonNull
            @Override
            public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_basic, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Category category) {
                holder.Name.setText(category.getName());
                holder.Img.setImageResource(arrayIcons.getResourceId(category.getIcon(), -1));
                holder.Img.setColorFilter(listColors.get(category.getColor()));

                holder.itemView.setOnClickListener(v -> listener.onItemClick(getSnapshots().getSnapshot(position), position));
            }

            @Override
            public void setOnItemClickListener(OnItemClickListener listener) {
                this.listener = listener;
            }

            class ModelHolder extends RecyclerView.ViewHolder {
                ImageView Img;
                TextView Name;

                ModelHolder(final View itemView) {
                    super(itemView);
                    Img = itemView.findViewById(R.id.list_Basic_Img);
                    Name = itemView.findViewById(R.id.list_Basic_Name);
                }
            }
        }
    }

    /**
     * Class for showing an interactive dialog with all the stores.
     */
    public static class StoreDialog extends DialogFragment {
        private DaoList adapter;
        private static OnStoreItemSelected listener;

        public static void getInstance(@NonNull Fragment fragment, OnStoreItemSelected onStoreItemSelected) {
            StoreDialog dialog = new StoreDialog();
            dialog.setTargetFragment(fragment, 300);
            dialog.show(fragment.getParentFragmentManager(), "VALUE");
            listener = onStoreItemSelected;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_recycler_view, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Query query = FirebaseFirestore.getInstance().collection(Store.COLLECTION).whereEqualTo(Store.USER, App.getInstance().getUser());
            FirestoreRecyclerOptions<Store> options = new FirestoreRecyclerOptions.Builder<Store>().setQuery(query, Store.class).build();

            App.getInstance().getUserDocument().collection(Category.COLLECTION).get().addOnSuccessListener(hasCategories -> {
                adapter = new DaoList(options, hasCategories.toObjects(Category.class));

                RecyclerView rec = view.requireViewById(R.id.fragmentRecyclerView);

                rec.setHasFixedSize(false);
                rec.setLayoutManager(new LinearLayoutManager(requireActivity()));
                rec.setAdapter(adapter);

                adapter.setOnItemClickListener((which, pos) -> {
                    listener.onSelectStore(which);
                    dismiss();
                });

                adapter.startListening();
            });
        }

        @Override
        public void onStart() {
            super.onStart();
            if (adapter != null) adapter.startListening();
        }

        @Override
        public void onStop() {
            super.onStop();
            if (adapter != null) adapter.stopListening();
        }

        public interface OnStoreItemSelected {
            void onSelectStore(DocumentSnapshot snapshot);
        }

        private class DaoList extends CloudRecyclerAdapter<Store, DaoList.ModelHolder> {
            private OnItemClickListener listener;

            private TypedArray arrayIcons;
            private List<Integer> listColors;
            private List<Category> categoryList;

            DaoList(@NonNull FirestoreRecyclerOptions<Store> options, List<Category> categories) {
                super(options);

                listColors = new LinkedList<>();
                for (int i : getResources().getIntArray(R.array.colors)) listColors.add(i);
                arrayIcons = getResources().obtainTypedArray(R.array.icons);
                categoryList = categories;

                Log.d("_Test", "[DaoList] DaoList() category size: " + categories.size());
            }

            @NonNull
            @Override
            public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ModelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_basic, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ModelHolder holder, int position, @NonNull Store store) {
                Log.d("_Test", "[DaoList] onBindViewHolder() store name: " + store.getName());
                holder.Name.setText(store.getName());
                holder.Img.setImageResource(arrayIcons.getResourceId(categoryList.get(position).getIcon(), -1));
                holder.Img.setColorFilter(listColors.get(categoryList.get(position).getColor()));

                holder.itemView.setOnClickListener(v -> listener.onItemClick(getSnapshots().getSnapshot(position), position));
            }

            @Override
            public void setOnItemClickListener(OnItemClickListener listener) {
                this.listener = listener;
            }

            class ModelHolder extends RecyclerView.ViewHolder {
                ImageView Img;
                TextView Name;

                ModelHolder(final View itemView) {
                    super(itemView);
                    Img = itemView.findViewById(R.id.list_Basic_Img);
                    Name = itemView.findViewById(R.id.list_Basic_Name);
                }
            }
        }
    }
}