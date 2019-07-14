package com.cheadtech.example.bakingtime.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecipeModel {
    @Embedded
    RecipeEntity recipe;

    @Relation(parentColumn = "id", entityColumn = "recipe_id", entity = IngredientsEntity.class)
    List<IngredientsEntity> ingredients;

    @Relation(parentColumn = "id", entityColumn = "recipe_id", entity = StepsEntity.class)
    List<StepsEntity> steps;
}
