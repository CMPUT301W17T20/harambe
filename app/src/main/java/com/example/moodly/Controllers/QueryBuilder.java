package com.example.moodly.Controllers;

import java.util.ArrayList;

/**
 * Created by jkc1 on 2017-03-19.
 */

public class QueryBuilder {

    private String usernameString;

    private int emotion;
    private boolean recent;
    private String reason;

    // ONLY TO BE INSTANTIATED WITHIN CONTROLLERS
    public QueryBuilder() {

        usernameString = "";

        emotion = 0;
        recent = false;
        reason = "";

    }


    // sets the emotion to filter for
    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    // if recent is true, search for the last 7 days of moods
    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    // Assuming our reason text search is a single word
    // sets the reason to search for
    public void setReason(String reason) {
        this.reason = reason;
    }

    // sets the users to be searched for
    // for moods class, please set this in GetMoodTask
    public void setUsers(ArrayList<String> usernameArray) {

        usernameString = usernameArray.get(0);
        for (int i = 1; i < usernameArray.size(); i++) {
            usernameString += " OR ";
            usernameString += usernameArray.get(i);
        }

    }


    // for moods class, please set this in GetMoodTask
    public String getMoodQuery() {

        String emotionMatch, recentMatch, reasonMatch;

        String ownerMatch = "\"must\" : { \n" +
                "\"query_string\" : { \n" +
                "\"fields\" : [\"owner\"],\n" +
                "\"query\" : \"" + usernameString + "\"\n" +
                "}" +
                "\n}";

        String sort = "\n\"sort\": { \"date\": { \"order\": \"desc\" } }";

        String query =
                "{" +
                    "\n\"query\" : {\n" +
                    "\"bool\" : {\n";

        query += ownerMatch;

        if(emotion != 0) {
            emotionMatch = ",\"must\" : { \n" +
                        "\"term\" : { \n" +
                            "\"emotion\" : \"" + emotion + "\"" +
                        "\n}" +
                    "\n}";
            query += emotionMatch;
        }

        if(recent) {
            recentMatch = ",\"must\" : { \n" +
                        "\"range\" : { \n" +
                            "\"date\" : {\n" +
                                "\"gte\" : \"now-7d\"" +
                            "\n}" +
                        "\n}" +
                    "\n}";
            query += recentMatch;
        }

        if(reason != "") {
            reasonMatch = ",\"must\" : { \n" +
                    "\"query_string\" : { \n" +
                    "\"fields\" : [\"reasonText\"],\n" +
                    "\"query\" : \"" + reason + "\"\n" +
                    "}" +
                    "\n}";
            query += reasonMatch;
        }

        query += "\n}" + // ends bool
                "\n},"; // ends query

        query += sort;

        query += " \n} "; // ends first bracket

        return query;
    }

}
