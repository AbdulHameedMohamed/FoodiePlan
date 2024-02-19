package com.abdulhameed.foodieplan.home.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.ItemFilterBinding;
import com.abdulhameed.foodieplan.model.data.Filter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {
    private List<Filter> filters = new ArrayList<>();
    private final OnClickListener<Filter> listener;
    public FilterAdapter(OnClickListener<Filter> listener) {
        this.listener = listener;
    }
    public void setList(List<Filter> filters) {
        this.filters = filters;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilterBinding binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FilterViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        String ingredient = filters.get(position).getName();

        holder.binding.tvName.setText(ingredient);

        Picasso.get().load(filters.get(position).getThumb())
                .placeholder(R.drawable.action_hero)
                .into(holder.binding.ivImage);
        holder.itemView.setOnClickListener(view -> listener.onItemClick(filters.get(position)));
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    public static class FilterViewHolder extends RecyclerView.ViewHolder {
        ItemFilterBinding binding;

        public FilterViewHolder(@NonNull ItemFilterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}