package com.vaikunth.textme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatListFragment extends Fragment {
    private ListView mListView;
    private ArrayAdapter mArrayAdapter;
    private ArrayList<String> nameList,idsList;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;



    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat_list, container, false);
        mListView=view.findViewById(R.id.ListViewChatList);
        nameList=new ArrayList<>();
        mArrayAdapter=new ArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1,nameList);
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(),MessageActivity.class);
                intent.putExtra("ReceiverId",idsList.get(position));
                intent.putExtra("ReceiverName",nameList.get(position));
                startActivity(intent);

            }
        });
        idsList=new ArrayList<>();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        userRef.child(mAuth.getCurrentUser().getUid()).child("ChatLists").orderByValue()
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.exists())
                        {
                            String userNameId = snapshot.getKey();
                            userRef.child(userNameId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    if(snapshot2.exists())
                                    {
                                        nameList.add(0,snapshot2.child("Name").getValue().toString());
                                        idsList.add(0,snapshot2.getKey());
                                        mArrayAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.exists()) {
                            final String userNameId = snapshot.getKey();
                            userRef.child(userNameId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    if (snapshot2.exists()) {
                                        int index=idsList.indexOf(userNameId);
                                        nameList.remove(index);
                                        idsList.remove(index);
                                        nameList.add(0, snapshot2.child("Name").getValue().toString());
                                        idsList.add(0, snapshot2.getKey());
                                        mArrayAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
        return view;
    }
}