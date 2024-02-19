package com.abdulhameed.foodieplan.home.details;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.ItemIngredientMeasureBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngrdientsViewHolder> {
    private List<Pair<String, String>> list = new ArrayList<>();
    @NonNull
    @Override
    public IngrdientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIngredientMeasureBinding binding = ItemIngredientMeasureBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IngrdientsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngrdientsViewHolder holder, int position) {
        Pair<String, String> item = list.get(position);
        Log.d(TAG, "https://www.themealdb.com/images/ingredients/" + item.first + ".png");
        String imageUrl = "https://www.themealdb.com/images/ingredients/" + item.first + ".png";

        holder.binding.tvIngredientName.setText(item.first);
        holder.binding.measureName.setText(item.second);
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.action_hero)
                .into(holder.binding.ivIngredientImg);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static final String TAG = "IngredientAdapter";
    public void setList(List<Pair<String, String>> ingredients) {
        this.list = ingredients;
        Log.d(TAG, "setList: "+ ingredients.get(0).first);
        notifyDataSetChanged();
    }

    public static class IngrdientsViewHolder extends RecyclerView.ViewHolder {
        private final ItemIngredientMeasureBinding binding;
        public IngrdientsViewHolder(@NonNull ItemIngredientMeasureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}