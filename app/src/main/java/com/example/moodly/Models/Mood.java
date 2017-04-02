//package com.example.moodly;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//import android.os.SystemClock;
//
//import java.util.Date;
//
///**
// * Created by mliew on 2017-02-25.
// */
//
///**
// * Enum for emotion
// */
//enum Emotion {
//    NONE,
//    ANGER,
//    CONFUSION,
//    DISGUST,
//    FEAR,
//    HAPPINESS,
//    SADNESS,
//    SHAME,
//    SUPRISE
//}
//
///**
// * Enum for social situation
// */
//enum SocialSituation {
//    NONE,
//    ALONE,
//    ONE,
//    SEVERAL,
//    CROWD,
//}
//
//public class Mood implements Parcelable {
//    private Date date;
//    private String owner;
//    private String location;
//    private String trigger;
//    private String reasonText;
//    private String image;
//    private Emotion emotion;
//    private SocialSituation socialSituation;
//
//    /**
//     * The constructor for the mood class
//     */
//    public Mood() {
//        this.date = new Date();
//        this.owner = "Placeholder";
//        this.location = "";
//        this.trigger = "";
//        this.emotion = Emotion.NONE;
//        this.socialSituation = SocialSituation.NONE;
//        this.reasonText = "";
//        this.image = null;
//    }
//
//    /**
//     * Used to read mood object passed from another activity
//     * @param in
//     */
//    private Mood(Parcel in) {
//
//        //http://stackoverflow.com/questions/21017404/reading-and-writing-java-util-date-from-parcelable-class
//        long tempDate = in.readLong();
//        this.date = tempDate == -1 ? null : new Date(tempDate);
//        owner = in.readString();
//        location = in.readString();
//        trigger = in.readString();
//        reasonText = in.readString();
//        emotion = Emotion.valueOf(in.readString());
//        socialSituation = socialSituation.valueOf(in.readString());
//        image = in.readString();
//    }
//
//    /**
//     * Used to write object to be passed to another activity
//     * @param out
//     * @param flags
//     */
//    public void writeToParcel(Parcel out, int flags) {
//        out.writeLong(date != null ? date.getTime() : -1);
//        out.writeString(owner);
//        out.writeString(location);
//        out.writeString(trigger);
//        out.writeString(reasonText);
//        out.writeString(emotion.name());
//        out.writeString(socialSituation.name());
//        out.writeString(image);
//    }
//
//    public int describeContents() {
//        return 0;
//    }
//
//    public static final Parcelable.Creator<Mood> CREATOR = new Parcelable.Creator<Mood>() {
//        public Mood createFromParcel(Parcel in) {
//            return new Mood(in);
//        }
//
//        public Mood[] newArray(int size) {
//            return new Mood[size];
//        }
//    };
//
//
//    public Date getDate() {
//        return date;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }
//
//    public String getOwner() {
//        return owner;
//    }
//
//    public void setOwner(String owner) {
//        this.owner = owner;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public String getTrigger() {
//        return trigger;
//    }
//
//    public void setTrigger(String trigger) {
//        this.trigger = trigger;
//    }
//
//    public Emotion getEmotion() {
//        return emotion;
//    }
//
//    public void setEmotion(Emotion emotion) {
//        this.emotion = emotion;
//    }
//
//    public SocialSituation getSocialSituation() {
//        return socialSituation;
//    }
//
//    public void setSocialSituation(SocialSituation socialSituation) {
//        this.socialSituation = socialSituation;
//    }
//
//    public String getReasonText() {
//        return reasonText;
//    }
//
//    public void setReasonText(String reasonText) {
//        this.reasonText = reasonText;
//    }
//
//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
//}

package com.example.moodly.Models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import com.example.moodly.Controllers.MoodController;
import com.example.moodly.Controllers.UserController;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Date;

import io.searchbox.annotations.JestId;
/**
 * Created by mliew on 2017-02-25.
 */
public class Mood {
    private Date date;
    private String owner;
    private String trigger;
    private String reasonText;
    private String image;
    private Integer emotion;
    private Integer socialSituation;
    private GeoLocation geo_location;

    private class GeoLocation {

        public double lat;

        public double lon;


        public GeoLocation(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }


    @JestId
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    /**
     * The constructor for the mood class
     */
    public Mood() {
        this.date = new Date();
        this.owner = UserController.getInstance().getCurrentUser().getName();
        this.geo_location = new GeoLocation(0, 0);
        this.trigger = "";
        this.emotion = 0;
        this.socialSituation = 0;
        this.reasonText = "";
        this.image = null;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public GeoLocation getLocation() {
        return geo_location;
    }

    public void setLocation(double lat, double lon) {
        this.geo_location.lat = lat;
        this.geo_location.lon = lon;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public int getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(int socialSituation) {
        this.socialSituation = socialSituation;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}