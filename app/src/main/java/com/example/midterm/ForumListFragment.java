package com.example.midterm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ForumListFragment extends Fragment implements ForumListViewAdapter.ForumListAdapterViewInterface{

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    ForumListFragmentInterface listener;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ForumListViewAdapter adapter;
    DataServices.Account loggedInAccount;

    public ForumListFragment() {
        // Required empty public constructor
    }

    public static ForumListFragment newInstance(String param1) {
        ForumListFragment fragment = new ForumListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum_list, container, false);
        getActivity().setTitle(getResources().getString(R.string.forums_label));
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        new GetAccountAsync().execute(mParam1);

        view.findViewById(R.id.button_create_new_forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.newForum(mParam1);
            }
        });
        view.findViewById(R.id.button_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.logout();
            }
        });
        return view;
    }

    interface ForumListFragmentInterface{
        void logout();
        void newForum(String token);
        void view_forum(String token, DataServices.Forum forum);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ForumListFragment.ForumListFragmentInterface) {
            listener = (ForumListFragment.ForumListFragmentInterface)context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IListener");
        }
    }

    class GetForumListsAsync extends AsyncTask<String,String, ArrayList<DataServices.Forum>>{
        @Override
        protected void onPostExecute(ArrayList<DataServices.Forum> forums) {
            if(forums!=null){
                adapter = new ForumListViewAdapter(mParam1,loggedInAccount,forums,ForumListFragment.this);
                recyclerView.setAdapter(adapter);
            }
            else{
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_error), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected ArrayList<DataServices.Forum> doInBackground(String... strings) {
            try{
                return DataServices.getAllForums(strings[0]);
            }
            catch (DataServices.RequestException e){
                e.printStackTrace();
                return null;
            }
        }
    }

    class GetAccountAsync extends AsyncTask<String,String, DataServices.Account>{
        @Override
        protected void onPostExecute(DataServices.Account account) {
            if(account!=null){
                loggedInAccount = account;
                new GetForumListsAsync().execute(mParam1);
            }
        }

        @Override
        protected DataServices.Account doInBackground(String... strings) {
            try{
                return DataServices.getAccount(strings[0]);
            }
            catch (DataServices.RequestException e){
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void get_forum_details(String token,DataServices.Forum forum) {
        listener.view_forum(token,forum);
    }

    @Override
    public void deleteForum(String token, Long id) {
        new DeleteForumAsync().execute(token,String.valueOf(id));
    }

    @Override
    public void LikeDislikeForum(String action, String token, Long id) {
        new LikeDislikeAsync().execute(action,token,String.valueOf(id));
    }

    class DeleteForumAsync extends AsyncTask<String,String,String>{
        @Override
        protected void onPostExecute(String s) {
            new GetForumListsAsync().execute(mParam1);
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                DataServices.deleteForum(strings[0],Long.valueOf(strings[1]));
            }catch(DataServices.RequestException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    class LikeDislikeAsync extends AsyncTask <String,String,String>{
        @Override
        protected void onPostExecute(String s) {
            new GetForumListsAsync().execute(mParam1);
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                if(strings[0].equals("Like")){
                    DataServices.likeForum(strings[1],Long.valueOf(strings[2]));
                }
                else{
                    DataServices.unLikeForum(strings[1],Long.valueOf(strings[2]));
                }
            }
            catch (DataServices.RequestException e){
                e.printStackTrace();
            }
            return null;
        }
    }


}