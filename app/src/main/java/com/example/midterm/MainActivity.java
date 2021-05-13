package com.example.midterm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginInterface, RegisterFragment.RegisterAccountInterface, NewForumFragment.NewForumFragmentInterface, ForumListFragment.ForumListFragmentInterface {
//    DataServices.AuthResponse loggedInUser;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer,new LoginFragment())
                .commit();
    }

    @Override
    public void getLoginCredentials(DataServices.AuthResponse authResponse) {
        Log.d("Demo","Successfully Login" + authResponse);
        token = authResponse.getToken();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, ForumListFragment.newInstance(authResponse.getToken()))
                .commit();
    }

    @Override
    public void createNewAccount() {
        Log.d("Demo","Create an account");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer,new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void addNewlyCreatedUser(DataServices.AuthResponse authResponse) {
        Log.d("Demo","New user created");
        token = authResponse.getToken();
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, ForumListFragment.newInstance(authResponse.getToken()))
                .commit();

    }

    @Override
    public void cancelRegisterUser() {
        Log.d("Demo","Cancel new user registration");
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer,new LoginFragment())
                .commit();
    }

    @Override
    public void new_forum_created(String token,DataServices.Forum forum) {
        Log.d("Demo","New forum, created");
        token = token;
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, ForumListFragment.newInstance(token))
                .commit();
    }

    @Override
    public void cancel_new_forum_create() {
        Log.d("Demo","Cancel create new forum");
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void logout() {
        token = null;
        getSupportFragmentManager().popBackStack();
        Log.d("Demo","Logout clicked");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer,new LoginFragment())
                .commit();
    }

    @Override
    public void newForum(String token) {
        this.token =token;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer,NewForumFragment.newInstance(token))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void view_forum(String token, DataServices.Forum forum) {
        this.token = token;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer,ForumViewFragment.newInstance(token,forum))
                .addToBackStack(null)
                .commit();
    }
}