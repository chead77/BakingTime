package com.cheadtech.example.bakingtime.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class RecipeEntity {
    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "servings")
    public Integer servings;

    @ColumnInfo(name = "image")
    public String image;
}
