package com.example.ft_hangouts;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder>
{
    List<Message> messages;

    public MessageAdapter(List<Message> messages)
    {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view;

        if (viewType == 1)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item_message_in, viewGroup, false);
        else
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item_message_out, viewGroup, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position)
    {
        Message message = this.messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount()
    {
        return this.messages.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        super.getItemViewType(position);

        return (this.messages.get(position).getIo() ? 1 : 0);
    }

    public void addMessage(Message message)
    {
        this.messages.add(message);
    }
}