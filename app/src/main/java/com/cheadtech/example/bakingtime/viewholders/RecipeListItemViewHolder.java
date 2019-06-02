package com.cheadtech.example.bakingtime.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cheadtech.example.bakingtime.R;
import com.google.android.material.card.MaterialCardView;

public class RecipeListItemViewHolder extends RecyclerView.ViewHolder {
    public RecipeListItemViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTV = itemView.findViewById(R.id.name_tv);
        servingsTV = itemView.findViewById(R.id.servings_tv);
        thumbnailTV = itemView.findViewById(R.id.thumbnail_iv);
        recipeCard = itemView.findViewById(R.id.recipe_card);
    }

    public MaterialCardView recipeCard;
    public TextView nameTV;
    public TextView servingsTV;
    public ImageView thumbnailTV;
}
