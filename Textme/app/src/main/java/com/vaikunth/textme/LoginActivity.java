package com.vaikunth.textme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edtEmail,edtPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    ProgressDialog pg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtEmail=findViewById(R.id.edtEmailLogin);
        edtPassword=findViewById(R.id.edtPasswordLogin);
        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        mAuth=FirebaseAuth.getInstance();
        pg=new ProgressDialog(LoginActivity.this);
        pg.setTitle("Authentication");
        pg.setMessage("Please Wait until Authentication finishes");
    }

    @Override
    public void onClick(View v) {
        if(edtEmail.getText().toString().equals("")||
        edtPassword.getText().toString().equals(""))
        {
            Toast.makeText(LoginActivity.this,"All fields are required",Toast.LENGTH_SHORT)
                    .show();
        }
        else
        {
            pg.show();
            mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),
                    edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this,"Successfully Logged In",Toast.LENGTH_SHORT).show();
                        pg.dismiss();
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Error"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                        pg.dismiss();
                    }
                }
            });
        }
    }
}