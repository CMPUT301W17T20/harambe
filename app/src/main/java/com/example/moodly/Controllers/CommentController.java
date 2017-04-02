package com.example.moodly.Controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.moodly.Models.Comment;
import com.example.moodly.Models.Mood;

import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Created by jkc1 on 2017-03-20.
 */

public class CommentController extends ElasticSearchController {

    private static CommentController instance = null;
    private static ArrayList<Comment> commentList = new ArrayList<>();
    private static QueryBuilder queryBuilder;
    private static String owner;

    private CommentController() {

        commentList = new ArrayList<>();
        queryBuilder = new QueryBuilder();
        owner = UserController.getInstance().getCurrentUser().getName();

    }

    public static CommentController getInstance() {
        if(instance == null) {
            instance = new CommentController();
        }
        return instance;

    }

    /* ---------- Controller Functions ---------- */
    // Use these to interact with the views
    public static void addComment(String text, String moodID) {
        Comment tempComment = new Comment(text, owner, moodID);
        // add offline
        commentList.add(0, tempComment);
        CommentController.AddCommentTask addCommentTask = new CommentController.AddCommentTask();
        addCommentTask.execute(tempComment);
    }

    // given the mood id, return the comment
    public ArrayList<Comment> getCommentList (String moodID) {

        CommentController.GetCommentTask getCommentTask = new CommentController.GetCommentTask();
        getCommentTask.execute(moodID);

        ArrayList<Comment> tempCommentList = new ArrayList<Comment>();
        try {
            tempCommentList = getCommentTask.get();
        } catch (Exception e) {

            Log.i("Error", "Failed to get comments out of async object");
        }

        return tempCommentList;
    }

    /* ---------- Elastic Search Requests ---------- */
    private static class AddCommentTask extends AsyncTask<Comment, Void, Void> {

        @Override
        protected Void doInBackground(Comment... comments){
            verifySettings();

            for(Comment comment : comments) {

                Index index = new Index.Builder(comment).index("cmput301w17t20").type("comment").build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        if (comment.getId() == null) {
                            comment.setId(result.getId());
                            // assumption method addComment always runs before this
                            // if the id is not set, set it
                            if(commentList.get(0).getId() == null) {
                                commentList.get(0).setId(result.getId());
                            }

                        }

                    } else {
                        Log.i("Error", "Elasticsearch was not able to add the comment");
                    }
                    // where is the client?
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the comment");
                }

            }

            return null;
        }
    }

    private static class GetCommentTask extends AsyncTask<String, Void, ArrayList<Comment>> {
        @Override
        protected ArrayList<Comment> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<Comment> currentCommentList = new ArrayList<Comment>();


            String moodId = search_parameters[0];
            // use query builder to find comments with moodID
            String query = "{\n" +
                    "\t\"query\": {\n" +
                    "\t\t\"match\": {\n" +
                    "\t\t\t\"moodId\": \"" + moodId + "\" }\n" +
                    "\t}\n" +
                    "}";

            Search search = new Search.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("comment")
                    .build();

            try {
                // get the results of our query
                SearchResult result = client.execute(search);
                if(result.isSucceeded()) {
                    // hits
                    List<SearchResult.Hit<Comment, Void>> foundComments = result.getHits(Comment.class);

                    for(int i = 0; i < foundComments.size(); i++) {
                        Comment temp = foundComments.get(i).source;
                        currentCommentList.add(temp);

                    }
                    commentList = currentCommentList;
                    // for your own list of moods

                } else {
                    Log.i("Error", "Search query failed to find any comments that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return currentCommentList;
        }
    }


}

