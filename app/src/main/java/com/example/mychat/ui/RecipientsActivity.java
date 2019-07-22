package com.example.mychat.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mychat.R;
import com.example.mychat.adapter.RecipientAdapter;
import com.example.mychat.model.User;
import com.example.mychat.utils.Constant;
import com.example.mychat.utils.FactoryMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.opencensus.tags.Tag;

public class RecipientsActivity extends AppCompatActivity {
    private static final String TAG = RecipientsActivity.class.getSimpleName();
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.empty_text_view)
    TextView mEmptyTV;
    @BindView(R.id.recipients_rec_view)
    RecyclerView mRecipientsRecView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mFirestore;
    private RecipientAdapter mRecipientAdapter;
    private ArrayList<User> mUsers;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mUsers = new ArrayList<>();
        mFirestore.collection(Constant.USERS_COLLECTION)
                .orderBy(Constant.USER_CREATE_DATE_DOC, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyTV.setVisibility(View.GONE);
                    for (DocumentSnapshot userDoc : Objects.requireNonNull(task.getResult())) {
                        User user = userDoc.toObject(User.class);
                        if (!user.getUserUID().equals(mCurrentUser.getUid())) {
                            mUsers.add(user);
                        }
                    }
                    mRecipientAdapter = new RecipientAdapter(RecipientsActivity.this, mUsers,
                            new RecipientAdapter.OnItemClickListener() {
                                @Override
                                public void OnItemClick(User user) {
                                    Intent intent = new Intent(RecipientsActivity.this, ChatRoomActivity.class);
                                    intent.putExtra(Constant.FROM, RecipientsActivity.class.getSimpleName());
                                    intent.putExtra(Constant.SELECTED_RECIPIENT_OBJECT, user);
                                    startActivity(intent);
                                }

                            });
                    mRecipientsRecView.setLayoutManager(new LinearLayoutManager(RecipientsActivity.this));
                    mRecipientsRecView.setHasFixedSize(true);
                    mRecipientsRecView.setAdapter(mRecipientAdapter);
                    Log.d(TAG, "OnCreate" + mUsers.size());

                 if (mUsers.size()==0){
mProgressBar.setVisibility(View.GONE);
mEmptyTV.setVisibility(View.VISIBLE);
mEmptyTV.setText("No recipients");
                 }

                }else {
                    Log.e(TAG, "onCreate: " + task.getException());
                }
            }
        });

    }
}
