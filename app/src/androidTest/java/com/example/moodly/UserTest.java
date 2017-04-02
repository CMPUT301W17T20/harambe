package com.example.moodly;

import android.test.ActivityInstrumentationTestCase2;

import com.example.moodly.Models.User;

/**
 * Created by MinhNguyen on 07/03/2017.
 */

public class UserTest extends ActivityInstrumentationTestCase2 {
    public UserTest() {
        super(User.class);
    }

    public void testNewUser(){
        User me = new User();
        String name = "hatuongminh96";
        me.setName(name);
        assertEquals(me.getName(), name);
    }

    public void testAddFriend() {
        //Test both adding follower and following
        User me = new User();
        String name = "hatuongminh96";
        me.setName(name);

        User myPal = new User();
        String palName = "1";
        myPal.setName(palName);

        me.addFollower(myPal);
        assertTrue(me.getFollowers().contains(myPal.getName()));
        assertTrue(myPal.getFollowing().contains(me.getName()));
    }

    public void testRemoveFriend() {
        // Test rm follower and following
        User me = new User();
        String name = "hatuongminh96";
        me.setName(name);

        User myPal = new User();
        String palName = "1";
        myPal.setName(palName);

        me.addFollowing(myPal);
        assertTrue(me.getFollowing().contains(myPal.getName()));
        assertTrue(myPal.getFollowers().contains(me.getName()));

        me.removeFollowing(myPal);
        assertFalse(me.getFollowing().contains(myPal.getName()));
        assertFalse(myPal.getFollowers().contains(me.getName()));
    }
}
