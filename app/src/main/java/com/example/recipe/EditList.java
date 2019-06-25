package com.example.recipe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class EditList extends AppCompatActivity {


    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Recipe,EditListViewHolder> adapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        this.setTitle("SELECT THE RECIPE");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipe");
        databaseReference.keepSynced(true);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_editlist);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Query query = databaseReference.orderByKey();
        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Recipe>()
                .setQuery(query,Recipe.class).build();

        adapter = new FirebaseRecyclerAdapter<Recipe, EditListViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull EditListViewHolder holder, int position, @NonNull Recipe model) {
                holder.RecipeType.setText(model.getRecipetype());
                holder.FoodName.setText(model.getFoodname());
                Picasso.with(getBaseContext()).load(model.getImageUrl()).into(holder.FoodImage);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(EditList.this,EditRecipe.class);
                        intent.putExtra("RecipeId",adapter.getRef(position).getKey());
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @NonNull
            @Override
            public EditListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipelist,viewGroup,false);
                return new EditListViewHolder(view);
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
}
