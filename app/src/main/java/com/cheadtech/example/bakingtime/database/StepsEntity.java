package com.cheadtech.example.bakingtime.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "steps")
class StepsEntity {
    @ColumnInfo(name = "row_id")
    @PrimaryKey(autoGenerate = true)
    Integer rowId;

    @ColumnInfo(name = "step_id")
    public Integer stepId;

    @ColumnInfo(name = "recipe_id")
    public Integer recipeId;

    @ColumnInfo(name = "short_description")
    public String shortDescription;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "video_url")
    public String videoUrl;

    @ColumnInfo(name = "thumbnail_url")
    public String thumbnailUrl;
}
