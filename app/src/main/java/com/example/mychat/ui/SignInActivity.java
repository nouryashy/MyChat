package com.example.mychat.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.model.User;
import com.example.mychat.utils.Constant;
import com.example.mychat.utils.FactoryMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = SignInActivity.class.getSimpleName();

    @BindView(R.id.sign_in_constraint_layout)
    ConstraintLayout mConstraintLayout;
    @BindView(R.id.name_edit_text)
    EditText mNameET;
    @BindView(R.id.sign_in_button)
    Button mSignInButton;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private String mUserName, mUserEmail, mUserPass;
    FirebaseUser mCurrentUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        FactoryMethods.hideProgressBar(mProgressBar,mSignInButton);
            mCurrentUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        if (mCurrentUser != null) {
            starChatActivity();
        }
    }

    @OnTextChanged(value = R.id.name_edit_text, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void getUserName(CharSequence s) {
        mUserName = s.toString();
    }

    @OnTextChanged(value = R.id.email_edit_text, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void getUserEmail(CharSequence s) {
        mUserEmail = s.toString();
    }

    @OnTextChanged(value = R.id.password_edit_text, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void getUserPassword(CharSequence s) {
        mUserPass = s.toString();
    }

    @OnClick(R.id.sign_in_button)
    public void signIn() {
        if (mUserName == null) {
            Toast.makeText(SignInActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;

        }
        if (mUserName.trim().isEmpty()) {
            Toast.makeText(SignInActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            mNameET.setText("");
            return;
        }
        if (!FactoryMethods.isValiedEmail(mUserEmail)) {
            Toast.makeText(SignInActivity.this, "Please enter correct email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!FactoryMethods.isValedPassword(mUserPass)) {
            Toast.makeText(SignInActivity.this, "please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        FactoryMethods.showProgressBar(mProgressBar, mSignInButton);
        mFirebaseAuth.signInWithEmailAndPassword(mUserEmail, mUserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    starChatActivity();
                } else {
                    mFirebaseAuth.createUserWithEmailAndPassword(mUserEmail, mUserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mCurrentUser = mFirebaseAuth.getCurrentUser();
                                createNewUserInFireStore(mCurrentUser);
                            }
                        }
                    });
                }
            }
        });


    }

    private void createNewUserInFireStore(FirebaseUser currentUser) {
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(mUserName).build();
        mFirebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
        User user = new User(mUserName, currentUser.getEmail()
                , FactoryMethods.getCurrentTimeStamp(), currentUser.getUid());
        mFirebaseFirestore.collection(Constant.USERS_COLLECTION)
                .add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
         starChatActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(mConstraintLayout,"Failed to sign in, please try again!",Snackbar.LENGTH_SHORT).show();
                Log.e(TAG,"createNewUserInFirestore: "+ e);
            }
        });

    }

    private void starChatActivity() {
        Log.d(TAG, "starChatActivity: ");
        Intent intent = new Intent(SignInActivity.this, ChatHistoryActivity.class);
        finishAffinity();
        startActivity(intent);

    }
}
