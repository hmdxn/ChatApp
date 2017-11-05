package com.tronography.locationchat.lobby;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tronography.locationchat.R;
import com.tronography.locationchat.model.ChatRoom;

import java.util.ArrayList;


public class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.ChatRoomViewHolder> {

    ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
    Listener listener;

    public LobbyAdapter(Listener listener) {
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
        ChatRoom chatRoom = chatRoomList.get(position);
        holder.bind(chatRoom);
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public void setData(ArrayList<ChatRoom> chatRoomList){
        this.chatRoomList = chatRoomList;
        notifyDataSetChanged();
    }

    public interface Listener {
        void onMessageLongClicked(ChatRoom chatRoom);
        void onChatRoomClicked(ChatRoom chatRoom);
    }

//  _______________________________________VIEWHOLDER_______________________________________________

    class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView chatRoomTitleTV;

        ChatRoomViewHolder(View itemView) {
            super(itemView);
            chatRoomTitleTV = itemView.findViewById(R.id.chatroom_title_tv);
        }

        void bind(final ChatRoom chatRoom) {
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
