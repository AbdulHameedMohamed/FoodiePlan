package com.abdulhameed.foodieplan.home.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.abdulhameed.foodieplan.databinding.ItemMealBinding;
import com.abdulhameed.foodieplan.model.Meal;

import com.squareup.picasso.Picasso;

public class MealAdapter extends ListAdapter<Meal, MealAdapter.MealViewHolder> {
    private final OnClickListener<Meal> listener;

    public MealAdapter(OnClickListener<Meal> listener) {
        super(new MealDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMealBinding binding = ItemMealBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MealViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = getItem(position);
        holder.binding.tvName.setText(meal.getName());
        holder.binding.tvCategory.setText(meal.getCategory());
        holder.binding.tvCountry.setText(meal.getCountry());
        Picasso.get().load(meal.getThumb()).into(holder.binding.ivMeal);
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        ItemMealBinding binding;

        public MealViewHolder(@NonNull ItemMealBinding binding, OnClickListener<Meal> listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
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
}