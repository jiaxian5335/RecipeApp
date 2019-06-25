package com.example.recipe.Model;

public class Recipe {
    private String foodimage;
    private String recipetype;
    private String foodname;
    private String ingredients;
    private String steps;
    private String imageUrl;

    public Recipe(){

    }

    public Recipe( String recipetype, String ingredients, String steps) {
        this.recipetype = recipetype;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public Recipe(String recipetype, String foodname, String ingredients, String steps, String imageUrl) {
        this.recipetype = recipetype;
        this.foodname = foodname;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
    }



    public String getFoodimage() {
        return foodimage;
    }

    public void setFoodimage(String foodimage) {
        this.foodimage = foodimage;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getRecipetype() {
        return recipetype;
    }

    public void setRecipetype(String recipetype) {
        this.recipetype = recipetype;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}


