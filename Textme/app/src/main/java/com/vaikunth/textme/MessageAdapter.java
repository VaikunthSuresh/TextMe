package com.vaikunth.textme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> messages;
    private DatabaseReference chatRef;

    public MessageAdapter() {
    }

    public MessageAdapter(Context mContext, ArrayList<String> messages) {
        this.mContext = mContext;
        this.messages = messages;
        chatRef= FirebaseDatabase.getInstance().getReference().child("Chats");
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.message_item,parent,false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        String pos=MessageActivity.messagePosition.get(position);
        Log.i("TAG", "onBindViewHolder: "+pos);
        if(pos.equals("0"))
        {
            holder.txtLeft.setVisibility(View.VISIBLE);
            holder.txtRight.setVisibility(View.GONE);
            holder.txtLeft.setText(messages.get(position));
        }
        else
        {

            holder.txtRight.setVisibility(View.VISIBLE);
            holder.txtLeft.setVisibility(View.GONE);
            holder.txtRight.setText(messages.get(position));
        }
        holder.txtRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(mContext);
                alert.setTitle("Delete Message");
                alert.setMessage("Are you sure?Whether you to delete this message"+messages.get(position));
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chatRef.child(MessageActivity.messageId.get(position)).removeValue();

                    }
                });
                alert.show();
            }
        });

    }

    @Override
    public int getItemCount() {

        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtLeft,txtRight;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLeft=itemView.findViewById(R.id.txtLeft);
            txtRight=itemView.findViewById(R.id.txtRight);
        }
    }
}
