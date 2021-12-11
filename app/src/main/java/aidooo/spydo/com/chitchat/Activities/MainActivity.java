package aidooo.spydo.com.chitchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import aidooo.spydo.com.chitchat.Adapters.TabsAccessorsAdapter;
import aidooo.spydo.com.chitchat.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private ViewPager myViewPager;
    private TabLayout my_tabLayout;
    private TabsAccessorsAdapter myTabsAccessorsAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.mainpage_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("ChitChat");

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorsAdapter = new TabsAccessorsAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        myViewPager.setAdapter(myTabsAccessorsAdapter);

        my_tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        my_tabLayout.setupWithViewPager(myViewPager);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLoginActivity();
        }
        else {
            verifyUserExistance();
        }
    }

    private void verifyUserExistance() {
       final String currentUserId= currentUser.getUid();
       reference.child("Users").orderByChild(currentUserId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child(currentUserId).child("name").exists())){

                }else {
                    sendUserToSettingsActivity();
                }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

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
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_find_friend_btn){

        }
        if (item.getItemId() == R.id.main_Crreat_group_btn){
            createNewGroup();
        }
        if (item.getItemId() == R.id.main_settings_btn){
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
            finish();
        }
        if (item.getItemId() == R.id.main_logout_btn){

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

    private void createNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");
        final EditText groupFieldName = new EditText(this);
        groupFieldName.setHint("e.g BracuCSE");
        builder.setView(groupFieldName);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupFieldName.getText().toString();

                if (TextUtils.isEmpty(groupName)) {
                    groupFieldName.setError("Field Can't be empty");
                }
                else {
                    requestCreateNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void requestCreateNewGroup(String groupName) {

        final ProgressDialog _loadingbar;
        _loadingbar = new ProgressDialog(this);
        _loadingbar.setTitle("Creating group");
        _loadingbar.setMessage("Please wait...");
        _loadingbar.show();

        reference.child("groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Group has been created", Toast.LENGTH_SHORT).show();
                            _loadingbar.dismiss();
                        }
                        else{
                            String massage = task.getException().toString();
                            Toast.makeText(MainActivity.this, "Error: "+massage, Toast.LENGTH_SHORT).show();
                            _loadingbar.dismiss();
                        }
                    }
                });
    }

    private void sendUserToSettingsActivity() {
        Intent SettingsIntent = new Intent(getApplicationContext(),SettingsActivity.class);
        SettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SettingsIntent);
        finish();
    }

}