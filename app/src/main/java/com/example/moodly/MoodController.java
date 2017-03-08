//package com.example.moodly;
//
//import java.util.ArrayList;
//
///**
// * Created by MinhNguyen on 06/03/2017.
// */
//
//
//// https://www.youtube.com/watch?v=NZaXM67fxbs singleton design pattern Mar 06
//public class MoodController {
//    // this isn't safe from synchronization, does it need to be?
//    // i don't know how to verify that, but i guess we will find out
//    // soon
//    private static MoodController instance = null;
//    private ArrayList<Mood> moodList;
//    private ArrayList<Mood> moodFollowList;
//    private ArrayList<Mood> filteredList;
//
//    private MoodController() {
//        moodList = new ArrayList<Mood>();
//        moodFollowList = new ArrayList<Mood>();
//        filteredList = new ArrayList<Mood>();
//    }
//
//    public static MoodController getInstance() {
//
//        if(instance == null) {
//            instance = new MoodController();
//        }
//
//        return instance;
//    }
//
//    public void addMood(Mood m){
//        instance.moodList.add(m);
//    }
//
//    public void editMood (int position, Mood newMood) {
//        moodList.remove(position);
//        moodList.add(position, newMood);
//    }
//
//    public void deleteMood(int position) {
//        moodList.remove(position);
//    }
//
//
//    public String getLocation(int position) {
//        Mood m = moodList.get(position);
//        return m.getLocation();
//    }
//
//
//
//    public ArrayList<Mood> getFiltered() {
//        this.filter();
//        return this.filteredList;
//    }
//    // ??? make helper functions ?
//    // only filters and sets the filtered list as filteredList
//    // moodList should be the full list
//    // filtering person, good luck
//    // so, currently all this does is set filteredList to reference
//    // each of its elemnts from moodList, this is not a deep copy
//    private void filter(){
//        filteredList = (ArrayList<Mood>) moodList.clone();
//    }
//}
//
//


















package com.example.moodly;

        import android.content.Context;
        import android.os.AsyncTask;
        import android.util.Log;
        import android.widget.Toast;

        import com.google.gson.JsonObject;
        import com.searchly.jestdroid.DroidClientConfig;
        import com.searchly.jestdroid.JestClientFactory;
        import com.searchly.jestdroid.JestDroidClient;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;

        import io.searchbox.core.DocumentResult;
        import io.searchbox.core.Index;
        import io.searchbox.core.Search;
        import io.searchbox.core.SearchResult;

        import static junit.framework.Assert.assertEquals;
//import com.jest
/**
 * Created by MinhNguyen on 06/03/2017.
 */


// https://www.youtube.com/watch?v=NZaXM67fxbs singleton design pattern Mar 06
public class MoodController {
    // this isn't safe from synchronization, does it need to be?
    // i don't know how to verify that, but i guess we will find out
    // soon
    private static MoodController instance = null;
    private static ArrayList<Mood> moodList;
    private ArrayList<Mood> moodFollowList;
    private ArrayList<Mood> filteredList;

    // move this out of the moodController
    private static JestDroidClient client;

    private MoodController() {
        moodList = new ArrayList<Mood>();
        moodFollowList = new ArrayList<Mood>();
        filteredList = new ArrayList<Mood>();
    }

    public static MoodController getInstance() {

        if(instance == null) {
            instance = new MoodController();
        }

        return instance;
    }

    public static class AddMoodTask extends AsyncTask<Mood, Void, Void> {

        int completion = 0;
        @Override
        protected Void doInBackground(Mood... moods){
            verifySettings();

            for(Mood mood : moods) {
                // should probably create custom builder?
                String source = "{" +
                        "\"date\": \"" + mood.getDate().toString() + "\", " +
                        "\"owner\": \"" + mood.getOwner() + "\", " +
                        "\"location\": \"" + mood.getLocation() + "\", " +
                        "\"trigger\": \"" + mood.getTrigger() + "\", " +
                        "\"reasonText\": \"" + mood.getReasonText() + "\", " +
                        "\"image\": \"" + mood.getImage() + "\", " +
                        "\"emotion\": \"" + "2" + "\", " +
                        "\"socialSituation\": \"" + "1" + "\"" +
                        "}";

                Index index = new Index.Builder(source).index("cmput301w17t20").type("mood").build();

//                CharSequence debugText = "App creation";
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(debugContext, debugText, duration);
//                toast.show();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        mood.setId(result.getId());
                    } else {
                        Log.i("Error", "Elasticsearch was not able to add the tweet");
                    }
                    // where is the client?
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the tweets");
                }

            }

            return null;
        }
    }

    public static class GetMoodTask extends AsyncTask<String, Void, ArrayList<Mood>> {
        @Override
        protected ArrayList<Mood> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<Mood> currentMoodList = new ArrayList<Mood>();
//            String query =
//                    "{ \n\"query\" : {\n" +
//                            "    \"term\" : { \"owner\" : \"" + search_parameters[0] +
//                            "\"     }\n " +
//                            "    }\n" +
//                            " } ";

            String query = "";
            // TODO Build the query
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("mood")
                    .build();

            try {
                // TODO get the results of the query
                SearchResult result = client.execute(search);
                if(result.isSucceeded()) {
                    List<SearchResult.Hit<Mood1, Void>> foundMoods = result.getHits(Mood1.class);
                    Mood1 temp2 = foundMoods.get(0).source;

                    JsonObject testResult = result.getJsonObject();

                    //List<Mood1> foundMoods = result.g(Mood1.class);
                    //assertEquals(1, foundMoods.size());
                    for(int i = 0; i < foundMoods.size(); i++) {
                        Mood1 temp1 = foundMoods.get(i).source;
                        Mood temp = new Mood(foundMoods.get(i).source);
                        currentMoodList.add(temp);
                    }
                    //currentMoodList.addAll(foundMoods);
                } else {
                    Log.i("Error", "Search query failed to find any tweets that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            moodList = currentMoodList;
            return null;
        }
    }


    public void addMood(Mood m){

        instance.moodList.add(m);
    }

    public void editMood (int position, Mood newMood) {
        moodList.remove(position);
        moodList.add(position, newMood);
    }

    public void deleteMood(int position) {
        moodList.remove(position);
    }


    public String getLocation(int position) {
        Mood m = moodList.get(position);
        return m.getLocation();
    }



    public ArrayList<Mood> getFiltered() {
        this.filter();
        return this.filteredList;
    }
    // ??? make helper functions ?
    // only filters and sets the filtered list as filteredList
    // moodList should be the full list
    // filtering person, good luck
    // so, currently all this does is set filteredList to reference
    // each of its elemnts from moodList, this is not a deep copy
    private void filter(){
        filteredList = (ArrayList<Mood>) moodList.clone();
    }

    // move this out of the mood controller?
    private static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
//            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("localhost:9200");

            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
