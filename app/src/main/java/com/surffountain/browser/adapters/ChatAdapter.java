package com.surffountain.browser.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.surffountain.browser.R;
import com.surffountain.browser.models.ChatMessage;

public class ChatAdapter extends ListAdapter<ChatMessage, ChatAdapter.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_AI = 1;

    public ChatAdapter() {
        super(DIFF_CALLBACK);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isUser() ? TYPE_USER : TYPE_AI;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == TYPE_USER ? R.layout.item_chat_user : R.layout.item_chat_ai;
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessage;

        ViewHolder(android.view.View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tv_message);
        }

        void bind(ChatMessage msg) {
            tvMessage.setText(msg.getContent());
        }
    }

    private static final DiffUtil.ItemCallback<ChatMessage> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChatMessage>() {
                @Override
                public boolean areItemsTheSame(@NonNull ChatMessage o, @NonNull ChatMessage n) {
                    return o.getTimestamp() == n.getTimestamp();
                }
                @Override
                public boolean areContentsTheSame(@NonNull ChatMessage o, @NonNull ChatMessage n) {
                    return o.getContent().equals(n.getContent());
                }
            };
}
