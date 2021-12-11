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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import aidooo.spydo.com.chitchat.R;

public class LoginActivity extends AppCompatActivity {

    private Button regipage, login, phoneLogin;
    private TextInputLayout email, pass;
    private ProgressDialog loadingBar;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        regipage = findViewById(R.id.registration_btn);
        login = findViewById(R.id.login_btn);
        //phoneLogin = findViewById(R.id.phone_login_btn);

        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_pass);

        loadingBar = new ProgressDialog(this);
        userAuth = FirebaseAuth.getInstance();
    }

    public void callRegiPage(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        finish();

    }

    public void letLogin(View view) {
        if (!validateFields()) {
            return;
        }

        loadingBar.setTitle("logging in");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        final String mail = email.getEditText().getText().toString().trim();
        final String password = pass.getEditText().getText().toString().trim();

        userAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            goToMainActivity();
                            loadingBar.dismiss();
                            finish();
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                email.setError("Incorrect email");
                            }else{
                                pass.setError("Paswword incorrect");
                            }

                            loadingBar.dismiss();
                            checkInternetConnecttion();

                        }
                    }
                });

    }

    private boolean validateFields() {
        String _email = email.getEditText().getText().toString().trim();
        String _pass = pass.getEditText().getText().toString().trim();
        if (_email.isEmpty()) {
            email.setError("Field can not be empty");
            email.requestFocus();
            return false;
        } else if (_pass.isEmpty()) {
            pass.setError("Field can not be empty");
            pass.requestFocus();
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            pass.setError(null);
            pass.setErrorEnabled(false);

            return true;

        }
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

    public void goToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}