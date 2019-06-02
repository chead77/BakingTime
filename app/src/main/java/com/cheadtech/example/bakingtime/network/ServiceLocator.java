package com.cheadtech.example.bakingtime.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {
    private static ServiceLocator serviceLocator;

    public static ServiceLocator getInstance() {
        if (serviceLocator == null)
            serviceLocator = new ServiceLocator();
        return serviceLocator;
    }

    // Maintain singleton object for the service interface here
    private RecipeService recipeService;
    public RecipeService getRecipeService() {
        if (recipeService == null) {
            recipeService = new Retrofit.Builder()
                    .baseUrl("https://d17h27t6h515a5.cloudfront.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RecipeService.class);
        }
        return recipeService;
    }
}
