package com.example.moodly.Controllers;

import com.example.moodly.Models.Mood;

import java.util.ArrayList;

/**
 * Created by jkc1 on 2017-03-19.
 */

public class QueryBuilder {

    private String usernameString;

    private int emotion;
    private boolean recent;
    private String reason;
    private int resultOffset;

    // ONLY TO BE INSTANTIATED WITHIN CONTROLLERS
    public QueryBuilder() {

        usernameString = "";

        emotion = 0;
        recent = false;
        reason = "";
        resultOffset = 0;

    }


    // check if mood can be added locally given filters
    public boolean isValid(Mood m) {

        if ((emotion != 0) && (emotion != m.getEmotion())) {
            return false;
        } else if
            ((reason != "") && (! m.getReasonText().contains(reason))) {
            return false;
        }
        return true;
    }

    public void setResultOffset(int resultOffset) {
        this.resultOffset = resultOffset;
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
                "{ \n" +
                        "\t\"from\" : "+Integer.toString(resultOffset)+", \"size\" : 10,\n" +
                        //"\t\"terminate_after\" : 10," +
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
                    "\"gte\" : \"now-/w\"" +
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
