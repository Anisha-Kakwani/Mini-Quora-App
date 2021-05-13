package com.example.midterm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;


public class ForumViewFragment extends Fragment implements ForumViewAdapter.ForumViewListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String token = "token";

    TextView author_name, forum_name, comments, desc;
    EditText addComment;
    Button post;
    ForumViewAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private DataServices.Forum mParam1;
    private String mParamToken;
    DataServices.Account loggedInAccount;

    public ForumViewFragment() {
        // Required empty public constructor
    }


    public static ForumViewFragment newInstance(String tok, DataServices.Forum data) {
        ForumViewFragment fragment = new ForumViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, data);
        args.putString(token, tok);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (DataServices.Forum) getArguments().getSerializable(ARG_PARAM1);
            mParamToken = getArguments().getString(token);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum_view, container, false);
        getActivity().setTitle(getResources().getString(R.string.forum_label));
        recyclerView = view.findViewById(R.id.commentListRC);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        author_name = view.findViewById(R.id.textView_author_detail);
        forum_name = view.findViewById(R.id.textView_title_detail);
        comments = view.findViewById(R.id.viewCommentSize);
        desc = view.findViewById(R.id.textView_desc_detail);
        addComment = view.findViewById(R.id.EditText_comment_detail);
        post = view.findViewById(R.id.button_post);
        DataServices.Account acc = mParam1.getCreatedBy();

        author_name.setText(acc.getName());
        forum_name.setText(mParam1.getTitle());
        desc.setText(mParam1.getDescription());
        desc.setMovementMethod(new ScrollingMovementMethod());
        new GetAccountAsync().execute(mParamToken);
        // call getAccountAsync
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addComment.getText().toString().isEmpty()) {
                    new addCommentAsync().execute(mParamToken, String.valueOf(mParam1.getForumId()), addComment.getText().toString());
                }
            }
        });

        return view;
    }

    @Override
    public void deleteComment(String commentId) {
        new deleteCommentAsync().execute(mParamToken, String.valueOf(mParam1.getForumId()), commentId);
    }

    class deleteCommentAsync extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPostExecute(String[] s) {
            if (s!= null)
                new getCommentAsync().execute(s[0], s[1]);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            try {
                DataServices.deleteComment(strings[0], parseInt(strings[1]), parseInt(strings[2]));
                String[] s = new String[2];
                s[0] = strings[0];
                s[1] = strings[1];
                return s;
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    class getCommentAsync extends AsyncTask<String, String, ArrayList<DataServices.Comment>> {

        @Override
        protected void onPostExecute(ArrayList<DataServices.Comment> s) {
            if (s!= null) {
                comments.setText(String.valueOf(s.size()));

                adapter = new ForumViewAdapter(s, mParamToken, loggedInAccount,ForumViewFragment.this);
                recyclerView.setAdapter(adapter);
            }
        }

        @Override
        protected ArrayList<DataServices.Comment> doInBackground(String... strings) {
            try {
                return DataServices.getForumComments(strings[0], parseInt(strings[1]));
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return null;
            }

        }
    }


    class addCommentAsync extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPostExecute(String[] s) {
            if (s != null) {
                new getCommentAsync().execute(s[0], s[1]);
            }
        }

        @Override
        protected String[] doInBackground(String... strings) {
            try {
                DataServices.createComment(strings[0], parseInt(strings[1]), strings[2]); //token, forumId, text
                String[] s = new String[2];
                s[0] = strings[0];
                s[1] = strings[1];
                return s;
            } catch (DataServices.RequestException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    //async to get account
    class GetAccountAsync extends AsyncTask<String,String, DataServices.Account>{
        @Override
        protected void onPostExecute(DataServices.Account account) {
            if(account!=null){
                loggedInAccount = account;
                new getCommentAsync().execute(mParamToken, String.valueOf(mParam1.getForumId()));;
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


}