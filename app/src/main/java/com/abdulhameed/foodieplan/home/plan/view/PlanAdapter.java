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

import java.util.ArrayList;
import java.util.List;

public class PlanAdapter extends ListAdapter<PlannedMeal, PlanAdapter.PlanViewHolder> {

    private final OnPlannedClickListener<PlannedMeal> listener;

    public PlanAdapter(OnPlannedClickListener<PlannedMeal> listener) {
        super(new PlannedDiffCallback());
        this.listener = listener;
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
        holder.bind(plannedMeal);
    }

    public void insertMeal(PlannedMeal meal) {
        List<PlannedMeal> newList = new ArrayList<>(getCurrentList());
        newList.add(meal);
        submitList(newList);
    }

    public void deleteMeal(PlannedMeal meal) {
        List<PlannedMeal> newList = new ArrayList<>(getCurrentList());
        newList.remove(meal);
        submitList(newList);
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlanBinding binding;

        public PlanViewHolder(@NonNull ItemPlanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            addListeners(binding);
        }

        private void addListeners(@NonNull ItemPlanBinding binding) {
            binding.getRoot().setOnClickListener(v -> listener.onItemClick(getItem(getAdapterPosition())));
            binding.ivSaveMeal.setOnClickListener(view -> listener.onFavouriteClick(getItem(getAdapterPosition())));
            binding.ivCalenderMeal.setOnClickListener(view -> listener.onPlanClick(getItem(getAdapterPosition())));
            binding.ivRemove.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                    listener.onDeleteClick(getItem(getAdapterPosition()));
            });
        }

        public void bind(PlannedMeal plannedMeal) {
            binding.tvDay.setText(plannedMeal.getDay());
            Picasso.get().load(plannedMeal.getThumb())
                    .placeholder(R.drawable.p_chief)
                    .into(binding.ivMeal);
            binding.tvName.setText(plannedMeal.getName());
            binding.tvCountry.setText(plannedMeal.getCountry());
            binding.tvCategory.setText(plannedMeal.getCategory());
        }
    }

    static class PlannedDiffCallback extends DiffUtil.ItemCallback<PlannedMeal> {
        @Override
        public boolean areItemsTheSame(@NonNull PlannedMeal oldItem, @NonNull PlannedMeal newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PlannedMeal oldItem, @NonNull PlannedMeal newItem) {
            return oldItem.equals(newItem);
        }
    }

    public interface OnPlannedClickListener<T> {
        void onItemClick(T meal);
        void onFavouriteClick(T meal);
        void onPlanClick(T meal);
        void onDeleteClick(T meal);
    }
}