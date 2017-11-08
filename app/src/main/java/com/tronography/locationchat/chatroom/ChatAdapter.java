package com.tronography.locationchat.chatroom;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tronography.locationchat.R;
import com.tronography.locationchat.model.Message;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.tronography.locationchat.utils.SharedPrefsUtils.*;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private ArrayList<Message> messageLog = new ArrayList<>();
    private Listener listener;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    ChatAdapter(Listener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        System.out.println("viewType = " + viewType);

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.itemview_message_sent, parent, false);
            return new MessageSentViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.itemview_message_received, parent, false);
            return new MessageReceivedViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageLog.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_RECEIVED:
                System.out.println("VIEW_TYPE_MESSAGE_RECEIVED = " + VIEW_TYPE_MESSAGE_RECEIVED);
                ((MessageReceivedViewHolder) holder).bind(message, position);
                break;
            case VIEW_TYPE_MESSAGE_SENT:
                System.out.println("VIEW_TYPE_MESSAGE_SENT = " + VIEW_TYPE_MESSAGE_SENT);
                ((MessageSentViewHolder) holder).bind(message, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageLog.size();
    }

    @Override
    public int getItemViewType(int position) {
        String messageId = messageLog.get(position).getSenderId();
        if (Objects.equals(messageId, CURRENT_USER_ID)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    public void setData(ArrayList<Message> chatLog) {
        this.messageLog = chatLog;
        notifyDataSetChanged();
    }

    interface Listener {
        void onMessageLongClicked(Message message);

        void onMessageClicked(Message message);
    }


    class MessageReceivedViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = MessageReceivedViewHolder.class.getSimpleName();
        @Bind(R.id.timestamp_tv)
        TextView timestampTV;
        @Bind(R.id.message_tv)
        TextView messageTV;
        @Bind(R.id.username_tv)
        TextView usernameTV;

        MessageReceivedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Message message, int position) {
            messageTV.setText(message.getMessage());
            timestampTV.setText(message.getTimeStamp());
            usernameTV.setText(message.getSenderName());

            if (position - 1 >= 0) {
                if (messageLog.get(position - 1).getSenderId().equals(message.getSenderId())) {
                    usernameTV.setVisibility(View.GONE);
                } else {
                    usernameTV.setVisibility(View.VISIBLE);
                }
            }
            Log.i(TAG, "bind: RECEIVED CALLED");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMessageClicked(message);
                }
            });

            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onMessageLongClicked(message);
                    return true;
                }
            });
        }
    }

    class MessageSentViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = MessageSentViewHolder.class.getSimpleName();
        @Bind(R.id.timestamp_tv)
        TextView timestampTV;
        @Bind(R.id.message_tv)
        TextView messageTV;

        MessageSentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Message message, int position) {
            messageTV.setText(message.getMessage());
            timestampTV.setText(message.getTimeStamp());

            Log.i(TAG, "bind: SENT CALLED");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMessageClicked(message);
                }
            });

            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onMessageLongClicked(message);
                    return true;
                }
            });
        }
    }
}
