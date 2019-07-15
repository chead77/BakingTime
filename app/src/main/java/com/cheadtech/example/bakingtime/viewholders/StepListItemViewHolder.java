package com.cheadtech.example.bakingtime.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cheadtech.example.bakingtime.R;
import com.google.android.material.card.MaterialCardView;

public class StepListItemViewHolder extends RecyclerView.ViewHolder {
    public StepListItemViewHolder(@NonNull View itemView) {
        super(itemView);
        stepCard = itemView.findViewById(R.id.step_card);
        stepDescriptionTV = itemView.findViewById(R.id.step_description);
    }

    public final MaterialCardView stepCard;
    public final TextView stepDescriptionTV;
}
