package com.tronography.locationchat.lobby;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tronography.locationchat.R;
import com.tronography.locationchat.model.ChatRoomModel;

import java.util.ArrayList;


public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    ArrayList<ChatRoomModel> chatRoomList = new ArrayList<>();
    Listener listener;

    public ChatRoomAdapter(Listener listener) {
        this.listener = listener;
    }

    @Override
    public ChatRoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_chatroom_rv,
                parent, false);

        return new ChatRoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatRoomViewHolder holder, int position) {
        ChatRoomModel chatRoom = chatRoomList.get(position);
        holder.bind(chatRoom);
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public void setData(ArrayList<ChatRoomModel> chatRoomList){
        this.chatRoomList = chatRoomList;
        notifyDataSetChanged();
    }

    public interface Listener {
        void onMessageLongClicked(ChatRoomModel chatRoom);
        void onChatRoomClicked(ChatRoomModel chatRoom);
    }

//  _______________________________________VIEWHOLDER_______________________________________________

    class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView chatRoomTitleTV;

        ChatRoomViewHolder(View itemView) {
            super(itemView);
            chatRoomTitleTV = itemView.findViewById(R.id.chatroom_title_tv);
        }

        void bind(final ChatRoomModel chatRoom) {
            chatRoomTitleTV.setText(chatRoom.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onChatRoomClicked(chatRoom);
                }
            });

            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onMessageLongClicked(chatRoom);
                    return true;
                }
            });
        }
    }
}
