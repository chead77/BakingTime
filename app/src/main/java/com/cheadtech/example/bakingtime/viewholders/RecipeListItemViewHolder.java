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
        ingredientsTV = itemView.findViewById(R.id.ingredients_tv);
        imageIV = itemView.findViewById(R.id.recipe_image_iv);
        recipeCard = itemView.findViewById(R.id.recipe_widget_item);
    }

    public final MaterialCardView recipeCard;
    public final TextView nameTV;
    public final TextView ingredientsTV;
    public final ImageView imageIV;
}
