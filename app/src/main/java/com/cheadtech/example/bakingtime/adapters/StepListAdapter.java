package com.cheadtech.example.bakingtime.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.models.Step;
import com.cheadtech.example.bakingtime.viewholders.StepListItemViewHolder;

import java.util.ArrayList;

public class StepListAdapter extends RecyclerView.Adapter<StepListItemViewHolder> {
    public StepListAdapter(ArrayList<Step> steps, StepListAdapterCallback callback) {
        setData(steps);
        this.callback = callback;
    }

    private ArrayList<Step> dataSet = new ArrayList<>();
    private void setData(ArrayList<Step> steps) {
        dataSet.clear();
        dataSet.addAll(steps);
        notifyDataSetChanged();
    }

    public interface StepListAdapterCallback {
        void onStepClicked(Step step);
    }
    private StepListAdapterCallback callback;

    @NonNull
    @Override
    public StepListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new StepListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final StepListItemViewHolder holder, int position) {
        if (holder.stepCard != null && holder.stepDescriptionTV != null) {
            final Step step = dataSet.get(holder.getAdapterPosition());
            holder.stepDescriptionTV.setText(step.shortDescription);
            holder.stepCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { callback.onStepClicked(step); }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
