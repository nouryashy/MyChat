package com.example.mychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.model.MessageItem;
import com.example.mychat.ui.ChatRoomActivity;
import com.example.mychat.utils.FactoryMethods;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRoomAdapter extends FirestoreRecyclerAdapter<MessageItem, ChatRoomAdapter.ChatViewHolder> {
    public static final int VIEW_TYPE_MESSAGE_SEND = 1;
    public static final int VIEW_TYPE_MESSAGE_RESIVED = 2;
    private Context mContext;
    private String mCurrentUserID;

    public ChatRoomAdapter(Context context, @NonNull FirestoreRecyclerOptions<MessageItem> options, String currentUserID) {
        super(options);
        mContext = context;
        mCurrentUserID = currentUserID;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomAdapter.ChatViewHolder chatViewHolder, int i, @NonNull MessageItem messageItem) {
        chatViewHolder.bindChatRoom(messageItem);
    }

    @Override
    public int getItemViewType(int position) {
        MessageItem message = getItem(position);
        if (mCurrentUserID.equals(message.getSenderId())) {
            return VIEW_TYPE_MESSAGE_SEND;
        } else {
            return VIEW_TYPE_MESSAGE_RESIVED;
        }
    }

    @NonNull
    @Override
    public ChatRoomAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SEND) {
            View messageView = LayoutInflater.from(mContext).inflate(R.layout.send_message_item, parent, false);
            return new ChatViewHolder(messageView);

        } else {
            View messageView = LayoutInflater.from(mContext).inflate(R.layout.recieved_message_item, parent, false);
            return new ChatViewHolder(messageView);

        }

    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_message_body)
        TextView mMessageBody;
        @BindView(R.id.text_message_time)
        TextView mMessageTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindChatRoom(MessageItem messageItem) {
            mMessageBody.setText(messageItem.getMessage());
            mMessageTime.setText(FactoryMethods.getDate(messageItem.getMessageDate()));
        }
    }
}
