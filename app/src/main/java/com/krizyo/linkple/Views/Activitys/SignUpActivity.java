package com.krizyo.linkple.Views.Activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krizyo.linkple.Models.UserModel;
import com.krizyo.linkple.R;
import com.krizyo.linkple.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    String name,mail,password;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Password Toggle
        final boolean[] isPasswordVisible = {false};

        binding.passwordText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //In Android, each EditText can have 4 drawables: start (0), top (1), end (2), bottom (3)
                final int DRAWABLE_END = 2;

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //We do this by comparing the X position of the touch (getRawX()) to the right side of the EditText
                    // minus the width of the end drawable (eye icon).
                    if (motionEvent.getRawX() >= (binding.passwordText.getRight()
                            - binding.passwordText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                        isPasswordVisible[0] = !isPasswordVisible[0];

                        if (isPasswordVisible[0]) {
                            binding.passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            binding.passwordText.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.password, 0, R.drawable.eye_on, 0);
                        } else {
                            binding.passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            binding.passwordText.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.password, 0, R.drawable.eye_off, 0);
                        }

                        //After changing input type, Android resets cursor to the start by default.
                        // Move cursor to end
                        binding.passwordText.setSelection(binding.passwordText.length());

                        return true;
                    }
                }
                return false;
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = binding.nameText.getText().toString();
                mail = binding.mailText.getText().toString();
                password = binding.passwordText.getText().toString();

                UserModel userModel = new UserModel();
                userModel.setName(name);
                userModel.setMail(mail);
                userModel.setPassword(password);

                if (!(mail.isEmpty()) && mail.equals(null) && !(password.isEmpty()) && password.equals(null) && !(name.isEmpty()) && name.equals(null)){

                    auth.createUserWithEmailAndPassword(mail,password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        db.collection("Users")
                                                .document()
                                                .set(userModel)
                                                .addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                        finish();
                                                    }
                                                });

                                    }else {
                                        Toast.makeText(SignUpActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(SignUpActivity.this,"Strong pass: upper and lower case aplhabet + special character + number",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(SignUpActivity.this,"Enter proper field",Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

    }
}