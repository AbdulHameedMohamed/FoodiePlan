package com.abdulhameed.foodieplan.home.plan.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.ItemPlanBinding;
import com.abdulhameed.foodieplan.model.data.PlannedMeal;
import com.squareup.picasso.Picasso;

public class PlanAdapter extends ListAdapter<PlannedMeal, PlanAdapter.PlanViewHolder> {

    private final OnClickListener<PlannedMeal> onClickListener;

    protected PlanAdapter(OnClickListener<PlannedMeal> onClickListener) {
        super(new DiffUtil.ItemCallback<PlannedMeal>() {
            @Override
            public boolean areItemsTheSame(@NonNull PlannedMeal oldItem, @NonNull PlannedMeal newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull PlannedMeal oldItem, @NonNull PlannedMeal newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlanBinding binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlanViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        PlannedMeal plannedMeal = getItem(position);
        holder.bind(plannedMeal, onClickListener);
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlanBinding binding;

        public PlanViewHolder(@NonNull ItemPlanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PlannedMeal plannedMeal, OnClickListener<PlannedMeal> onClickListener) {
            binding.tvDay.setText(plannedMeal.getDay());
            Picasso.get().load(plannedMeal.getThumb())
                    .placeholder(R.drawable.ic_cooking)
                    .into(binding.iMeal.ivMeal);
            binding.iMeal.tvName.setText(plannedMeal.getName());
            binding.iMeal.tvCountry.setText(plannedMeal.getCountry());
            binding.iMeal.tvCategory.setText(plannedMeal.getCategory());
            binding.getRoot().setOnClickListener(view -> onClickListener.onItemClick(plannedMeal));
        }
    }

    public interface OnClickListener<T> {
        void onItemClick(T item);
    }
}