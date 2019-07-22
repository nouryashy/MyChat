package com.example.mychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mychat.R;
import com.example.mychat.model.User;
import com.example.mychat.utils.FactoryMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.RecipientViewHolder> {
    private Context mContext;
    private ArrayList<User> mUsers;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void OnItemClick(User user);
    }

    public RecipientAdapter(Context context,ArrayList<User> users,OnItemClickListener listener) {
        mUsers = users;
        mContext = context;
        mListener = listener;

    }

    @NonNull
    @Override
    public RecipientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, parent, false);

        return new RecipientViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecipientViewHolder holder, int position) {
        holder.bindUser(mUsers.get(position), mListener);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class RecipientViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chat_history_icon_image_view)
        ImageView mUserIconIV;
        @BindView(R.id.employee_name_text_view)
        TextView mUserNameTV;
        @BindView(R.id.sub_title_text_view)
        TextView mUserCreateDateTV;
        public RecipientViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindUser(User user, OnItemClickListener listener) {
            Glide.with(mContext)
                    .load("")
                    .apply(RequestOptions.circleCropTransform().apply(RequestOptions.placeholderOf(R.drawable.ic_profile_image_placeholder)))
                    .apply(RequestOptions.circleCropTransform())
                    .into(mUserIconIV);
mUserNameTV.setText(user.getUserName());
mUserCreateDateTV.setText(FactoryMethods.getDate(user.getUserCreateDate()));
itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
listener.OnItemClick(user);
    }
});

        }
    }
}
