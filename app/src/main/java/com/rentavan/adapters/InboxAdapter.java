package com.rentavan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rentavan.R;
import com.rentavan.objects.Inbox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.inboxViewHolder>{

    private static final FirebaseDatabase FIXCARE_DB = FirebaseDatabase.getInstance();
    private static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();

    Context context;
    ArrayList<Inbox> arrInbox = new ArrayList<>();
    private OnInboxListener mOnInboxListener;

    public InboxAdapter(Context context, ArrayList<Inbox> arrInbox, OnInboxListener onInboxListener) {
        this.context = context;
        this.arrInbox = arrInbox;
        this.mOnInboxListener = onInboxListener;
    }

    @NonNull
    @Override
    public inboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_inbox, parent, false);
        return new inboxViewHolder(view, mOnInboxListener);
    }

    @Override
    public void onBindViewHolder(@NonNull inboxViewHolder holder, int position) {
        Inbox inbox = arrInbox.get(position);

        loadPassengerName(holder, inbox);
        loadAuthorName(holder, inbox);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy - hh:mm aa");
        holder.tvTimestamp.setText(sdf.format(inbox.getTimestamp()));
    }

    private void loadPassengerName(inboxViewHolder holder, Inbox inbox) {
        DatabaseReference dbContactName = FIXCARE_DB.getReference("user_"+ inbox.getPassengerUid());
        dbContactName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = Objects.requireNonNull(snapshot.child("firstName").getValue()).toString();
                    String lastName = Objects.requireNonNull(snapshot.child("lastName").getValue()).toString();

                    holder.tvAuthorName.setText(firstName+" "+lastName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAuthorName(inboxViewHolder holder, Inbox inbox) {
        if (Objects.equals(inbox.getAuthorUid(), Objects.requireNonNull(USER).getUid())) {
            holder.tvLastMessage.setText("You: "+inbox.getMessage());
        }
        else {
            holder.tvLastMessage.setText(inbox.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return arrInbox.size();
    }

    public class inboxViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvAuthorName, tvLastMessage, tvTimestamp;
        OnInboxListener onInboxListener;

        public inboxViewHolder(@NonNull View itemView, OnInboxListener onInboxListener) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

            this.onInboxListener = onInboxListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onInboxListener.onInboxClick(getAdapterPosition());
        }
    }

    public interface OnInboxListener{
        void onInboxClick(int position);

    }
}