package com.example.recipe;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.recipe.Model.Recipe;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class UploadRecipe extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final int PICK_IMAGE_REQUEST = 1;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    ImageView recipeImage;
    Button selectimageBtn, uploadrecipeBtn, cancelBtn;
    Spinner recipe_types;
    EditText ingredientsEditText, stepsEditText, foodnameEditText;
    Uri mImageUri;
    String foodid;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);
        this.setTitle("UPLOAD NEW RECIPE");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Recipe");
        storageReference = FirebaseStorage.getInstance().getReference("RecipeImage");
        progressDialog = new ProgressDialog(this);

        recipe_types = (Spinner) findViewById(R.id.Recipe_Type);
        foodnameEditText = (EditText) findViewById(R.id.FoodNameEditText);
        ingredientsEditText = (EditText) findViewById(R.id.IngredientsEditText);
        stepsEditText = (EditText) findViewById(R.id.StepsEditText);
        uploadrecipeBtn = (Button) findViewById(R.id.uploadBtn);
        selectimageBtn = (Button) findViewById(R.id.Selectbtn);
        recipeImage =  (ImageView) findViewById(R.id.FoodImage);
        cancelBtn = (Button) findViewById(R.id.Cancelbtn);


        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.recipetypes, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        recipe_types.setAdapter(arrayAdapter);
        recipe_types.setOnItemSelectedListener(this);

        uploadrecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String food_name = foodnameEditText.getText().toString().trim();
                String ingredients = ingredientsEditText.getText().toString().trim();
                String steps = stepsEditText.getText().toString().trim();


                if(TextUtils.isEmpty(food_name)){
                    Toast.makeText(UploadRecipe.this, "Please enter the food name", Toast.LENGTH_SHORT).show();

                    return;
                }

                else if(TextUtils.isEmpty(ingredients)){
                    Toast.makeText(UploadRecipe.this, "Please enter the ingredients", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(steps)){
                    Toast.makeText(UploadRecipe.this, "Please enter the steps", Toast.LENGTH_SHORT).show();
                    return;
                }

                Upload();
                progressDialog.setMessage("Your request is currently being processed.");
            }
        });

        selectimageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadRecipe.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode ==  RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(recipeImage);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void Upload(){


        if(mImageUri !=null){
             progressDialog.show();
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    Recipe recipe = new Recipe(
                            recipe_types.getSelectedItem().toString(),
                            foodnameEditText.getText().toString(),
                            ingredientsEditText.getText().toString(),
                            stepsEditText.getText().toString(),
                            foodid


                    );
                     progressDialog.dismiss();
                    recipe.setImageUrl(downloadUrl.toString());
                    databaseReference.push().setValue(recipe);
                    Toast.makeText(UploadRecipe.this,"New Recipe Uploaded Successfull",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });


        }else{
            Toast.makeText(UploadRecipe.this,"No image selected", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent){

    }
}
