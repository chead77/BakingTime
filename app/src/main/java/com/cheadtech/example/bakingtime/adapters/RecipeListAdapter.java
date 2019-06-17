package com.cheadtech.example.bakingtime.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.database.Recipes;
import com.cheadtech.example.bakingtime.viewholders.RecipeListItemViewHolder;

import java.util.ArrayList;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListItemViewHolder> {
    private ArrayList<Recipes> dataSet = new ArrayList<>();
    public void setData(ArrayList<Recipes> recipes) {
        dataSet.clear();
        dataSet.addAll(recipes);
        notifyDataSetChanged();
    }

    public RecipeListAdapter(ArrayList<Recipes> recipes, RecipeListAdapterCallback callback) {
        dataSet.clear();
        dataSet.addAll(recipes);
        this.callback = callback;
    }

    public interface RecipeListAdapterCallback {
        void onRecipeClicked(Integer recipeId);
    }
    private RecipeListAdapterCallback callback;

    @NonNull
    @Override
    public RecipeListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new RecipeListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecipeListItemViewHolder holder, int position) {
        if (holder.recipeCard != null && holder.nameTV != null && holder.servingsTV != null && holder.imageIV != null) {
            final Recipes recipe = dataSet.get(holder.getAdapterPosition());
            holder.nameTV.setText(recipe.name);
            holder.servingsTV.setText(holder.servingsTV.getContext().getString(R.string.servings, recipe.servings));
            Glide.with(holder.imageIV.getContext())
                    .load(recipe.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_android_black_80dp)
                    .error(R.drawable.ic_android_black_80dp)
                    .into(holder.imageIV);
            holder.recipeCard.setOnClickListener(view -> callback.onRecipeClicked(recipe.id));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
