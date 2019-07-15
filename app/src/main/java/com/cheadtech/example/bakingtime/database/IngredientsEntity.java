package com.cheadtech.example.bakingtime.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredients")
class IngredientsEntity {
    @ColumnInfo(name = "row_id")
    @PrimaryKey(autoGenerate = true)
    Integer rowId;

    @ColumnInfo(name = "recipe_id")
    public Integer recipeId;

    @ColumnInfo(name = "quantity")
    public Float quantity;

    @ColumnInfo(name = "measure")
    public String measure;

    @ColumnInfo(name = "ingredient")
    public String ingredient;
}
