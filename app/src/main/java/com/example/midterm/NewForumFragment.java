package com.example.midterm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class NewForumFragment extends Fragment {
    private static final String TOKEN = "TOKEN";
    private String mParam1;
    EditText name, desc;
    String titleValue,descValue;
    NewForumFragmentInterface listener;

    public static NewForumFragment newInstance(String param1) {
        NewForumFragment fragment = new NewForumFragment();
        Bundle args = new Bundle();
        args.putString(TOKEN, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_forum, container, false);
        getActivity().setTitle(getResources().getString(R.string.new_forum_label));
        name = view.findViewById(R.id.editText_forum_title);
        desc = view.findViewById(R.id.editText_forum_desc);
        view.findViewById(R.id.button_submit_new_forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // submit button is clicked
                titleValue = name.getText().toString();
                descValue = desc.getText().toString();
                if(titleValue.isEmpty() || descValue.isEmpty()){
                    Toast.makeText(getActivity(),"All fields are mandatory", Toast.LENGTH_LONG).show();
                }
                else{
                    new CreateForumAsync().execute(mParam1,titleValue,descValue);
                }

            }
        });

        view.findViewById(R.id.textView_cancel_new_forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel_new_forum_create();
            }
        });

        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NewForumFragment.NewForumFragmentInterface) {
            listener = (NewForumFragment.NewForumFragmentInterface)context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IListener");
        }
    }

    interface NewForumFragmentInterface {
        void new_forum_created(String token,DataServices.Forum forum);
        void cancel_new_forum_create();
    }

    class CreateForumAsync extends AsyncTask<String,String,DataServices.Forum>{
        @Override
        protected void onPostExecute(DataServices.Forum forum) {
            if(forum!=null){
                listener.new_forum_created(mParam1,forum);
            }
            else{
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_error), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected DataServices.Forum doInBackground(String... strings) {
            try{
                return DataServices.createForum(strings[0],strings[1],strings[2]);
            }
            catch (DataServices.RequestException e){
                e.printStackTrace();
                return null;
            }
        }
    }
}