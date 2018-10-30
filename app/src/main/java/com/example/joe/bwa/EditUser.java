package com.example.joe.bwa;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditUser extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 234;

    //view objects
    private ImageButton btnUser;
    private Button btnSave;
    private EditText userNameText;
    private ImageView imageUser;

    //uri to store file
    private Uri filePath;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);






            btnUser = (ImageButton) findViewById(R.id.btnUser);
            btnSave = (Button) findViewById(R.id.btnSave);
            imageUser = (ImageView) findViewById(R.id.imageUser);
            userNameText = (EditText) findViewById(R.id.userNameText);

            sharedPref = getPreferences(MODE_PRIVATE);
            // String userId = sharedPref.getString("userId", "0");
            //SharedPreferences.Editor editor = sharedPref.edit();
            // editor.putString("userId", userId);
            //editor.commit();

            // Toast.makeText(this, "userID SF  " + userId, Toast.LENGTH_SHORT).show();




            storageReference = FirebaseStorage.getInstance().getReference();
            mDatabase = FirebaseDatabase.getInstance().getReference(Constants.UPLOADS_EVENT);

            btnUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFileChooser();
                }

            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadFile();
                }
            });


        }












        private void showFileChooser() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }



        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageUser.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }}}


        public String getFileExtension(Uri uri) {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));


        }





        private void uploadFile() {
            //checking if file is available
            if (filePath != null) {
                //displaying progress dialog while image is uploading
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                //getting the storage reference
                StorageReference sRef = storageReference.child(Constants.STORAGE_USER + System.currentTimeMillis() + "." + getFileExtension(filePath));

                //adding the file to reference
                sRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //dismissing the progress dialog
                                progressDialog.dismiss();

                                //displaying success toast
                                Toast.makeText(EditUser.this, "" +
                                        "Login Success ", Toast.LENGTH_LONG).show();
                                sharedPref = getPreferences(MODE_PRIVATE);
                                //creating the upload object to store uploaded image details
                                startActivity(new Intent(EditUser.this,ShowImagesActivity.class));
                                finish();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String userId = user.getUid();
                                String userPhone = user.getPhoneNumber();

                                DatabaseReference pushRef = mDatabase.push();
                                String pushId = pushRef.getKey();


                                DatabaseReference eventRef = FirebaseDatabase
                                        .getInstance()
                                        .getReference(Constants.USER_DATA).child(userId);





                                Map<String, String> uploads = new HashMap<>();
                                uploads.put("nama",userNameText.getText().toString());
                                uploads.put("url", taskSnapshot.getDownloadUrl().toString());
                                uploads.put("phone",userPhone);
                                uploads.put("pushId",pushId);
                                uploads.put("postTime", DateFormat.getDateTimeInstance().format(new Date()));

                                eventRef.setValue(uploads);


                           /*DatabaseReference pushRef = eventRef.push();
                           String pushId = pushRef.getKey();
                           upload.setPushId(pushId);*/


                                sharedPref = getPreferences(MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                //editor.putString("userId", userId);
                                editor.commit();
                                // mDatabase.child(userId).setValue(upload);

                                //adding an upload to firebase database
                                // String uploadId = mDatabase.push().getKey();




                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.dismiss();
                                Toast.makeText(EditUser.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //displaying the upload progress
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });
            }

            //display an error if no file is selected

        }
    }

