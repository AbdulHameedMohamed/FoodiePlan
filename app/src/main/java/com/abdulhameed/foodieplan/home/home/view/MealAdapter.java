package com.abdulhameed.foodieplan.home.home.view;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.ItemMealBinding;
import com.abdulhameed.foodieplan.model.data.Meal;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends ListAdapter<Meal, MealAdapter.MealViewHolder> implements ActionMode.Callback {
    private final OnMealClickListener<Meal> listener;

    private final FragmentActivity requireActivity;
    private boolean multiSelection = false;
    private ActionMode mActionMode;
    private final ArrayList<Meal> selectedRecipes = new ArrayList<>();
    private final ArrayList<MealViewHolder> myViewHolders = new ArrayList<>();
    private View rootView;

    public MealAdapter(OnMealClickListener<Meal> listener, FragmentActivity requireActivity) {
        super(new MealDiffCallback());
        this.listener = listener;
        this.requireActivity = requireActivity;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMealBinding binding = ItemMealBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MealViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        holder.bindViews(holder, position);
    }

    public void removeMeal(Meal meal) {
        List<Meal> newList = new ArrayList<>(getCurrentList());
        newList.remove(meal);
        submitList(newList);
    }

    public void insertMeal(Meal meal) {
        List<Meal> newList = new ArrayList<>(getCurrentList());
        newList.add(meal);
        submitList(newList);
    }

    public void insertMeals(ArrayList<Meal> selectedMeals) {
        List<Meal> newList = new ArrayList<>(getCurrentList());
        newList.addAll(selectedMeals);
        submitList(newList);
    }

    public void removeMeals(ArrayList<Meal> selectedMeals) {
        List<Meal> newList = new ArrayList<>(getCurrentList());
        newList.removeAll(selectedMeals);
        submitList(newList);
    }

    public Meal getMeal(int position) {
        return getItem(position);
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        selectedRecipes.clear();
        mActionMode = actionMode;
        mActionMode.getMenuInflater().inflate(R.menu.cm_favourite, menu);
        applyStatusBarColor(R.color.contextualStatusBarColor);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.i_delete) {
            listener.onMealsSelected(selectedRecipes);

            multiSelection = false;
            actionMode.finish();
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        for (MealViewHolder holder : myViewHolders) {
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor);
        }
        multiSelection = false;
        applyStatusBarColor(R.color.statusBarColor);
    }

    private void changeRecipeStyle(MealViewHolder holder, int backgroundColor, int strokeColor) {
        holder.itemView.setBackgroundColor(
                ContextCompat.getColor(requireActivity, backgroundColor)
        );
        holder.binding.getRoot().setStrokeColor(
                ContextCompat.getColor(requireActivity, strokeColor)
        );
    }

    private void applyStatusBarColor(int color) {
        requireActivity.getWindow().setStatusBarColor(
                ContextCompat.getColor(requireActivity, color)
        );
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        ItemMealBinding binding;

        public MealViewHolder(@NonNull ItemMealBinding binding, OnMealClickListener<Meal> listener) {
            super(binding.getRoot());
            this.binding = binding;
            myViewHolders.add(this);
            rootView = binding.getRoot();
            binding.getRoot().setOnClickListener(v -> {
                if (multiSelection) {
                    applySelection(getItem(getAdapterPosition()));
                } else {
                    listener.onItemClick(getItem(getAdapterPosition()));
                }
            });

            binding.getRoot().setOnLongClickListener(v -> {
                if (!multiSelection) {
                    multiSelection = true;
                    requireActivity.startActionMode(MealAdapter.this);
                }
                applySelection(getMeal(getAdapterPosition()));
                return true;
            });
            binding.ivSaveMeal.setOnClickListener(view -> listener.onFavouriteClick(getItem(getAdapterPosition())));
            binding.ivCalenderMeal.setOnClickListener(view -> listener.onPlanClick(getItem(getAdapterPosition())));
        }

        private void applySelection(Meal currentRecipe) {
            if (selectedRecipes.contains(currentRecipe)) {
                selectedRecipes.remove(currentRecipe);
                changeRecipeStyle(this, R.color.cardBackgroundColor, R.color.strokeColor);
                applyActionModeTitle();
            } else {
                selectedRecipes.add(currentRecipe);
                changeRecipeStyle(this, R.color.cardBackgroundLightColor, R.color.colorPrimary);
                applyActionModeTitle();
            }
        }

        private void applyActionModeTitle() {
            switch (selectedRecipes.size()) {
                case 0:
                    mActionMode.finish();
                    multiSelection = false;
                    break;
                case 1:
                    mActionMode.setTitle(selectedRecipes.size() + " item selected");
                    break;
                default:
                    mActionMode.setTitle(selectedRecipes.size() + " items selected");
                    break;
            }
        }

        private void bindViews(@NonNull MealViewHolder holder, int position) {
            Meal meal = getItem(position);
            holder.binding.tvName.setText(meal.getName());
            holder.binding.tvCategory.setText(meal.getCategory());
            holder.binding.tvCountry.setText(meal.getCountry());
            Picasso.get().load(meal.getThumb()).placeholder(R.drawable.p_chief).into(holder.binding.ivMeal);
        }
    }

    static class MealDiffCallback extends DiffUtil.ItemCallback<Meal> {
        @Override
        public boolean areItemsTheSame(@NonNull Meal oldItem, @NonNull Meal newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Meal oldItem, @NonNull Meal newItem) {
            return oldItem.equals(newItem);
        }
    }

    public interface OnMealClickListener<T> {
        void onItemClick(T meal);

        void onFavouriteClick(T meal);

        void onPlanClick(T meal);

        void onMealsSelected(ArrayList<T> selectedMeals);
    }
}