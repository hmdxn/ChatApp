package com.tronography.locationchat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tronography.locationchat.model.MessageModel;

import java.util.ArrayList;
import java.util.Objects;

import static com.tronography.locationchat.ui.ChatRoomActivity.MY_USER_KEY;

/**
 * Created by jonathancolon on 7/31/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<MessageModel> chatLog = new ArrayList<>();
    Listener listener;

    public MyAdapter(Listener listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_chat_rv,
                parent, false);

        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MessageModel message = chatLog.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return chatLog.size();
    }

    public void setData(ArrayList<MessageModel> chatLog){
        this.chatLog = chatLog;
        notifyDataSetChanged();
    }

    public interface Listener {
        void onMessageLongClicked(MessageModel message);
        void onMessageClicked(MessageModel message);
    }

//  _______________________________________VIEWHOLDER_______________________________________________

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTV, timestampTV, usernameTV;

        MyViewHolder(View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_tv);
            timestampTV = itemView.findViewById(R.id.timestamp_tv);
            usernameTV = itemView.findViewById(R.id.username_tv);
        }

        void bind(final MessageModel messageModel) {
            messageTV.setText(messageModel.getMessage());
            timestampTV.setText(messageModel.getTimeStamp());
            usernameTV.setText(messageModel.getUsername());

            if (Objects.equals(messageModel.getSenderId(), MY_USER_KEY)){
                usernameTV.setTextColor(Color.parseColor("#37879A"));
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
