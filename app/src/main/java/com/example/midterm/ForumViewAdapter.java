package com.example.midterm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumViewAdapter extends RecyclerView.Adapter<ForumViewAdapter.ListViewHolder> {
    ArrayList<DataServices.Comment> DataLists;
    String AuthToken;
    DataServices.Account account;
    ForumViewAdapter.ForumViewListener forumViewListener;

    public ForumViewAdapter(ArrayList<DataServices.Comment> Data, String token, DataServices.Account loggedInAccount,ForumViewAdapter.ForumViewListener forumViewListener) {
        this.DataLists = Data;
        this.AuthToken = token;
        this.account = loggedInAccount;
        this.forumViewListener = forumViewListener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_view_layout, parent, false);
        ForumViewAdapter.ListViewHolder viewHolder = new ForumViewAdapter.ListViewHolder(view, forumViewListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        holder.comment.setText(DataLists.get(position).getText());
        holder.authName.setText(DataLists.get(position).getCreatedBy().getName());
        holder.date.setText( DataLists.get(position).getCreatedAt().toString());
        DataServices.Account acc = null;
        if(account == DataLists.get(position).getCreatedBy() ){
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumViewListener.deleteComment(String.valueOf(DataLists.get(position).getCommentId()));
            }
        });



    }

    @Override
    public int getItemCount() {
        return DataLists.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        TextView authName, comment, date;
        ImageView delete;
        ForumViewAdapter.ForumViewListener forumViewListener;
        public ListViewHolder(@NonNull View itemView, ForumViewListener forumViewListener) {
            super(itemView);
            this.forumViewListener = forumViewListener;
            authName = itemView.findViewById(R.id.textView_author_comment_detail);
            comment = itemView.findViewById(R.id.textView_comment_desc);
            date = itemView.findViewById(R.id.textView_date_detail);
            delete = itemView.findViewById(R.id.imageView);

        }
    }

    interface ForumViewListener {
        void deleteComment(String commentId);
    }
}
