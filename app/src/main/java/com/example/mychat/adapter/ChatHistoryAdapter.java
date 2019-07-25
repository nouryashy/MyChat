package com.example.mychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mychat.R;
import com.example.mychat.model.ChatHistory;
import com.example.mychat.model.ChatMetaData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder> {
    private Context mContext;
    private ArrayList<ChatMetaData> mChatMetaData;
    private ArrayList<ChatHistory> mChatHistories;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(ChatMetaData chatMetaData);
    }

    public ChatHistoryAdapter(Context context, ArrayList<ChatMetaData> chatMetaData,
                              ArrayList<ChatHistory> chatHistories, OnItemClickListener listener) {
        mContext = context;
        mChatMetaData = chatMetaData;
        mChatHistories = chatHistories;
        mListener = listener;

    }

    @NonNull
    @Override
    public ChatHistoryAdapter.ChatHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.chat_list_item, parent, false);

        return new ChatHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHistoryAdapter.ChatHistoryViewHolder holder, int position) {
        holder.bindChatHis(mChatMetaData.get(position), mChatHistories.get(position), mListener);

    }

    @Override
    public int getItemCount() {
        return mChatMetaData.size();
    }

    public class ChatHistoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chat_history_icon_image_view)
        ImageView mChatHistoryIV;
        @BindView(R.id.employee_name_text_view)
        TextView mEmployeeNameTV;
        @BindView(R.id.sub_title_text_view)
        TextView mLastMessageTV;

        public ChatHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        public void bindChatHis(ChatMetaData chatMetaData, ChatHistory chatHistory, OnItemClickListener listener) {
            Glide.with(mContext)
                    .load("")
                    .apply(RequestOptions.circleCropTransform().apply(RequestOptions.placeholderOf(R.drawable.ic_profile_image_placeholder)))
                    .apply(RequestOptions.circleCropTransform())
                    .into(mChatHistoryIV);
            mEmployeeNameTV.setText(chatMetaData.getUserName());
            mLastMessageTV.setVisibility(View.VISIBLE);
            mLastMessageTV.setText(chatHistory.getLastMessage());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(chatMetaData);
                }
            });


        }
    }

}
