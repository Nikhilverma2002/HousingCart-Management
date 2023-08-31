package com.example.hcart;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.hcart.Model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import www.sanju.motiontoast.MotionToast;

public class Login extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth auth;
    LinearLayout sign_in;
    Dialog google_dialog;
    private static final int RC_SIGN_IN = 101;
    GoogleSignInClient agooglesigninclient;
    DatabaseReference user_reference,reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setStatusBarTransparent();

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();


        user_reference= FirebaseDatabase.getInstance().getReference().child("users");
        reference = FirebaseDatabase.getInstance().getReference().child("admin");
        //Hide the keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        sign_in=findViewById(R.id.sign_in);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        agooglesigninclient = GoogleSignIn.getClient(this,gso);

        sign_in.setOnClickListener(v-> signIn_Google());

    }
    private void signIn_Google() {
        Intent SignInIntent = agooglesigninclient.getSignInIntent();
        startActivityForResult(SignInIntent,RC_SIGN_IN);
        google_dialog = new Dialog(Login.this);
        google_dialog.setCancelable(true);
        google_dialog.setContentView(R.layout.loading);
        google_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        google_dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                Log.e("exception ",e+"");
            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if(task.isSuccessful()){

                        getSharedPreferences("login?",MODE_PRIVATE).edit()
                                .putString("Yes","login_done").apply();

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (Objects.requireNonNull(Objects.requireNonNull(ds.getKey()).trim()).equals(user.getUid().trim())) {
                                        getSharedPreferences("Is_admin",MODE_PRIVATE).edit()
                                                .putString("Yes_of","Admin").apply();
                                    }
                                   /* else {

                                        getSharedPreferences("Is_SP",MODE_PRIVATE).edit()
                                                .putString("Yes_of",ds.getKey()).apply();

                                          getSharedPreferences("Is_admin",MODE_PRIVATE).edit()
                                                .putString("Yes_of","user").apply();
                                    }*/
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if (google_dialog != null && google_dialog.isShowing()) {
                            google_dialog.dismiss();
                        }

                        user = auth.getCurrentUser();

                        assert user != null;

                        MotionToast.Companion.darkColorToast(Login.this,
                                "Welcome \uD83D\uDE03",
                                "Signed in - Successfully.",
                                MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(Login.this,R.font.quicksand_bold));

                        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    if (!Objects.equals(snapshot.child(Objects.requireNonNull(ds.getKey())).child("present").getValue(String.class), "")) {
                                        user_reference.child(user.getUid()).child("email").setValue(user.getEmail());//
                                        user_reference.child(user.getUid()).child("name").setValue(user.getDisplayName());//
                                        user_reference.child(user.getUid()).child("uid").setValue(user.getUid());//
                                        user_reference.child(user.getUid()).child("position").setValue("");//
                                        user_reference.child(user.getUid()).child("number").setValue("");//
                                        user_reference.child(user.getUid()).child("dp_link").setValue(""); //
                                        user_reference.child(user.getUid()).child("adhaar").setValue("");
                                        user_reference.child(user.getUid()).child("instagram").setValue("");
                                        user_reference.child(user.getUid()).child("gender").setValue("");
                                        user_reference.child(user.getUid()).child("present").setValue("0");
                                    }
                                    else {
                                        user_reference.child(user.getUid()).child("email").setValue(user.getEmail());
                                        user_reference.child(user.getUid()).child("name").setValue(user.getDisplayName());
                                        user_reference.child(user.getUid()).child("uid").setValue(user.getUid());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                        updateUI();

                    }
                    else{
                        Toast.makeText(Login.this, "Login failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateUI() {
        Intent intent=new Intent(Login.this, Dashboard.class);
        startActivity(intent);
        finish();

    }

    private void setStatusBarTransparent () {
        Window window = Login.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user!=null){
            Intent intent=new Intent(Login.this, Dashboard.class);
            startActivity(intent);
            finish();
        }
    }
}