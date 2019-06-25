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
import android.widget.Button;

import com.example.recipe.Interface.ItemClickListener;
import com.example.recipe.Model.Recipe;
import com.example.recipe.ViewHolder.MainViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button uploadrecipeBtn,editrecipeBtn, breakfastBtn, lunchBtn, dinnerBtn;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Recipe,MainViewHolder> adapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("RECIPE APP");

        uploadrecipeBtn = (Button) findViewById(R.id.uploadBtn);
        editrecipeBtn = (Button) findViewById(R.id.editBtn);
        breakfastBtn = (Button) findViewById(R.id.breakfastbtn);
        lunchBtn = (Button) findViewById(R.id.lunchbtn);
        dinnerBtn = (Button) findViewById(R.id.dinnerbtn);
        uploadrecipeBtn.setOnClickListener(this);
        editrecipeBtn.setOnClickListener(this);
        breakfastBtn.setOnClickListener(this);
        lunchBtn.setOnClickListener(this);
        dinnerBtn.setOnClickListener(this);



        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipe");
        databaseReference.keepSynced(true);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_recipelist);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Query query = databaseReference.orderByKey();
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
                        Intent intent = new Intent(MainActivity.this,RecipeDetail.class);
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
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart(){
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onClick(View v){

        if(v == uploadrecipeBtn){
            startActivity(new Intent(this,UploadRecipe.class));
        }
        else if (v == editrecipeBtn){
            startActivity(new Intent(this,EditList.class));
        }
        else if (v == breakfastBtn){
            Intent intent = new Intent(MainActivity.this,FilteredRecipe.class);
            String breakfast = "Breakfast";
            intent.putExtra("recipeType",breakfast);
            startActivity(intent);
        }
        else if (v == lunchBtn){
            Intent intent = new Intent(MainActivity.this,FilteredRecipe.class);
            String lunch = "Lunch";
            intent.putExtra("recipeType",lunch);
            startActivity(intent);
        }
        else if (v == dinnerBtn){
            Intent intent = new Intent(MainActivity.this,FilteredRecipe.class);
            String dinner = "Dinner";
            intent.putExtra("recipeType",dinner);
            startActivity(intent);
        }

    }
}
