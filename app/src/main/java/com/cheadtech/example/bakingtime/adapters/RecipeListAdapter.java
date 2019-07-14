package com.cheadtech.example.bakingtime.adapters;

import android.annotation.SuppressLint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cheadtech.example.bakingtime.R;
import com.cheadtech.example.bakingtime.models.Ingredient;
import com.cheadtech.example.bakingtime.models.Recipe;
import com.cheadtech.example.bakingtime.viewholders.RecipeListItemViewHolder;

import java.util.ArrayList;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListItemViewHolder> {
    private final ArrayList<Recipe> dataSet = new ArrayList<>();

    public void setData(ArrayList<Recipe> recipes) {
        dataSet.clear();
        if (recipes != null) dataSet.addAll(recipes);
        notifyDataSetChanged();
    }

    public RecipeListAdapter(ArrayList<Recipe> recipes, RecipeListAdapterCallback callback) {
        dataSet.clear();
        if (recipes != null) dataSet.addAll(recipes);
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
        if (holder.recipeCard != null && holder.nameTV != null && holder.ingredientsTV != null && holder.imageIV != null) {
            final Recipe recipe = dataSet.get(holder.getAdapterPosition());

            holder.nameTV.setText(recipe.name);

            ArrayList<Ingredient> ingredients = dataSet.get(holder.getAdapterPosition()).ingredients;
            SpannableStringBuilder ingredientsBuilder = new SpannableStringBuilder();
            for (Ingredient ingredient : ingredients) {
                SpannableStringBuilder builder = new SpannableStringBuilder(trimTrailingZeroes(ingredient.quantity.toString()));
                if (!ingredient.measure.equals(holder.ingredientsTV.getContext().getString(R.string.measure_unit)))
                        builder.append(" ").append(ingredient.measure);
                builder.append(" ").append(ingredient.ingredient).append("\n");
                builder.setSpan(new BulletSpan(12, holder.ingredientsTV.getCurrentTextColor()),
                        0, builder.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                ingredientsBuilder.append(builder);
            }

            // trim off the trailing new line character
            if (ingredientsBuilder.toString().endsWith("\n"))
                holder.ingredientsTV.setText(ingredientsBuilder.delete(ingredientsBuilder.length() - 1, ingredientsBuilder.length()));
            else
                holder.ingredientsTV.setText(ingredientsBuilder);

            Glide.with(holder.imageIV.getContext())
                    .load(recipe.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_android_black_80dp)
                    .error(R.drawable.ic_android_black_80dp)
                    .into(holder.imageIV);
            holder.recipeCard.setOnClickListener(view -> callback.onRecipeClicked(recipe.id));
        }
    }

    private String trimTrailingZeroes(String quantity) {
        if (quantity.contains(".")) {
            while (quantity.endsWith("0")) {
                quantity = quantity.substring(0, quantity.length() - 1);
            }
            if (quantity.endsWith("."))
                quantity = quantity.substring(0, quantity.length() - 1);
        }
        return quantity;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
