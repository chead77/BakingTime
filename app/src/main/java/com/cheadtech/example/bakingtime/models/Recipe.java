package com.cheadtech.example.bakingtime.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

@SuppressWarnings({"unused"})
public class Recipe implements Parcelable {
    public Recipe() {}

    public Recipe(int id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps, int servings, String image) {
        this.id=id;
        this.name=name;
        this.ingredients=ingredients;
        this.steps=steps;
        this.servings=servings;
        this.image=image;
    }

    public Integer id;
    public String name;
    public ArrayList<Ingredient> ingredients;
    public ArrayList<Step> steps;
    public Integer servings;
    public String image;

    protected Recipe(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            ingredients = null;
        } else {
            ingredients = new ArrayList<>();
            in.readList(ingredients, Ingredient.class.getClassLoader());
        }
        if (in.readByte() == 0) {
            steps = null;
        } else {
            steps = new ArrayList<>();
            in.readList(steps, Step.class.getClassLoader());
        }
        if (in.readByte() == 0) {
            servings = null;
        } else {
            servings = in.readInt();
        }
        image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(name);
        if (ingredients == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeList(ingredients);
        }
        if (steps == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeList(steps);
        }
        if (servings == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(servings);
        }
        parcel.writeString(image);
    }
}
