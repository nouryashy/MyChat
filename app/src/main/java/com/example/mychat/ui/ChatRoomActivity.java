package com.example.mychat.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.mychat.R;
import com.example.mychat.adapter.ChatRoomAdapter;
import com.example.mychat.model.ChatMetaData;
import com.example.mychat.model.User;
import com.example.mychat.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

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
            case "RecipientActivity":
                mRecipient = getIntent().getParcelableExtra(Constant.SELECTED_RECIPIENT_OBJECT);
                break;
        }
        mFirebaseAuth=FirebaseAuth.getInstance();
        mFirestore= FirebaseFirestore.getInstance();
        mCurrentUser=mFirebaseAuth.getCurrentUser();
    }
}
