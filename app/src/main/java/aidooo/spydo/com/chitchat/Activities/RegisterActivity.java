package aidooo.spydo.com.chitchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import aidooo.spydo.com.chitchat.R;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout pass, comPass, email;
    private String passs, comPasss, emaill;
    private FirebaseAuth userAuth;
    private DatabaseReference reference;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pass = findViewById(R.id.regi_pass);
        comPass = findViewById(R.id.regiCom_pass);
        email = findViewById(R.id.regi_email);


        loadingBar = new ProgressDialog(this);

        userAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();


    }

    public void callHomeRage(View view) {

        emaill = email.getEditText().getText().toString().trim();
        passs = pass.getEditText().getText().toString().trim();
        comPasss = comPass.getEditText().getText().toString().trim();

        if (!validateEmail() | !validatePass() | !validateComPass()) {
            return;
        }

        loadingBar.setTitle("Creating New User");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(true);

        storeUserData();

    }

    private void storeUserData() {

        loadingBar.setTitle("Signing up");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        userAuth.createUserWithEmailAndPassword(emaill, passs)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            String currentUserID = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
                            reference.child("Users").child(currentUserID).setValue("");

                            goToMainActivity();
                            loadingBar.dismiss();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                email.setError("User already exists");
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "user Already exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
        checkInternetConnecttion();

    }

    private boolean validateEmail() {
        String val = email.getEditText().getText().toString().trim();
        String checkEmail = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            email.setError("Invalid Email");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePass() {
        String val = pass.getEditText().getText().toString().trim();
        String checkPass = "(?=^.{6,255}$)((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))^.*";
        if (val.isEmpty()) {
            pass.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkPass)) {
            pass.setError("Password should be Complex");
            return false;
        } else {
            pass.setError(null);
            pass.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateComPass() {
        String com_Pass = comPass.getEditText().getText().toString().trim();
        String check_pass = pass.getEditText().getText().toString().trim();
        if (!com_Pass.equals(check_pass)) {
            comPass.setError("Password mismatch");
            return false;
        } else {
            comPass.setError(null);
            comPass.setErrorEnabled(false);
            return true;
        }
    }

    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void checkInternetConnecttion() {
        ConnectivityManager manager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (activeNetwork != null) {


        } else {
            Toast.makeText(this, "No Network enabled", Toast.LENGTH_SHORT).show();
        }


    }

    public void BackToLogin(View view) {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    public void goToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}