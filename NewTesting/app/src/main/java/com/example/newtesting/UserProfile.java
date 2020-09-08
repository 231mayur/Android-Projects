package com.example.newtesting;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class UserProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog dialog;
    private ProgressDialog uploading_message;
    private ImageView profile_image;
    private Button change_profile;
    private TextView confirm_img;
    private static int REQUEST_CODE=1234;
    private StorageReference mStorageRef;
    private FirebaseDatabase firebaseDatabase;
    private File localFile;
    private Uri down_uri;
    private String img_loc;
    private Uri uri;
    private TextView pro_name,pro_mobile_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        change_profile=(Button)findViewById(R.id.change_profile);
        pro_name=(TextView)findViewById(R.id.pro_name);
        pro_mobile_no=(TextView)findViewById(R.id.pro_mobile);
        profile_image=(ImageView)findViewById(R.id.pro_pic);
        confirm_img=(TextView)findViewById(R.id.confirm_img);
        confirm_img.setVisibility(View.INVISIBLE);
        this.setTitle("Profile");
        dialog=new ProgressDialog(UserProfile.this);
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        dialog.setTitle("Profile");
        dialog.setMessage("Loading...");
        uploading_message=new ProgressDialog(UserProfile.this);
        dialog.show();


        mAuth=FirebaseAuth.getInstance();

        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("User").child(uid);
        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select a Photo"),REQUEST_CODE);
                confirm_img.setVisibility(View.VISIBLE);

            }
        });
        confirm_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String i=getImg(uri);
                Log.d("UserProfile","LAADK  "+System.currentTimeMillis());
                databaseReference.child("Image").setValue("photos"+System.currentTimeMillis());
                StorageReference reference=mStorageRef.child("photos"+System.currentTimeMillis());
                uploading_message.show();
                uploading_message.setCancelable(false);
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot1) {
                        uploading_message.dismiss();

                        Toast.makeText(getApplicationContext(),"Photo Uploaded",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploading_message.dismiss();
                        Toast.makeText(getApplicationContext(),"Upload fail",Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        uploading_message.setTitle("Please Wait");
                        uploading_message.setMessage("Uploaded "+progress+"%");
                    }
                });
            }
        });
      databaseReference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

           pro_name.setText(dataSnapshot.child("Name").getValue().toString());
           pro_mobile_no.setText(dataSnapshot.child("Mobile").getValue().toString());
           if(dataSnapshot.child("Image").exists()) {
               img_loc = dataSnapshot.child("Image").getValue().toString();
               StorageReference riversRef = mStorageRef.child(img_loc);

               try {

                   localFile = File.createTempFile(img_loc, "jpg");
                   riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           down_uri = uri;
                       }
                   });
                   riversRef.getFile(localFile)
                           .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                   Picasso.with(UserProfile.this).load(down_uri).into(profile_image);
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception exception) {

                       }
                   });

               } catch (Exception e) {

               }
           }
            dialog.dismiss();

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
              Log.d("UserProfile","Error while getting data");
          }
      });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if( requestCode==REQUEST_CODE && resultCode== Activity.RESULT_OK && data.getData()!=null && data!=null)
        {


            uri =data.getData();
            Log.d("UserProfile","Running  "+uri.toString());
             try
             {
                 Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                 profile_image.setImageBitmap(bitmap);
             }catch (Exception e)
             {
                 Log.d("UserProfile","Image getting error");
             }



        }

    }
    public String getImg(Uri uri1)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri1));
    }
}
