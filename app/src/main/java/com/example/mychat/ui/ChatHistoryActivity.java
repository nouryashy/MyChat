package com.example.mychat.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mychat.R;
import com.example.mychat.adapter.ChatHistoryAdapter;
import com.example.mychat.model.ChatHistory;
import com.example.mychat.model.ChatMetaData;
import com.example.mychat.utils.Constant;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;

import java.util.ArrayList;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatHistoryActivity extends AppCompatActivity {

    public static final String TAG = ChatHistoryActivity.class.getSimpleName();

    @BindView(R.id.chat_history_rec_view)
    RecyclerView mChatHistoryRecView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.empty_text_view)
    TextView mEmptyTV;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;
    private ArrayList<ChatHistory> mChatHistories;
    private ArrayList<ChatMetaData> mChatMetaData;
    private ArrayList<String> mChatRoomIDs;
    private ChatHistoryAdapter mChatHistoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        mChatHistories = new ArrayList<>();
        mChatMetaData = new ArrayList<>();
        mChatRoomIDs = new ArrayList<>();

        getSupportActionBar().setTitle("Welcome " + mCurrentUser.getDisplayName());
        mProgressBar.setVisibility(View.VISIBLE);
        mEmptyTV.setVisibility(View.GONE);


        mFirestore.collection(Constant.CHAT_COLLECTION).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onCreate: " + e.getLocalizedMessage());
                    return;
                }
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    if (documentSnapshot.getId().contains(mCurrentUser.getUid())) {
                        if (!mChatRoomIDs.contains(mCurrentUser.getUid())) {
                            mChatRoomIDs.add(documentSnapshot.getId());
                            String recipientID = documentSnapshot.getId().replace("-", "")
                                    .replace(mCurrentUser.getUid(), "");
                            Log.d(TAG, "recipientID:" + recipientID);
                            mFirestore.collection(Constant.CHAT_COLLECTION)
                                    .document(documentSnapshot.getId())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                            ChatHistory chatHistory = documentSnapshot.toObject(ChatHistory.class);
                                            mChatHistories.add(chatHistory);
                                            mChatHistoryAdapter.notifyDataSetChanged();
                                        }
                                    });
                            mFirestore.collection(Constant.CHAT_COLLECTION).document(documentSnapshot.getId())
                                    .collection(Constant.CHAT_META_DATA).document(recipientID)
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                            ChatMetaData chatMetaData = documentSnapshot.toObject(ChatMetaData.class);
                                            mChatMetaData.add(chatMetaData);
                                            mChatHistoryAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                }
                mChatHistoryAdapter = new ChatHistoryAdapter(ChatHistoryActivity.this, mChatMetaData, mChatHistories, new ChatHistoryAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ChatMetaData chatMetaData) {
                        Intent intent = new Intent(ChatHistoryActivity.this, ChatRoomActivity.class);
                        intent.putExtra(Constant.FROM, ChatHistoryActivity.class.getSimpleName());
                        intent.putExtra(Constant.SELECTED_RECIPIENT_OBJECT, chatMetaData);
                        startActivity(intent);
                    }
                });
                mChatHistoryRecView.setLayoutManager(new LinearLayoutManager(ChatHistoryActivity.this));
                mChatHistoryRecView.setHasFixedSize(true);
                mChatHistoryRecView.setAdapter(mChatHistoryAdapter);
                Log.d(TAG, "mChatMetaData: " + mChatMetaData.size());
                Log.d(TAG, "mChatHistories: " + mChatHistories.size());

                Log.d(TAG, "onCreate: " + mChatRoomIDs.size());
                mProgressBar.setVisibility(View.GONE);
                if (mChatRoomIDs.size() == 0) {
                    mEmptyTV.setVisibility(View.VISIBLE);
                    mEmptyTV.setText("No recipients");
                }

            }
        });

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
    public void openRecipientActivity() {
        Intent intent = new Intent(this, RecipientsActivity.class);
        startActivity(intent);
    }


}
