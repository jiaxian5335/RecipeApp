package com.example.recipe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.Interface.ItemClickListener;
import com.example.recipe.Model.Recipe;
import com.example.recipe.ViewHolder.EditListViewHolder;
import com.example.recipe.ViewHolder.MainViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class FilteredRecipe extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Recipe,MainViewHolder> adapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String recipe_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_recipe);
        this.setTitle("FILTERED RECIPE");

        if(getIntent()!= null)
            recipe_type = getIntent().getStringExtra("recipeType");

        if(!recipe_type.isEmpty()&& recipe_type!=null){
            loadFilteredList(recipe_type);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipe");
        databaseReference.keepSynced(true);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_filteredlist);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

    }

    private void loadFilteredList(String recipe_type){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipe");
        Query query = databaseReference.orderByChild("recipetype").equalTo(recipe_type);
        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Recipe>()
                .setQuery(query,Recipe.class).build();


        adapter = new FirebaseRecyclerAdapter<Recipe, MainViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull MainViewHolder holder, int position, @NonNull Recipe model) {
               holder.RecipeType.setText(model.getRecipetype());
                holder.FoodName.setText(model.getFoodname());
                Picasso.with(getBaseContext()).load(model.getImageUrl()).into(holder.FoodImage);

                  holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(FilteredRecipe.this,RecipeDetail.class);
                        intent.putExtra("RecipeId",adapter.getRef(position).getKey());
                        startActivity(intent);

                    }
                });


            }

            @NonNull
            @Override
            public MainViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipelist,viewGroup,false);
                return new MainViewHolder(view);
            }
        };

    }




    @Override
    public void onStart(){
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
