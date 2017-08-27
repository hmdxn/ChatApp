package com.tronography.locationchat.chatroom;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tronography.locationchat.R;
import com.tronography.locationchat.model.MessageModel;

import java.util.ArrayList;
import java.util.Objects;

import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_KEY;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    ArrayList<MessageModel> messageLog = new ArrayList<>();
    Listener listener;

    public MessageAdapter(Listener listener) {
        this.listener = listener;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_message_rv,
                parent, false);

        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageModel message = messageLog.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageLog.size();
    }

    public void setData(ArrayList<MessageModel> chatLog){
        this.messageLog = chatLog;
        notifyDataSetChanged();
    }

    public interface Listener {
        void onMessageLongClicked(MessageModel message);
        void onMessageClicked(MessageModel message);
    }

//  _______________________________________VIEWHOLDER_______________________________________________

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTV, timestampTV, usernameTV;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_tv);
            timestampTV = itemView.findViewById(R.id.timestamp_tv);
            usernameTV = itemView.findViewById(R.id.username_tv);
        }

        void bind(final MessageModel messageModel) {
            messageTV.setText(messageModel.getMessage());
            timestampTV.setText(messageModel.getTimeStamp());
            usernameTV.setText(messageModel.getSenderName());

            if (Objects.equals(messageModel.getSenderId(), CURRENT_USER_KEY)){
                usernameTV.setTextColor(Color.parseColor("#DB6B71"));
            } else {
                usernameTV.setTextColor(Color.parseColor("#FF424242"));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMessageClicked(messageModel);
                }
            });

            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onMessageLongClicked(messageModel);
                    return true;
                }
            });
        }
    }
}
