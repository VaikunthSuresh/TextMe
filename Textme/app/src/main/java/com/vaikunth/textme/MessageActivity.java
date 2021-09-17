package com.vaikunth.textme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {
   private Toolbar mToolBar;
    private TextView txtReceiverName,txtLastSeen;
    private String receiverName,receiverId;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef,chatRef;
    private EditText edtMessage;
    private ImageButton btnSend,btnBack;
    private RecyclerView mRecyclerView;
    private MessageAdapter mMessageAdapter;
    public static ArrayList<String> messages,messagePosition,messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
       mToolBar=findViewById(R.id.toolBarMessage);
        setSupportActionBar(mToolBar);
        txtReceiverName=findViewById(R.id.txtReceiverName);
        txtLastSeen=findViewById(R.id.lastSeen);
        Intent intent=getIntent();
        receiverId=intent.getStringExtra("ReceiverId");
        receiverName=intent.getStringExtra("ReceiverName");
        txtReceiverName.setText(receiverName);
        messages=new ArrayList<>();
        messagePosition=new ArrayList<>();
        messageId=new ArrayList<>();
        mRecyclerView=findViewById(R.id.recyclarView);
        mMessageAdapter=new MessageAdapter(MessageActivity.this,messages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        mRecyclerView.setAdapter(mMessageAdapter);
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        edtMessage=findViewById(R.id.edtMessage);
        btnSend=findViewById(R.id.btnSend);
        chatRef=FirebaseDatabase.getInstance().getReference().child("Chats");
        btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MessageActivity.this,MainActivity.class);
                startActivity(intent1);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtMessage.getText().toString().equals(""))
                {
                    Toast.makeText(MessageActivity.this, "Enter Something", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    HashMap<String,String> map=new HashMap<>();
                    map.put("SenderId",mAuth.getCurrentUser().getUid());
                    map.put("Message",edtMessage.getText().toString());
                    map.put("ReceiverId",receiverId);
                    chatRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            userRef.child(mAuth.getCurrentUser().getUid())
                                    .child("ChatLists").child(receiverId).setValue(ServerValue.TIMESTAMP);
                            userRef.child(receiverId).child("ChatLists").child(mAuth.getCurrentUser().getUid()).setValue(ServerValue.TIMESTAMP);
                            edtMessage.setText("");
                        }
                    });

                }
            }
        });
        userRef.child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChild("Status")) {
                    if (snapshot.child("Status").hasChild("Online")) {
                        txtLastSeen.setText("Online");
                    } else {
                        Object objTimeStamp = snapshot.child("Status").child("Offline").getValue();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String lastSeenString = df.format(objTimeStamp);
                        String currentDateString = df.format(new Date());
                        Date currentDate = null, lastSeenDate = null;
                        try {
                            currentDate = df.parse(currentDateString);
                            lastSeenDate = df.parse(lastSeenString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (currentDate.compareTo(lastSeenDate) == 0) {
                            String time = new SimpleDateFormat("h:mm a").format(objTimeStamp);
                            txtLastSeen.setText("last seen :" + time);
                        } else {
                            String date = new SimpleDateFormat("yyyy-MM-dd").format(objTimeStamp);
                            txtLastSeen.setText("last Seen:" + date);
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getMessages();

        }
        private void getMessages()
        {
            chatRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if(snapshot.exists() && snapshot!=null)
                    {
                        Log.i("MessageData", "onChildAdded: "+snapshot);
                        Log.i("MessageData", "onChildAdded: "+snapshot.child("ReceiverId").getValue());
                        if(snapshot.child("ReceiverId").getValue().toString().equals(mAuth.getCurrentUser().getUid())
                        && snapshot.child("SenderId").getValue().toString().equals(receiverId))
                        {
                            messages.add(snapshot.child("Message").getValue().toString());
                            messagePosition.add("0");
                            messageId.add(snapshot.getKey());
                            mMessageAdapter.notifyItemInserted(messages.size()-1);
                            mRecyclerView.smoothScrollToPosition(messages.size()-1);

                        }
                        else if(snapshot.child("ReceiverId").getValue().toString().equals(receiverId)
                                && snapshot.child("SenderId").getValue().toString().equals(mAuth.getCurrentUser().getUid()))
                        {
                            messages.add(snapshot.child("Message").getValue().toString());
                            messagePosition.add("1");
                            messageId.add(snapshot.getKey());
                            mMessageAdapter.notifyItemInserted(messages.size()-1);
                            mRecyclerView.smoothScrollToPosition(messages.size()-1);

                        }
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    }
