package com.abdulhameed.foodieplan.home.filter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulhameed.foodieplan.databinding.ItemFilterMealsBinding;
import com.abdulhameed.foodieplan.home.home.OnClickListener;
import com.abdulhameed.foodieplan.model.data.FilterMeal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private List<FilterMeal> meals = new ArrayList<>();
    private final OnClickListener listener;

    public FilterAdapter(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilterMealsBinding binding = ItemFilterMealsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FilterViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        FilterMeal recipe = meals.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void setList(List<FilterMeal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {
        ItemFilterMealsBinding binding;
        public FilterViewHolder(@NonNull ItemFilterMealsBinding binding, final OnClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(meals.get(position));
                }
            });
        }

        public void bind(FilterMeal filterMeal) {
            binding.tvFilterName.setText(filterMeal.getName());
            Picasso.get().load(filterMeal.getThumb()).into(binding.ivFilterImage);
        }
    }
}