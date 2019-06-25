package com.example.recipe;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipe.Model.Recipe;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditRecipe extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView foodimage;
    TextView  foodname, ingredients, steps;
    Spinner recipetypes;
    Button editbtn, deletebtn, cancelbtn, selectimagebtn;
    String recipeid = "";
    String imageurl;
    Recipe recipe;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    Uri mImageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        this.setTitle("EDIT/ DELETE RECIPE");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipe");
        storageReference = FirebaseStorage.getInstance().getReference("RecipeImage");
        databaseReference.keepSynced(true);

        foodimage = (ImageView) findViewById(R.id.EditFoodImage);
        recipetypes = (Spinner) findViewById(R.id.Recipe_TypeSpinner);
        foodname = (TextView) findViewById(R.id.EditFoodName);
        ingredients = (TextView) findViewById(R.id.EditIngredients);
        steps = (TextView) findViewById(R.id.EditSteps);
        selectimagebtn = (Button) findViewById(R.id.SelectImagebtn);
        editbtn = (Button) findViewById(R.id.Edit_btn);
        deletebtn = (Button) findViewById(R.id.Delete_btn);
        cancelbtn = (Button) findViewById(R.id.Cancel_btn);

        progressDialog = new ProgressDialog(this);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.recipetypes, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        recipetypes.setAdapter(arrayAdapter);
        recipetypes.setOnItemSelectedListener(this);


        if(getIntent()!=null){
            recipeid = getIntent().getStringExtra("RecipeId");
        }
        if(!recipeid.isEmpty())
        {
            getDetail(recipeid);

        }

        //Edit Button
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             String foodName = foodname.getText().toString().trim();
             String Ingredients = ingredients.getText().toString().trim();
             String Steps = steps.getText().toString().trim();

             if(TextUtils.isEmpty(foodName)){
                 Toast.makeText(EditRecipe.this,"Please enter the food name.",Toast.LENGTH_SHORT).show();
                 return;
             }
             else if(TextUtils.isEmpty(Ingredients)){
                 Toast.makeText(EditRecipe.this,"Please enter the ingredients.",Toast.LENGTH_SHORT).show();
                 return;
             }
             else if(TextUtils.isEmpty(Steps)){
                 Toast.makeText(EditRecipe.this,"Please enter the steps.",Toast.LENGTH_SHORT).show();
                 return;
             }

             Edit();
            }
        });

        //Delete Button
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete(recipeid);
            }
        });

        //Cancel Button
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditRecipe.this, EditList.class);
                startActivity(intent);
                finish();

            }
        });

        selectimagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

    }

    private void getDetail(final String recipeid){
        databaseReference.child(recipeid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Recipe recipe = dataSnapshot.getValue(Recipe.class);
                Picasso.with(getBaseContext()).load(recipe.getImageUrl()).into(foodimage);
                foodname.setText(recipe.getFoodname());
                ingredients.setText(recipe.getIngredients());
                steps.setText(recipe.getSteps());
                recipetypes.setTag(recipe.getRecipetype());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Edit function
    private void Edit(){
        progressDialog.setMessage("Your request is currently being processed.");

        if(mImageUri!=null){
            progressDialog.show();
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    Recipe recipe = new Recipe(
                            recipetypes.getSelectedItem().toString(),
                            foodname.getText().toString(),
                            ingredients.getText().toString(),
                            steps.getText().toString(),
                            imageurl
                    );
                    progressDialog.dismiss();
                    recipe.setImageUrl(downloadUrl.toString());
                    databaseReference.child(recipeid).setValue(recipe);

                    Toast.makeText(EditRecipe.this,"Recipe Edited Successful",Toast.LENGTH_SHORT).show();
                    finish();
                }

            });
        }
        else
        {
            Toast.makeText(EditRecipe.this,"Please select image", Toast.LENGTH_SHORT).show();
        }
    }

    //Delete function
    private void Delete(String recipeid){
        progressDialog.setMessage("Your request is currently being processed.");
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipe").child(recipeid);
        databaseReference.removeValue();
        progressDialog.dismiss();
        Toast.makeText(EditRecipe.this,"Recipe Deleted Successful",Toast.LENGTH_SHORT).show();
        finish();

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent){

    }
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode ==  RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(foodimage);
        }
    }



    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
