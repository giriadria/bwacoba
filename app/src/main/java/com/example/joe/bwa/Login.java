package com.example.joe.bwa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText NoTelepon, SetKode;
    private Button Masuk, Verifikasi, Resend;
    private TextView PhoneID;

    //Variable yang Dibutuhkan Untuk Autentikasi
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener stateListener;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String VerifikasiID;
    private String No_Telepon;
    private SharedPreferences sharedPref;
    private DatabaseReference mDatabase, mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PhoneID = findViewById(R.id.no_id);
        NoTelepon = findViewById(R.id.phone);
        SetKode = findViewById(R.id.setVerifi);
        Masuk = findViewById(R.id.login);
        Masuk.setOnClickListener(this);
        Verifikasi = findViewById(R.id.verifi);
        Verifikasi.setOnClickListener(this);
        Resend = findViewById(R.id.resend);
        Resend.setOnClickListener(this);
        Resend.setEnabled(false);
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.UPLOADS_EVENT);
        mUser = FirebaseDatabase.getInstance().getReference(Constants.USER_DATA);
        //Menghubungkan Project Dengan Firebase Authentication

        auth = FirebaseAuth.getInstance();
        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Meneteksi Apakah Ada com.example.joe.bwa.Splash.User Yang Sedang Login (Belum Logout)
                if(user != null){
                    String userPhone = user.getPhoneNumber();
                    String userId = user.getUid();


                    DatabaseReference userRef = FirebaseDatabase
                            .getInstance()
                            .getReference(Constants.USER_DATA).child(userId);
                  // Map<String, String> uploads1 = new HashMap<>();
                  // uploads1.put("Phone",userPhone);


                    sharedPref = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userId", userId);
                    editor.commit();
                  // mUser.child(userId).setValue(uploads1);
                   // userRef.setValue(uploads1);

                    //Jika Ada, com.example.joe.bwa.Splash.User Tidak perlu Login Lagi, dan Langsung Menuju Acivity Yang Dituju
                    startActivity(new Intent(Login.this, ShowImagesActivity.class));
                    Toast.makeText(Login.this, "Wellcome " + userPhone, Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Melampirkan Listener pada FirebaseAuth saat Activity Dimulai
        auth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(stateListener != null){
            //Menghapus Listener pada FirebaseAuth saat Activity Dihentikan
            auth.removeAuthStateListener(stateListener);
        }
    }

    private void setupVerificationCallback(){
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // Callback didalam sini akan dipanggil/dieksekusi saat terjadi proses pengiriman kode
                // Dan com.example.joe.bwa.Splash.User Diminta untuk memasukan kode verifikasi

                // Untuk Menyimpan ID verifikasi dan kirim ulang token
                VerifikasiID = verificationId;
                resendToken = token;
                Resend.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Mendapatkan Kode Verifikasi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential Credential) {
                // Callback disini akan dipanggil saat Verifikasi Selseai atau Berhasil
                Toast.makeText(getApplicationContext(), "Verifikasi Selesai", Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(Credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // Callback disini akan dipanggil saat permintaan tidak valid atau terdapat kesalahan
                Toast.makeText(getApplicationContext(), "Verifikasi Gagal, Silakan Coba Lagi", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Sign In Berhasil
                            startActivity(new Intent(Login.this,EditUser.class));
                            finish();
                        }else{
                            //Sign In Gagal
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                // Kode Yang Dimasukan tidal Valid.
                                Toast.makeText(getApplicationContext(), "Kode yang dimasukkan tidak valid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                No_Telepon = PhoneID.getText()+NoTelepon.getText().toString();
                setupVerificationCallback();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        No_Telepon, //NO Telepon Untuk Di Verifikasi
                        60, //Durasi Waktu Habis
                        TimeUnit.SECONDS, //Unit Timeout
                        this, //Activity
                        callbacks); // OnVerificationStateChangedCallbacks
                Toast.makeText(getApplicationContext(), "Memverifikasi, Mohon Tunggu", Toast.LENGTH_SHORT).show();
                NoTelepon.setText("");
                break;

            case R.id.verifi:
                String verifiCode = SetKode.getText().toString();
                if(TextUtils.isEmpty(verifiCode)){
                    //Memberi Pesan pada user bahwa kode verifikasi tidak boleh kosong saat menekan Tombol Verifikasi
                    Toast.makeText(getApplicationContext(),"Masukan Kode Verifikasi", Toast.LENGTH_SHORT).show();
                }else{
                    //Memverifikasi Nomor Telepon, Saat Tombol Verifikasi Ditekan
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerifikasiID, verifiCode);
                    signInWithPhoneAuthCredential(credential);
                    Toast.makeText(getApplicationContext(),"Sedang Memverifikasi", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.resend:
                No_Telepon = PhoneID.getText()+NoTelepon.getText().toString();
                setupVerificationCallback();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        No_Telepon, //NO Telepon Untuk Di Vertifikai
                        60, //Durasi Waktu Habis
                        TimeUnit.SECONDS, //Unit Timeout
                        this, //Activity
                        callbacks, // OnVerificationStateChangedCallbacks
                        resendToken); // Digunakan untuk mengirim ulang kembali kode verifikasi
                Toast.makeText(getApplicationContext(), "Mengirim Ulang Kode Verifikasi", Toast.LENGTH_SHORT).show();
                break;
        }
    }}
