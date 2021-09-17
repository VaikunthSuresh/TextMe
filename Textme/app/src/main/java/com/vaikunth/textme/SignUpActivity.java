package com.vaikunth.textme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText edtUserName,edtEmail,edtPassword;
    private TextView txtAlreadyHaveAAccount;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtUserName=findViewById(R.id.edtUserName);
        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);
        txtAlreadyHaveAAccount=findViewById(R.id.txtAlreadyHaveAccount);
        btnRegister=findViewById(R.id.btnRegister);
        mAuth= FirebaseAuth.getInstance();
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");

        txtAlreadyHaveAAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


        ProgressDialog pg=new ProgressDialog(SignUpActivity.this);
        pg.setTitle("Authentication");
        pg.setMessage("Please Wait until,Validation finishes");
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtUserName.getText().toString().equals("")||
                edtEmail.getText().toString().equals("")||
                edtPassword.getText().toString().equals(""))
                {
                    Toast.makeText(SignUpActivity.this,"All fields are required",Toast.LENGTH_SHORT).show();
                }
                else {
                    pg.show();

                    mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        userRef.child(mAuth.getCurrentUser().getUid()).child("Name")
                                                .setValue(edtUserName.getText().toString());
                                        Toast.makeText(SignUpActivity.this, edtUserName.getText().toString() + "Successfully Registered", Toast.LENGTH_SHORT).show();
                                        pg.dismiss();
                                        Intent intent =new Intent(SignUpActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(SignUpActivity.this, "Error"+task.getException().toString(),
                                                Toast.LENGTH_SHORT).show();
                                        pg.dismiss();

                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null)
        {
            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}