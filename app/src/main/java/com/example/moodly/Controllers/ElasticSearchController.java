package com.example.moodly.Controllers;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

/**
 * Created by jkc1 on 2017-03-11.
 */

/**
 * Base elastic search controller that our MoodController and UserController inherit from
 */
public class ElasticSearchController {

    protected static JestDroidClient client;

    /**
     * Checks if we are connected to elastic search, if not, then connect
     */
    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");

            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }

}
