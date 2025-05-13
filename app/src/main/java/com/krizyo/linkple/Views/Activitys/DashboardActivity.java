package com.krizyo.linkple.Views.Activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.krizyo.linkple.R;
import com.krizyo.linkple.databinding.ActivityDashboardBinding;
import com.krizyo.linkple.databinding.JoinMeetingDialogBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class DashboardActivity extends AppCompatActivity {
    ActivityDashboardBinding binding;
    FirebaseAuth auth;
    AlertDialog joinMeetingDialog;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setActionBar(binding.toolbar);
        getActionBar().setTitle("");


        auth = FirebaseAuth.getInstance();

        View view = LayoutInflater.from(DashboardActivity.this).inflate(R.layout.join_meeting_dialog,null);

        joinMeetingDialog = new AlertDialog.Builder(DashboardActivity.this).create();
        joinMeetingDialog.setView(view);
        joinMeetingDialog.setCancelable(false);
        joinMeetingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //onBackPressed for dialog, when dialog is open
        joinMeetingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    finish();
                    return true;
                }
                return false;
            }
        });


        joinMeetingDialog.show();

        ImageButton logout = view.findViewById(R.id.logoutBtn);
        EditText secretCode = view.findViewById(R.id.secretCodeText);
        CardView join = view.findViewById(R.id.joinBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
                finish();
            }
        });

        //setup sever url and option to  default jitsi option
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");

            JitsiMeetConferenceOptions defaultOptions = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    .setFeatureFlag("welcomepage.enabled", false)
                    .build();

            JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(secretCode.getText().toString())
                        .build();

                JitsiMeetActivity.launch(DashboardActivity.this,options);
            }
        });

    }
}