package com.example.personinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PersonRecyler extends RecyclerView.Adapter<PersonRecyler.ViewHolder> {
    public ArrayList<PersonAdapter> personAdapters;
    public Context context;
    public OnPersonListener onPersonListener;

    public PersonRecyler(Context context, ArrayList<PersonAdapter> personAdapters, OnPersonListener onPersonListener){
        this.context = context;
        this.personAdapters = personAdapters;
        this.onPersonListener = onPersonListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gateout_person_recycler, parent, false);
        ViewHolder holder = new ViewHolder(view, onPersonListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fullname = personAdapters.get(position).getFname() + " " + personAdapters.get(position).getLname();
        holder.personName.setText(fullname);
    }

    @Override
    public int getItemCount() {
        return personAdapters.size();
    }

    public int getClickedPersonID(int position) {
        return personAdapters.get(position).getId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView personName;
        LinearLayout linearLayout;
        OnPersonListener onPersonListener;

        public ViewHolder(@NonNull View itemView, OnPersonListener onPersonListener) {
            super(itemView);
            personName = itemView.findViewById(R.id.personName);
            linearLayout = itemView.findViewById(R.id.personParentLayout);
            this.onPersonListener = onPersonListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPersonListener.OnPersonClickListener(getAdapterPosition());
        }
    }

    public interface OnPersonListener{
        void OnPersonClickListener(int position);
    }
}
