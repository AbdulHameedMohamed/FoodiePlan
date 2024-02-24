package com.abdulhameed.foodieplan.home.home.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.ItemMealBinding;
import com.abdulhameed.foodieplan.model.Meal;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends ListAdapter<Meal, MealAdapter.MealViewHolder> {
    private final OnMealClickListener<Meal> listener;

    public MealAdapter(OnMealClickListener<Meal> listener) {
        super(new MealDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return getMealViewHolder(parent);
    }

    @NonNull
    private MealViewHolder getMealViewHolder(@NonNull ViewGroup parent) {
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

    public Meal getMeal(int position) {
        return getItem(position);
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        ItemMealBinding binding;

        public MealViewHolder(@NonNull ItemMealBinding binding, OnMealClickListener<Meal> listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> listener.onItemClick(getItem(getAdapterPosition())));
            binding.ivSaveMeal.setOnClickListener(view -> listener.onFavouriteClick(getItem(getAdapterPosition())));
            binding.ivCalenderMeal.setOnClickListener(view -> listener.onPlanClick(getItem(getAdapterPosition())));
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
    }
}