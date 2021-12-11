package aidooo.spydo.com.chitchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import aidooo.spydo.com.chitchat.R;


public class GroupChatActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ImageButton sendMassageButton;
    private EditText userTextInput;
    private ScrollView scrollView;
    private TextView displayUserText;
    private FirebaseAuth userAuth;
    private DatabaseReference userRef,groupNameRef,groupMassageKeyRef;

    String currentGroupName,currentUserID,currentUserName,currentDate,currentTime;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("Group_Name").toString();

        userAuth = FirebaseAuth.getInstance();
        currentUserID= userAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("groups").child(currentGroupName);

        initializeFields();
        getUserInfo();

        sendMassageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMassageInfoToDatabase();
                userTextInput.setText("");
                scrollView.fullScroll(scrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    if (dataSnapshot.exists()){
                        dispalyMassages(dataSnapshot);
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                if (dataSnapshot.exists()){
                    dispalyMassages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initializeFields() {
        mtoolbar = (Toolbar) findViewById(R.id.group_chatBar_layout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(currentGroupName);
        sendMassageButton = findViewById(R.id.send_massage_button);
        userTextInput = findViewById(R.id.input_group_massages);
        scrollView = findViewById(R.id.my_scroll_view);
        displayUserText = findViewById(R.id.group_chat_text_display);
    }

    private void getUserInfo() {

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        currentUserName = dataSnapshot.child("name").getValue().toString();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMassageInfoToDatabase() {
        validateFields();
        if (!validateFields()){
            return;
        }

        String massageKey = groupNameRef.push().getKey();
        String massage = userTextInput.getText().toString().trim();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormate = new SimpleDateFormat("dd MMM, yyyy");
        currentDate = currentDateFormate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormate = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormate.format(calForTime.getTime());

        HashMap<String,Object> groupMassageKey = new HashMap<>();
        groupNameRef.updateChildren(groupMassageKey);

        groupMassageKeyRef = groupNameRef.child(massageKey);

        HashMap<String, Object> massageInfoMap = new HashMap<>();

        massageInfoMap.put("name",currentUserName);
        massageInfoMap.put("massage",massage);
        massageInfoMap.put("date",currentDate);
        massageInfoMap.put("time",currentTime);

        groupMassageKeyRef.updateChildren(massageInfoMap);


    }

    private boolean validateFields() {
        String massage = userTextInput.getText().toString().trim();
        if (massage.isEmpty()){
            userTextInput.setError("Please write massage");
            return false;
        }else{
            return true;
        }
    }

    private void dispalyMassages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String chatDate = (String)((DataSnapshot)iterator.next()).getValue();
            String chatMassage = (String)((DataSnapshot)iterator.next()).getValue();
            String chatName = (String)((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String)((DataSnapshot)iterator.next()).getValue();

            displayUserText.append(chatName + "\n" + "\n\t\t\t"+"   "+chatMassage + "\n\n" + chatTime +"     "  + chatDate+"\n\n\n\n");

            scrollView.fullScroll(scrollView.FOCUS_DOWN);
        }
    }


}