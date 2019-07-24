package com.example.mychat.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.mychat.R;
import com.example.mychat.adapter.ChatRoomAdapter;
import com.example.mychat.model.ChatMetaData;
import com.example.mychat.model.MessageItem;
import com.example.mychat.model.User;
import com.example.mychat.utils.Constant;
import com.example.mychat.utils.FactoryMethods;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ChatRoomActivity extends AppCompatActivity {
    public static final String TAG = ChatRoomActivity.class.getSimpleName();

    @BindView(R.id.chat_room_rec_view)
    RecyclerView mChatRoomRecView;
    @BindView(R.id.send_message_button)
    ImageButton mSendMessageButton;
    @BindView(R.id.message_edit_text)
    EditText mMessageET;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;

    private User mRecipient;
    private ChatMetaData mChatMetaData;
    private ChatRoomAdapter mChatRoomAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    private String mChatRoomID, mMessage, mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ButterKnife.bind(this);

        mFrom = getIntent().getStringExtra(Constant.FROM);
        switch (mFrom) {
            case "ChatHistoryActivity":
                mChatMetaData = getIntent().getParcelableExtra(Constant.SELECTED_RECIPIENT_OBJECT);
                break;
            case "RecipientsActivity":
                mRecipient = getIntent().getParcelableExtra(Constant.SELECTED_RECIPIENT_OBJECT);
                break;
        }
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        if (mRecipient != null) {
            getSupportActionBar().setTitle(mRecipient.getUserUID());
        } else {
            getSupportActionBar().setTitle(mChatMetaData.getUserUID());
        }
        if (mRecipient != null) {
            createChatRoomID(mRecipient.getUserUID());
        } else {
            createChatRoomID(mChatMetaData.getUserUID());
        }
        Query query = mFirestore.collection(Constant.CHAT_COLLECTION).document(mChatRoomID)
                .collection(Constant.MESSAGES_COLLECTION).orderBy("messageDate", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<MessageItem> options = new FirestoreRecyclerOptions.Builder<MessageItem>()
                .setQuery(query, MessageItem.class)
                .setLifecycleOwner(this)
                .build();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mChatRoomRecView.setLayoutManager(mLinearLayoutManager);
        mChatRoomRecView.setHasFixedSize(true);
        mChatRoomAdapter = new ChatRoomAdapter(this, options, mCurrentUser.getUid());
        mChatRoomAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mChatRoomAdapter.getItemCount();
                int lastVarabilePosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if (lastVarabilePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVarabilePosition == (positionStart - 1))) {
                    mChatRoomRecView.scrollToPosition(positionStart);
                }

            }
        });
        mChatRoomRecView.setAdapter(mChatRoomAdapter);
    }

    private void createChatRoomID(String userUID) {
        if (mCurrentUser.getUid().compareTo(userUID) > 0) {
            mChatRoomID = mCurrentUser.getUid() + "-" + userUID;

        } else {
            mChatRoomID = userUID + "-" + mCurrentUser.getUid();
        }


    }

    @OnTextChanged(value = R.id.message_edit_text, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void getMessage(CharSequence s) {
        if (s.toString().trim().isEmpty()) {
            mSendMessageButton.setEnabled(false);
        } else {
            mSendMessageButton.setEnabled(true);
            mMessage = s.toString();
        }
    }

    @OnClick(R.id.send_message_button)
    public void sendMessage() {
        if (mMessage != null) {
            if (!mMessage.trim().isEmpty()) {
                mMessageET.setText("");
                long currentDateandTime = FactoryMethods.getCurrentTimeStamp();
                MessageItem newMessage = new MessageItem(mMessage, mCurrentUser.getUid(), currentDateandTime);
                mFirestore.collection(Constant.CHAT_COLLECTION).document(mChatRoomID)
                        .collection(Constant.MESSAGES_COLLECTION).add(newMessage)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Message sent successfully: ");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "sendMessage: " + e.getLocalizedMessage());
                    }
                });
                HashMap<String, Object> updateMessageMetaDate = new HashMap<>();
                updateMessageMetaDate.put(Constant.LAST_MESSAGE_DATE_DOC, currentDateandTime);
                updateMessageMetaDate.put(Constant.LAST_MESSAGE_DOC, mMessage);
                mFirestore.collection(Constant.CHAT_COLLECTION).document(mChatRoomID)
                        .set(updateMessageMetaDate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating document" + e.getLocalizedMessage());
                    }
                });
                if (mCurrentUser != null) {
                    User currentUser = new User(mCurrentUser.getDisplayName(), mCurrentUser.getEmail(), mCurrentUser.getUid());
                    mFirestore.collection(Constant.CHAT_COLLECTION).document(mChatRoomID).collection(Constant.CHAT_META_DATA)
                            .document(mCurrentUser.getUid())
                            .set(currentUser, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error updating document" + e.getLocalizedMessage());

                        }
                    });
                }
                if(mRecipient!=null){
                    updateRecipientMetaData(mRecipient.getUserName(),mRecipient.getUserEmail(),mRecipient.getUserUID());
                }else{
                    updateRecipientMetaData(mChatMetaData.getUserName(),mChatMetaData.getUserEmail(),mChatMetaData.getUserUID());
                }

            }
        }
    }

    private void updateRecipientMetaData(String userName, String userEmail, String userUID) {
User recipientUser=new User(userName,userEmail,userUID);
mFirestore.collection(Constant.CHAT_COLLECTION).document(mChatRoomID).collection(Constant.CHAT_META_DATA)
        .document(recipientUser.getUserUID()).set(recipientUser,SetOptions.merge())
        .addOnSuccessListener(new OnSuccessListener<Void>() {
    @Override
    public void onSuccess(Void aVoid) {
        Log.d(TAG, "DocumentSnapshot successfully updated!");
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Error updating document" + e.getLocalizedMessage());
    }
});
    }

}
