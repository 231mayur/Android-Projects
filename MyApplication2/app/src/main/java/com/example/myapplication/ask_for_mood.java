package com.example.myapplication;
/*****************************************************************
 * Name      : Mayur Chavhan
 * Change    :Adding image view to add profile picture.
 * Change Id : C1
 * Date      : 10/10/2020
 * ****************************************************************
 * Name :Mayur Chavhan
 * Change : Adding Progress bar
 * Change Id: C2
 *  ***************************************************************
 * Name: Mayur Chavhan
 * Change : Starting new activity to access home page (after_login.java)
 *          and adding img url to firebase
 * Change Id: C3
 * *******************************************************************
 * Name: Mayur Chavhan
 * Change: Adding image url to database
 * Change Id: C4
 * *****************************************************************/
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ask_for_mood extends AppCompatActivity {
private Button all_set;
private EditText edttext_person;
private String person_name;
FirebaseDatabase rootNode;
//C4
    private String image_url;
private String gender;
//#C1
private ImageView pro_image;
private Uri imageuri,uri_extention;
private StorageReference storageReference;
private TextView text_progress;
private RadioButton rbutton;
private RadioGroup rgroup;
//#C2
    private String phone_number;
    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    AlertDialog alertDialog;
    String str_uri;
    View view;
DatabaseReference reference;
private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_mood);
        this.getSupportActionBar().hide();
        //#C2 start here
        phone_number=getIntent().getStringExtra("phone");
        Toast.makeText(getApplicationContext(),phone_number,Toast.LENGTH_LONG).show();
        alertDialogBuilder = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();
        view= inflater.inflate(R.layout.dialog_layout,null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        //#C2 end here
        edttext_person=(EditText)findViewById(R.id.edt_person_name);
        pro_image=(ImageView)findViewById(R.id.profile_pic);
        text_progress=(TextView)findViewById(R.id.progress);
        text_progress.setVisibility(View.GONE);
        rgroup=(RadioGroup)findViewById(R.id.radioGroup);
        //Handling profile iamge
        //#C1 Starts here
        storageReference= FirebaseStorage.getInstance().getReference("Images");

        pro_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePicker();
            }
        });
        //#C1 end here

        all_set=(Button)findViewById(R.id.all_set);

        //To write the data on firebase
        uid=FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        rootNode=FirebaseDatabase.getInstance();
        reference=rootNode.getReference("Users");
        Log.d("Userid",uid);
        all_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person_name=edttext_person.getText().toString();
                if (person_name.isEmpty())
                {
                    edttext_person.requestFocus();
                    edttext_person.setError("Name required");
                    return;
                }
                int selectedGenderId=rgroup.getCheckedRadioButtonId();
                rbutton=(RadioButton)findViewById(selectedGenderId);
                if (selectedGenderId == -1)
                {
                    Toast.makeText(getApplicationContext(),"Please select gender", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    gender=rbutton.getText().toString();
                }
                photoLoader();
            }


        });
    }

    private String getExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void photoLoader() {

        str_uri=System.currentTimeMillis()+".jpg"+getExtension(uri_extention);
        final StorageReference ref=storageReference.child(str_uri);
        ref.putFile(uri_extention)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        Toast.makeText(getApplicationContext(),"Image Uploaded successfully",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                        //change C3
                        //C4 start here

                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        if(downloadUri.isSuccessful()){
                            image_url = downloadUri.getResult().toString();
                            System.out.println("## Stored path is "+image_url);
                            Log.d("Image",image_url);
                        }
                        GetHelper getHelper=new GetHelper(person_name,gender,phone_number,image_url);
                        reference.child(uid).setValue(getHelper);
                        Intent intent=new Intent(ask_for_mood.this,after_login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        //C4 end here

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        alertDialog.show();
                        text_progress.setVisibility(View.VISIBLE);
                        double progress
                                = (100.0
                                * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                        //#C2 start here
                        TextView text_progress1=(TextView)view.findViewById(R.id.uploaded);
                        text_progress1.setText(
                                "Uploaded "
                                        + (int)progress + "%");
                        //#C2 end here
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(getApplicationContext(),"Please retry image not updloaded",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                });
    }

    //Start change #C1
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode== CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode== RESULT_OK )
        {
            Uri uri=CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,uri)){
                imageuri=uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                }

            }else {
                startImageCrop(imageuri);
            }
        }
        if (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE == requestCode)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                uri_extention=result.getUri();
                pro_image.setImageURI(uri_extention);
            }
        }

    }

    private void startImageCrop(Uri imageuri) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(10,10)
                .start(this);
    }

    private void filePicker() {
        CropImage.startPickImageActivity(ask_for_mood.this);
    }
    //END change #C1
}