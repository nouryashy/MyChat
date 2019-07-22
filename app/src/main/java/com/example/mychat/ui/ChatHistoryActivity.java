package com.example.mychat.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatHistoryActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;
// ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        getSupportActionBar().setTitle("Welcome " + mCurrentUser.getDisplayName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseAuth.signOut();
                        Intent intent = new Intent(ChatHistoryActivity.this, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                };
                alertDialog("Logout", "Are you sure ? ", listener);

        }
        return super.onOptionsItemSelected(item);
    }

    private void alertDialog(String title, String message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", listener);
        alertDialog.setNegativeButton("No", null);
        alertDialog.create().show();
    }
    @OnClick(R.id.open_recipients_activity)
    public void openRecipientActivity(){
Intent intent=new Intent(this,RecipientsActivity.class);
startActivity(intent);
    }
}
