package com.example.joe.bwa;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    int mYear,mMonth,mDay;
    static final int DATE_DIALOG_id=0;
    private String[] arrmonth ={"Jan","Feb","Mar","Apr","May","Jun","Jul","Agu","Sep","Okt","Nop","Des"};

    private static final int PICK_IMAGE_REQUEST = 234;

    //view objects
    private Button buttonChoose;
    private Button buttonUpload;
    private EditText lokasiText;
    private EditText tentangText;

    private ImageView imageView;
    private EditText tanggalText;
    private EditText jamText;
    private EditText namaText;

    //uri to store file
    private Uri filePath;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        imageView = (ImageView) findViewById(R.id.imageView);
        lokasiText = (EditText) findViewById(R.id.lokasiText);

        tentangText = (EditText) findViewById(R.id.tentangText);
        tanggalText=(EditText)findViewById(R.id.tglText);
        jamText=(EditText)findViewById(R.id.jamText);
        namaText=(EditText)findViewById(R.id.namaText);
        sharedPref = getPreferences(MODE_PRIVATE);
       // String userId = sharedPref.getString("userId", "0");
        //SharedPreferences.Editor editor = sharedPref.edit();
       // editor.putString("userId", userId);
        //editor.commit();

       // Toast.makeText(this, "userID SF  " + userId, Toast.LENGTH_SHORT).show();




        final Calendar c =Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay=c.get(Calendar.DAY_OF_MONTH);


        tanggalText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(DATE_DIALOG_id);
                return false;
            }
        });

        jamText.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                     @Override
                     public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                         String am_pm = "";

                         Calendar datetime = Calendar.getInstance();
                         datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                         datetime.set(Calendar.MINUTE, minute);

                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                             am_pm = "AM";

                       else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";



                         // Create a new instance of TimePickerDialog and return it


                         String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";

                         jamText.setText( strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm );
                     }


                 //   public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                      //  jamText.setText( "" + selectedHour + ":" + "" +selectedMinute);

                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });




    storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.UPLOADS_EVENT).child("event");

        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }

        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
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
                imageView.setImageBitmap(bitmap);
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
            StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filePath));

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(MainActivity.this, "File Uploaded ", Toast.LENGTH_LONG).show();
                            sharedPref = getPreferences(MODE_PRIVATE);
                            //creating the upload object to store uploaded image details
                            progressDialog.setMessage("Tunggu...");
                            progressDialog.show();
                                startActivity(new Intent(MainActivity.this,ShowImagesActivity.class));
                                finish();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = user.getUid();
                            String userPhone = user.getPhoneNumber();







                            DatabaseReference eventRef = FirebaseDatabase
                                    .getInstance()
                                    .getReference(Constants.UPLOADS_EVENT).push();





                            Map<String, String> uploads = new HashMap<>();
                            uploads.put("lokasi",lokasiText.getText().toString());
                            uploads.put("tanggal",tanggalText.getText().toString());
                            uploads.put("tentang",tentangText.getText().toString());
                            uploads.put("jam",jamText.getText().toString());
                            uploads.put("url", taskSnapshot.getDownloadUrl().toString());
                            uploads.put("phone",userPhone);
                            uploads.put("uid",userId);

                            uploads.put("pushId",mDatabase.getKey());
                            uploads.put("nama",namaText.getText().toString());
                            uploads.put("postTime",DateFormat.getDateTimeInstance().format(new Date()));


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
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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
    @Override
    protected Dialog onCreateDialog(int id){
        switch (id){
            case DATE_DIALOG_id:
                return new DatePickerDialog(
                        this,mDateSetListener1,mYear,mMonth,mDay);


        }return null;
    }
private DatePickerDialog.OnDateSetListener mDateSetListener1=
new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int monthofYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthofYear;
        mDay = dayOfMonth;
        String sdate = LPad(mDay + "", "0",2) + "-" + arrmonth[mMonth] + "-" + mYear;
        tanggalText.setText(sdate);
    }
};

    private static String LPad (String schar, String spad, int len) {
        String sret = schar;
        for (int i = sret.length(); i <len; i++) {
            sret = spad + sret;
        }
        return new String(sret);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}





