package com.vaikunth.textme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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


public class SearchFragment extends Fragment {
    private ListView mListView;
    private EditText edtSearch;
    private ArrayAdapter mArrayAdapter;
    private ArrayList<String> usersArrayList;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private ArrayList<String> userIds;



    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        mListView=view.findViewById(R.id.listViewUsers);
        edtSearch=view.findViewById(R.id.edtSearch);
        usersArrayList=new ArrayList<>();
        userIds=new ArrayList<>();
        mArrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,usersArrayList);
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(),MessageActivity.class);
                intent.putExtra("ReceiverId",userIds.get(position));
                intent.putExtra("ReceiverName",usersArrayList.get(position));
                startActivity(intent);
            }
        });
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        final ProgressDialog pg=new ProgressDialog(getContext());
        pg.setTitle("Getting Users");
        pg.setMessage("Please wait,Until Progress is Done!");
        pg.show();

        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists() && !snapshot.getKey().equals(mAuth.getCurrentUser().getUid()))
                {
                    if(snapshot.child("Name").getValue()!=null) {
                        usersArrayList.add(snapshot.child("Name").getValue().toString());
                        userIds.add(snapshot.getKey());
                    }
                }
                mArrayAdapter.notifyDataSetChanged();
                pg.dismiss();
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
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }
    private void searchUsers(String enterTxt)
    {
        usersArrayList.clear();
        userIds.clear();
        mArrayAdapter.notifyDataSetChanged();
        userRef.orderByChild("Name").startAt(enterTxt).endAt(enterTxt+"\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersArrayList.clear();
                        userIds.clear();
                        if(snapshot.exists())
                        {
                            for(DataSnapshot userSnap:snapshot.getChildren())
                            {
                                if(!userSnap.getKey().equals(mAuth.getCurrentUser().getUid()))
                                {
                                    usersArrayList.add(userSnap.child("Name").getValue().toString());
                                    userIds.add(userSnap.getKey());
                                }
                            }

                            mArrayAdapter.notifyDataSetChanged();
                           // pg.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}