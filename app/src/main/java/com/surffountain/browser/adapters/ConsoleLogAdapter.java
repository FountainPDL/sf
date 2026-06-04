package com.surffountain.browser.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.surffountain.browser.R;

import java.util.ArrayList;
import java.util.List;

public class ConsoleLogAdapter extends RecyclerView.Adapter<ConsoleLogAdapter.ViewHolder> {

    private final List<ConsoleMessage> messages = new ArrayList<>();

    public void add(ConsoleMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_console_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsoleMessage msg = messages.get(position);
        holder.tvMessage.setText("[" + msg.messageLevel() + "] " + msg.message());
        holder.tvSource.setText(msg.sourceId() + ":" + msg.lineNumber());

        int color;
        switch (msg.messageLevel()) {
            case ERROR: color = 0xFFCF6679; break;
            case WARNING: color = 0xFFFFC107; break;
            default: color = 0xFF4CAF50;
        }
        holder.tvMessage.setTextColor(color);
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvSource;
        ViewHolder(View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tv_console_message);
            tvSource = view.findViewById(R.id.tv_console_source);
        }
    }
}
