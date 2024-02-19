package com.abdulhameed.foodieplan.home.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.model.data.Step;

import java.util.ArrayList;
import java.util.List;

public class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.StepViewHolder> {
    private List<Step> stepList = new ArrayList<>();

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        Step step = stepList.get(position);
        holder.stepNumberTextView.setText("Step " + step.getNumber());
        holder.stepDescriptionTextView.setText(step.getDescription());
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public void setList(List<Step> steps) {
        this.stepList= steps;
        notifyDataSetChanged();
    }
    static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView stepNumberTextView;
        TextView stepDescriptionTextView;

        StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumberTextView = itemView.findViewById(R.id.stepNumberTextView);
            stepDescriptionTextView = itemView.findViewById(R.id.stepDescriptionTextView);
        }
    }
}