package com.example.midterm;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumListViewAdapter extends RecyclerView.Adapter<ForumListViewAdapter.ForumListViewHolder> {
    DataServices.Account currentUserAccount;
    ArrayList<DataServices.Forum> Lists;
    String token;
    ForumListViewAdapter.ForumListAdapterViewInterface listener;

    public ForumListViewAdapter(String token, DataServices.Account account, ArrayList<DataServices.Forum> Data, ForumListAdapterViewInterface AppListListener) {
        this.currentUserAccount = account;
        this.Lists = Data;
        this.listener = AppListListener;
        this.token = token;
    }

    @NonNull
    @Override
    public ForumListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_list_layout, parent, false);
        ForumListViewAdapter.ForumListViewHolder viewHolder = new ForumListViewAdapter.ForumListViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForumListViewHolder holder, int position) {
        DataServices.Forum selectedForum = Lists.get(position);
        holder.title.setText(selectedForum.getTitle());
        holder.author.setText(selectedForum.getCreatedBy().getName());
        if (selectedForum.getDescription().length() > 200) {
            holder.desc.setText(selectedForum.getDescription().substring(0, 200)+ "...");
        }
        else{
            holder.desc.setText(selectedForum.getDescription());
        }
        if(selectedForum.getLikedBy().size()==1){
            holder.likes.setText(selectedForum.getLikedBy().size() + " Like  |");
        }
        else {
            holder.likes.setText(selectedForum.getLikedBy().size() + " Likes |");
        }

        if (selectedForum.getCreatedBy() != currentUserAccount) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
        }
        holder.date.setText(selectedForum.getCreatedAt().toString());
        holder.position = position;
        holder.forum = selectedForum;
        holder.currentAccount = currentUserAccount;
        holder.token = token;
        if(selectedForum.getLikedBy().contains(currentUserAccount)){
            holder.liked.setVisibility(View.VISIBLE);
            holder.notLiked.setVisibility(View.GONE);
        }
        else{
            holder.liked.setVisibility(View.GONE);
            holder.notLiked.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return Lists.size();
    }

    public static class ForumListViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, desc, likes, date;
        ImageView delete, liked, notLiked;
        DataServices.Account currentAccount;
        String token;
        int position;
        DataServices.Forum forum;
        ForumListViewAdapter.ForumListAdapterViewInterface ListListener;

        @SuppressLint("ResourceType")
        public ForumListViewHolder(@NonNull View itemView, ForumListAdapterViewInterface AppListListener) {
            super(itemView);
            this.ListListener = AppListListener;
            title = itemView.findViewById(R.id.textView_forum_title);
            author = itemView.findViewById(R.id.textView_forum_author);
            desc = itemView.findViewById(R.id.textView_forum_desc);
            likes = itemView.findViewById(R.id.textView_likes);
            date = itemView.findViewById(R.id.textView_date);
            delete = itemView.findViewById(R.id.imageView_delete);
            notLiked = itemView.findViewById(R.id.imageView_not_liked);
            liked = itemView.findViewById(R.id.imageView_liked);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListListener.get_forum_details(token,forum);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListListener.deleteForum(token,forum.getForumId());
                }
            });

            liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListListener.LikeDislikeForum("Dislike", token,forum.getForumId());
                }
            });

            notLiked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListListener.LikeDislikeForum("Like",token,forum.getForumId());
                }
            });
        }

    }

    interface ForumListAdapterViewInterface {
        void get_forum_details(String token,DataServices.Forum forum);
        void deleteForum(String token, Long id);
        void LikeDislikeForum(String action, String token, Long id);
    }
}
