package aidooo.spydo.com.chitchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import aidooo.spydo.com.chitchat.Fragments.BottomSheetFragment;
import aidooo.spydo.com.chitchat.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAcc;
    private TextInputEditText birthdate,fullname,username,phoneNum, newPassword, confirmPass;
    private EditText status;
    private CircleImageView userProfilePic;
    private ProgressDialog loadingBar;
    private DatabaseReference reference;
    private FirebaseAuth userAuth;
    private String currentUserID;
    private StorageReference userProfilePicRef;

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeFileds();

        updateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
                loadingBar.show();
            }
        });

        retriveUserInfo();

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

    }

    private void sendUserToLoginActivity() {
        Intent LoginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.updatePass){
            updatePass();
        }
        if (item.getItemId() == R.id.logout){
            final ProgressDialog _loadingbar;
            _loadingbar = new ProgressDialog(this);
            _loadingbar.setTitle("Logging Out");
            _loadingbar.setMessage("Please wait...");
            _loadingbar.show();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            userAuth.signOut();
            sendUserToLoginActivity();
        }

        return true;
    }

    private void updatePass() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());

    }

    private void initializeFileds() {

        updateAcc = findViewById(R.id.update_btn);
        status = findViewById(R.id.user_status);
        birthdate = findViewById(R.id.user_birthdate_txt);
        userProfilePic = findViewById(R.id.profile_image);
        phoneNum = findViewById(R.id.regi_user_phone_txt);
        username = findViewById(R.id.regi_username_txt);
        fullname = findViewById(R.id.regi_fullname_txt);

        mToolBar = (Toolbar) findViewById(R.id.mainpage_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("ChitChat");

        reference = FirebaseDatabase.getInstance().getReference();
        userAuth = FirebaseAuth.getInstance();
        currentUserID = userAuth.getCurrentUser().getUid();
        userProfilePicRef = FirebaseStorage.getInstance().getReference().child("ProfilePic");

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Updating");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(true);

    }

    private void cropImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Picasso.get().load(resultUri).into(userProfilePic);

                uploadImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    protected void  uploadImage(Uri resultUri){

                StorageReference filePath = userProfilePicRef.child(currentUserID +".jpg");


                final ProgressDialog _loadingbar;
                _loadingbar = new ProgressDialog(this);
                _loadingbar.setTitle("Updating Profile Picture");
                _loadingbar.setMessage("Please wait...");
                _loadingbar.setCanceledOnTouchOutside(false);
                _loadingbar.show();
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> TaskSnapshot) {

                        if (TaskSnapshot.isSuccessful()){

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProfilePic").child(currentUserID+".jpg");
                            String proPic= storageReference.toString();

                            reference.child("Users").child(currentUserID).child("images")
                                    .setValue(proPic)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                _loadingbar.dismiss();
                                                Toast.makeText(SettingsActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                        }
                        else{
                            String massage = TaskSnapshot.getException().toString().trim();
                            Toast.makeText(SettingsActivity.this,"Error: "+massage, Toast.LENGTH_SHORT).show();
                            _loadingbar.dismiss();
                        }

                    }
                });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    private void updateSettings() {
        final String setUserBirthdate = birthdate.getText().toString().trim();
        final String phonenum = phoneNum.getText().toString().trim();
        final String fullName = fullname.getText().toString().trim();
        final String userName = username.getText().toString().trim();
        final String setStatus = status.getText().toString().trim();

        if (TextUtils.isEmpty(setUserBirthdate)){
            birthdate.setError("Field Can not be empty");
        }
        if (TextUtils.isEmpty(setStatus)){
            birthdate.setError("Please write your status");
        }

        if (!validateFullname() | !validatePhoneNumber() | !validateUsername()){
            return;
        }

        else {
           HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("username",userName);
            profileMap.put("name",fullName);
            profileMap.put("phone",phonenum);
            profileMap.put("birthdate",setUserBirthdate);
            profileMap.put("status",setStatus);

            loadingBar.show();

            reference.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                loadingBar.dismiss();
                                goToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String massage = task.getException().toString().trim();
                                Toast.makeText(SettingsActivity.this, "Error :" + massage, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }

    public void goToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private boolean validateFullname() {
        String val = fullname.getText().toString().trim();

        if (val.isEmpty()) {
            fullname.setError("Field can not be empty");
            return false;
        } else {
            fullname.setError(null);
            return true;
        }
    }

    private boolean validateUsername() {
        String val = username.getText().toString().trim();
        String checkSpaces = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            username.setError("Field can not be empty");
            return false;
        } else if (val.length() > 20) {
            username.setError("Username is too large");
            return false;
        } else if (!val.matches(checkSpaces)) {
            username.setError("No white spaces");
            return false;
        } else {
            username.setError(null);

            return true;
        }
    }

    private boolean validatePhoneNumber() {
        String phonenum = phoneNum.getText().toString().trim();
        if (phonenum.isEmpty()) {
            phoneNum.setError("Field can't be empty");
            return false;
        } else {
            phoneNum.setError(null);
            return true;
        }
    }

    private void retriveUserInfo() {

        final ProgressDialog _loadingbar;
        _loadingbar = new ProgressDialog(this);
        _loadingbar.setTitle("Collecting info");
        _loadingbar.setMessage("Please wait...");
        _loadingbar.show();

        reference.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("username")
                                && dataSnapshot.hasChild("phone") && dataSnapshot.hasChild("birthdate")
                                && dataSnapshot.hasChild("status")){

                            String _fullname = dataSnapshot.child("name").getValue().toString();
                            String _username = dataSnapshot.child("username").getValue().toString();
                            String _phonenum = dataSnapshot.child("phone").getValue().toString();
                            String _birthdate = dataSnapshot.child("birthdate").getValue().toString();
                            String _status = dataSnapshot.child("status").getValue().toString();

                            StorageReference profilePicRef = userProfilePicRef.child(currentUserID+".jpg");

                            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(userProfilePic);

                                }
                            });

                            fullname.setText(_fullname);
                            username.setText(_username);
                            phoneNum.setText(_phonenum);
                            birthdate.setText(_birthdate);
                            status.setText(_status);

                            _loadingbar.dismiss();




                        }
                        else
                            Toast.makeText(SettingsActivity.this, "Please update information", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }



}