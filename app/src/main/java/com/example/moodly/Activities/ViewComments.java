package com.example.moodly.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.moodly.Controllers.CommentController;
import com.example.moodly.Controllers.MoodController;
import com.example.moodly.Models.Comment;
import com.example.moodly.Models.Mood;
import com.example.moodly.R;

import java.util.ArrayList;

public class ViewComments extends AppCompatActivity {
    ArrayList<Comment> commentList = new ArrayList<>();
    ListView displayCommentList;
    TextView displayNoComment;
    Button loadMoreComments;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
        android.support.v7.app.ActionBar action = getSupportActionBar();
        action.setTitle("Comments");
        id = getIntent().getStringExtra("moodID");
        // refresh comments
        commentList = CommentController.getInstance().getCommentList(id, true);
        showComments(commentList);
        setListeners();

    }

    protected void showComments(ArrayList<Comment> commentList) {
        ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.messageSwitcher);
        displayCommentList = (ListView) findViewById(R.id.commentsView);
        displayNoComment = (TextView) findViewById(R.id.noComments);
        loadMoreComments = (Button) findViewById(R.id.loadMore);
        if (commentList.size() == 0) {
            viewSwitcher.showNext();
            loadMoreComments.setVisibility(Button.INVISIBLE);
        } else {
            ArrayAdapter<Comment> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, commentList);
            displayCommentList.setAdapter(adapter);
        }
    }

    protected void setListeners() {
        loadMoreComments.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentList = CommentController.getInstance().getCommentList(id, false);

                ArrayAdapter<Comment> adapter = new ArrayAdapter<>(ViewComments.this, android.R.layout.simple_list_item_1, commentList);
                displayCommentList.setAdapter(adapter);
            }

            })

        );
    }
}