package eu.urbancoders.zonkysniper.messaging;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.08.2016
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.dataobjects.Message;

import java.util.List;

public class MessagesFromZonkyAdapter extends RecyclerView.Adapter<MessagesFromZonkyAdapter.MessagesViewHolder> {

    private List<Message> messagesList;
    private Context context;

    public class MessagesViewHolder extends RecyclerView.ViewHolder {
        public TextView date, text;

        public MessagesViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            text = (TextView) view.findViewById(R.id.message);
        }
    }


    public MessagesFromZonkyAdapter(Context context, List<Message> messagesList) {
        this.messagesList = messagesList;
        this.context = context;
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_list_row, parent, false);

        return new MessagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder holder, int position) {
        Message message = messagesList.get(position);
        holder.date.setText(Constants.DATE_DD_MM_YYYY_HH_MM.format(message.getDate()));
        holder.text.setText(message.getText());
        if (!message.isVisited()) {
            holder.text.setTypeface(null, Typeface.BOLD);
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
