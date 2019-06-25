package com.example.recipe;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipe.Model.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RecipeDetail extends AppCompatActivity {

    ImageView foodimage;
    TextView recipetype, foodname, ingredients, steps;
    String recipeid = "";
    Recipe recipe;

    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        this.setTitle("RECIPE DETAILS");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipe");
        databaseReference.keepSynced(true);

        foodimage = (ImageView) findViewById(R.id.food_image);
        recipetype = (TextView) findViewById(R.id.recipe_type);
        foodname = (TextView) findViewById(R.id.food_name);
        ingredients = (TextView) findViewById(R.id.Ingredients);
        steps = (TextView) findViewById(R.id.Steps);

        if(getIntent()!=null){
            recipeid = getIntent().getStringExtra("RecipeId");
        }
        if(!recipeid.isEmpty())
        {
            getDetail(recipeid);

        }
    }

    private void getDetail(final String recipeid){
        databaseReference.child(recipeid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipe = dataSnapshot.getValue(Recipe.class);
                Picasso.with(getBaseContext()).load(recipe.getImageUrl()).into(foodimage);
                recipetype.setText(recipe.getRecipetype());
                foodname.setText(recipe.getFoodname());
                ingredients.setText(recipe.getIngredients());
                steps.setText(recipe.getSteps());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
