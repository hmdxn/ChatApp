package com.tronography.locationchat.chatroom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tronography.locationchat.R;
import com.tronography.locationchat.model.Message;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_ID;

public class ChatroomAdapter extends RecyclerView.Adapter {

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private ArrayList<Message> messageLog = new ArrayList<>();
    private Listener listener;

    // Allows adapter to remember the last item shown on screen
    private int lastPosition = -1;

    ChatroomAdapter(Listener listener) {
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
        @BindView(R.id.timestamp_tv)
        TextView timestampTV;
        @BindView(R.id.message_tv)
        TextView messageTV;
        @BindView(R.id.profile_thumbnail_iv)
        ImageView profileImage;

        private Context context;

        MessageReceivedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        void bind(final Message message, int position) {
            messageTV.setText(message.getMessage());
            timestampTV.setText(message.getTimeStamp());

            loadProfileImage(message, position);

            Log.i(TAG, "bind: RECEIVED CALLED");

            itemView.setOnClickListener(view -> listener.onMessageClicked(message));

            itemView.setOnLongClickListener(view -> {
                listener.onMessageLongClicked(message);
                return true;
            });
        }

        void loadProfileImage(Message message, int position) {
            if (position == 0) {
                if (messageLog.get(position).getSenderId().equals(message.getSenderId())) {

                    // Create a reference with an initial file path and name
                    StorageReference pathReference = storageRef.child(message.getSenderId() + "_profile_img");

                    pathReference.getDownloadUrl()
                            .addOnSuccessListener(uri ->
                                    Glide.with(context)
                                            .load(uri)
                                            .apply(RequestOptions.circleCropTransform()
                                                    .placeholder(new ColorDrawable(Color.LTGRAY))
                                                    .fallback(new ColorDrawable(Color.LTGRAY))
                                            ).into(profileImage))
                            .addOnFailureListener(e -> Log.e(TAG, "onFailure: " + e.getMessage()));

                    profileImage.setVisibility(View.VISIBLE);
                }
            }

            if (position - 1 >= 0) {
                if (messageLog.get(position - 1).getSenderId().equals(message.getSenderId())) {
                    profileImage.setVisibility(View.INVISIBLE);
                } else {
                    // Create a reference with an initial file path and name
                    StorageReference pathReference = storageRef.child(message.getSenderId() + "_profile_img");

                    pathReference.getDownloadUrl()
                            .addOnSuccessListener(uri ->
                                    Glide.with(context)
                                            .load(uri)
                                            .apply(RequestOptions.fitCenterTransform()
                                                    .circleCrop()
                                                    .placeholder(new ColorDrawable(Color.LTGRAY))
                                                    .fallback(new ColorDrawable(Color.LTGRAY))
                                            ).into(profileImage))
                            .addOnFailureListener(e ->
                                    Glide.with(context)
                                    .load(new ColorDrawable(Color.BLACK))
                                            .apply(RequestOptions.fitCenterTransform()
                                                    .circleCrop()
                                                    .placeholder(new ColorDrawable(Color.LTGRAY))
                                                    .fallback(new ColorDrawable(Color.LTGRAY)))
                                    .into(profileImage));

                    profileImage.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    class MessageSentViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = MessageSentViewHolder.class.getSimpleName();
        @BindView(R.id.timestamp_tv)
        TextView timestampTV;
        @BindView(R.id.message_tv)
        TextView messageTV;
        @BindView(R.id.profile_thumbnail_iv)
        ImageView profileImage;

        private Context context;

        MessageSentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        void bind(final Message message, int position) {
            messageTV.setText(message.getMessage());
            timestampTV.setText(message.getTimeStamp());

            loadProfileImage(message, position);

            Log.i(TAG, "bind: SENT CALLED");

            itemView.setOnClickListener(view -> listener.onMessageClicked(message));

            itemView.setOnLongClickListener(view -> {
                listener.onMessageLongClicked(message);
                return true;
            });
        }

        void loadProfileImage(Message message, int position) {
            if (position == 0) {
                if (messageLog.get(position).getSenderId().equals(message.getSenderId())) {

                    // Create a reference with an initial file path and name
                    StorageReference pathReference = storageRef.child(message.getSenderId() + "_profile_img");

                    pathReference.getDownloadUrl()
                            .addOnSuccessListener(uri ->
                                    Glide.with(context)
                                            .load(uri)
                                            .apply(RequestOptions.fitCenterTransform()
                                                    .circleCrop()
                                                    .placeholder(new ColorDrawable(Color.LTGRAY))
                                                    .fallback(new ColorDrawable(Color.LTGRAY))
                                            ).into(profileImage))
                            .addOnFailureListener(e -> Log.e(TAG, "onFailure: " + e.getMessage()));

                    profileImage.setVisibility(View.VISIBLE);
                }
            }

            if (position - 1 >= 0) {
                if (messageLog.get(position - 1).getSenderId().equals(message.getSenderId())) {
                    profileImage.setVisibility(View.INVISIBLE);
                } else {
                    // Create a reference with an initial file path and name
                    StorageReference pathReference = storageRef.child(message.getSenderId() + "_profile_img");

                    pathReference.getDownloadUrl()
                            .addOnSuccessListener(uri ->
                                    Glide.with(context)
                                            .load(uri)
                                            .apply(RequestOptions.fitCenterTransform()
                                                    .circleCrop()
                                                    .placeholder(new ColorDrawable(Color.LTGRAY))
                                                    .fallback(new ColorDrawable(Color.LTGRAY))
                                            ).into(profileImage))
                            .addOnFailureListener(e -> Log.e(TAG, "onFailure: " + e.getMessage()));

                    profileImage.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
