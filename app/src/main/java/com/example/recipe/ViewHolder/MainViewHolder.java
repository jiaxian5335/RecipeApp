package com.example.recipe.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipe.Interface.ItemClickListener;
import com.example.recipe.R;

public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView FoodImage;
    public TextView RecipeType;
    public TextView FoodName;
    private ItemClickListener itemClickListener;

    public MainViewHolder(View v){
        super(v);

        FoodImage = (ImageView) v.findViewById(R.id.Food_Image);
        RecipeType = (TextView) v.findViewById(R.id.RecipeType);
        FoodName = (TextView) v.findViewById(R.id.RecipeName);
        v.setOnClickListener(this);
        }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
public void onClick(View v){

        itemClickListener.onClick(v,getAdapterPosition(),false);
        }
}
