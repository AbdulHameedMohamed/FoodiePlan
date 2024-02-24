package com.abdulhameed.foodieplan.home.filter.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.ItemFilterMealsBinding;
import com.abdulhameed.foodieplan.utils.OnClickListener;
import com.abdulhameed.foodieplan.model.data.FilterMeal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FilterMealsAdapter extends RecyclerView.Adapter<FilterMealsAdapter.FilterViewHolder> {

    private List<FilterMeal> meals = new ArrayList<>();
    private final OnClickListener<FilterMeal> listener;

    public FilterMealsAdapter(OnClickListener<FilterMeal> listener) {
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
            Picasso.get().load(filterMeal.getThumb()).placeholder(R.drawable.p_chief).into(binding.ivFilterImage);
        }
    }
}