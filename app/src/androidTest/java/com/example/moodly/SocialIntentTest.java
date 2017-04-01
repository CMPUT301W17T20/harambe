package com.example.moodly;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.moodly.Activities.LoginScreen;
import com.example.moodly.Activities.ViewMood;
import com.example.moodly.Activities.ViewMoodList;
import com.robotium.solo.Solo;

import java.util.Random;

/**
 * Created by Admin on 01/04/2017.
 */

public class SocialIntentTest extends ActivityInstrumentationTestCase2<LoginScreen> {
    private Solo solo;

    private String userName1 = null;
    private String userName2 = null;


    public SocialIntentTest() {
        super(com.example.moodly.Activities.LoginScreen.class);
    }


    public void actionSignUp(String username) {
        solo.assertCurrentActivity("Wrong Activity", LoginScreen.class);
        solo.enterText((EditText) solo.getView(R.id.userName), username);
        solo.clickOnButton("Sign Up");
        solo.assertCurrentActivity("Wrong Activities", ViewMoodList.class);
        solo.clickOnMenuItem("Log Out");
    }

    public void actionLogin(String username) {
        solo.assertCurrentActivity("Wrong Activity", LoginScreen.class);
        solo.enterText((EditText) solo.getView(R.id.userName), username);
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activities", ViewMoodList.class);
        solo.clickOnMenuItem("Social");
    }


    public void actionLogOut() {
        solo.goBack();
        solo.sendKey(solo.MENU);
        solo.clickOnText("Log Out");
        solo.assertCurrentActivity("Wrong Activity", LoginScreen.class);
    }


    protected String getRDString() {
        String CHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < 7) {
            int index = (int) (rnd.nextFloat() * CHARS.length());
            stringBuilder.append(CHARS.charAt(index));
        }
        String Str = stringBuilder.toString();
        return Str;

    }


    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }


    public void test1_SearchUser() {
        if (userName1 == null){
            userName1 = getRDString();
            actionSignUp(userName1);
        }
        if (userName2 == null) {
            userName2 = getRDString();
            actionSignUp(userName2);
        }
        actionLogin(userName1);
        solo.enterText((EditText) solo.getView(R.id.search_text), userName2);
        solo.clickOnText("Search");
        solo.clearEditText((EditText) solo.getView(R.id.search_text));
        assertTrue(solo.searchText(userName2));
        actionLogOut();
    }

    public void test2_SendRequest() {
        if (userName1 == null){
            userName1 = getRDString();
            actionSignUp(userName1);
        }
        if (userName2 == null) {
            userName2 = getRDString();
            actionSignUp(userName2);
        }

        actionLogin(userName1);
        solo.enterText((EditText) solo.getView(R.id.search_text), userName2);
        solo.clickOnText("Search");
        solo.clearEditText((EditText) solo.getView(R.id.search_text));
        assertTrue(solo.searchText(userName2));
        solo.clickOnText(userName2);
        solo.clickOnText("Send");
        actionLogOut();

        actionLogin(userName2);
        solo.clickOnText("Follow Requests");
        assertTrue(solo.searchText(userName1));
        actionLogOut();
    }

    public void test3_AcceptRequest() {
        if (userName1 == null){
            userName1 = getRDString();
            actionSignUp(userName1);
        }
        if (userName2 == null) {
            userName2 = getRDString();
            actionSignUp(userName2);
        }

        actionLogin(userName1);
        solo.enterText((EditText) solo.getView(R.id.search_text), userName2);
        solo.clickOnText("Search");
        solo.clearEditText((EditText) solo.getView(R.id.search_text));
        assertTrue(solo.searchText(userName2));
        solo.clickOnText(userName2);
        solo.clickOnText("Send");
        actionLogOut();

        actionLogin(userName2);
        solo.clickOnText("Follow Requests");
        solo.clickOnText(userName1);
        solo.clickOnText("Accept");
        solo.clickOnText("Followers");
        assertTrue(solo.searchText(userName1));
        solo.goBack();

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.fab));
        solo.assertCurrentActivity("Wrong Activities", ViewMood.class);
        Spinner emoij = (Spinner) solo.getView(R.id.spinner_emotion);
        solo.clickOnView(emoij);
        solo.clickOnText("Anger");
        solo.enterText((EditText) solo.getView(R.id.view_date), "TODAY");
        solo.clickOnText("Save Mood");
        solo.clickOnText("Save Mood");
        solo.sendKey(solo.MENU);
        solo.clickOnText("Log Out");

        actionLogin(userName1);
        solo.clickOnText("Following");
        assertTrue(solo.searchText(userName2));
        solo.goBack();

        solo.clickOnText("Following");
        assertTrue(solo.searchText(userName2));
        assertTrue(solo.searchText("Anger"));

        solo.sendKey(solo.MENU);
        solo.clickOnText("Log Out");
    }

    public void test4_DeclineRequest() {
        if (userName1 == null){
            userName1 = getRDString();
            actionSignUp(userName1);
        }
        if (userName2 == null) {
            userName2 = getRDString();
            actionSignUp(userName2);
        }

        actionLogin(userName1);
        solo.enterText((EditText) solo.getView(R.id.search_text), userName2);
        solo.clickOnText("Search");
        solo.clearEditText((EditText) solo.getView(R.id.search_text));
        assertTrue(solo.searchText(userName2));
        solo.clickOnText(userName2);
        solo.clickOnText("Send");
        actionLogOut();

        actionLogin(userName2);
        solo.clickOnText("Follow Requests");
        solo.clickOnText(userName1);
        solo.clickOnText("Decline");
        solo.clickOnText("Followers");
        assertFalse(solo.searchText(userName1));
        solo.goBack();

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.fab));
        solo.assertCurrentActivity("Wrong Activities", ViewMood.class);
        Spinner emoij = (Spinner) solo.getView(R.id.spinner_emotion);
        solo.clickOnView(emoij);
        solo.clickOnText("Sad");
        solo.enterText((EditText) solo.getView(R.id.view_date), "TODAY");
        solo.clickOnText("Save Mood");
        solo.clickOnText("Save Mood");
        solo.sendKey(solo.MENU);
        solo.clickOnText("Log Out");

        actionLogin(userName1);
        solo.clickOnText("Following");
        assertFalse(solo.searchText(userName2));
        solo.goBack();

        solo.clickOnText("Following");
        assertFalse(solo.searchText(userName2));
        assertFalse(solo.searchText("Sad"));

        solo.sendKey(solo.MENU);
        solo.clickOnText("Log Out");
    }

}