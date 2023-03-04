package com.example.ft_hangouts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;

public class MessageViewHolder extends RecyclerView.ViewHolder
{
    private TextView text, date;

    private Message  message;

    public MessageViewHolder(View itemView)
    {
        super(itemView);
        this.text = (TextView) itemView.findViewById(R.id.txtMessageContent);
        this.date = (TextView) itemView.findViewById(R.id.txtMessageDate);
    }

    public void bind(Message message)
    {
        this.message = message;
        this.text.setText(this.message.getText());
        this.date.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(this.message.getDate()));
    }

}

