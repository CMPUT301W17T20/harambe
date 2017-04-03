package com.example.moodly.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.moodly.Controllers.CommentController;
import com.example.moodly.Controllers.MoodController;
import com.example.moodly.Models.Emotion;
import com.example.moodly.Models.Mood;
import com.example.moodly.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.moodly.Models.Emotion.NONE;

/**
 * ViewMood allows the user to view their
 * selected mood and it's relevant details
 */
public class ViewMood extends AppCompatActivity {

    private Mood mood;
    private EditText editReasonText;
    private EditText editDate;
    private TextView viewReasonText;
    private TextView viewDate;

    private Spinner emotionSpinner;
    private Spinner socialSituationSpinner;
    private Button saveButton;
    private Button addComments;
    private Button viewComments;
    private Button viewMoodComment;
    private FloatingActionButton cameraButton;
    private FloatingActionButton map;
    private ImageView moodImage;

    private Uri imageUri;

    private String base64;
    private String imagePath;
    private String setPhotoPath;
    private String base64Encoded;
    private String date;
    private Date currentDate;

    private int position = -1;
    private int edit = 0;
    private double lat = 0;
    private double lon = 0;

    /**
     * Gets the mood event and position,
     * sets view and listeners to it.
     * @param savedInstanceState
     * @see #setViews()
     * @see #setListeners()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mood = MoodController.getInstance().getMood();
        position = getIntent().getIntExtra("MOOD_POSITION", -1);
        edit = getIntent().getIntExtra("edit",0);

        if (edit == 0) {
            setContentView(R.layout.activity_view_mood);
            android.support.v7.app.ActionBar action = getSupportActionBar();
            if (position == -1) {
                action.setTitle("Add Mood");
            }
            else{
                action.setTitle("Editing Mood");
            }
        }
        else{
            setContentView(R.layout.activity_view_social);
            android.support.v7.app.ActionBar action = getSupportActionBar();
            action.setTitle("Viewing Mood Event");
        }
        setViews();
        setListeners();
    }

    /**
     * Sets the spinners for emotional state and
     * social situation options.
     */
    protected void setSpinners() {

        ArrayList<String> emotionList = new ArrayList<>();
        emotionList.add("Choose an emotion");
        emotionList.add("Anger");
        emotionList.add("Confusion");
        emotionList.add("Disgust");
        emotionList.add("Fear");
        emotionList.add("Happiness");
        emotionList.add("Sadness");
        emotionList.add("Shame");
        emotionList.add("Surprise");

        ArrayAdapter<String> emotionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emotionList);
        emotionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotionSpinner.setAdapter(emotionAdapter);

        ArrayList<String> ssList = new ArrayList<>();
        ssList.add("No social situation");
        ssList.add("Alone");
        ssList.add("With one other person");
        ssList.add("With two to several people");
        ssList.add("With a crowd");

        ArrayAdapter<String> ssAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,ssList);
        ssAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(ssAdapter);
    }


    protected void setViews() {
        // Taken from http://stackoverflow.com/questions/13408419/how-do-i-tell-if-intent-extras-exist-in-android 3/8/2017 22:08
        //https://www.tutorialspoint.com/android/android_spinner_control.htm
        emotionSpinner = (Spinner) findViewById(R.id.spinner_emotion);
        socialSituationSpinner = (Spinner) findViewById(R.id.spinner_SS);
        setSpinners();
        emotionSpinner.setSelection(mood.getEmotion());
        socialSituationSpinner.setSelection(mood.getSocialSituation());
        moodImage = (ImageView) findViewById(R.id.moodImage);
        viewMoodComment = (Button) findViewById(R.id.viewMoodComments);
        map = (FloatingActionButton) findViewById(R.id.mapButton);
        base64Encoded = mood.getImage();
        if (base64Encoded != null) {
            decodeFromBase64(base64Encoded);
        }

        if (edit == 0) {
            // set editable location pin if not null
            saveButton = (Button) findViewById(R.id.saveButton);
            cameraButton = (FloatingActionButton) findViewById(R.id.cameraButton);
            editDate = (EditText) findViewById(R.id.view_date);
            editReasonText = (EditText) findViewById(R.id.view_reason);
            currentDate = mood.getDate();
            editDate.setText(currentDate.toString(), TextView.BufferType.EDITABLE);
            if (position == -1){
                viewMoodComment.setVisibility(Button.INVISIBLE);
            }
            else {
                editReasonText.setText(mood.getReasonText(), TextView.BufferType.EDITABLE);
            }
        }
        else{
            // set Location pin if not null
            addComments = (Button)findViewById(R.id.addComments);
            viewComments = (Button) findViewById(R.id.viewComments);
            viewDate = (TextView) findViewById(R.id.view_date);
            viewReasonText = (TextView) findViewById(R.id.view_reason);
            viewDate.setText(mood.getDate().toString());
            viewReasonText.setText(mood.getReasonText());
            socialSituationSpinner.setEnabled(false);
            emotionSpinner.setEnabled(false);
        }
    }

    /**
     * Set save button listener to validate
     * and save edited mood event from user.
     *
     */
    protected void setListeners() {
        if (edit == 0) {
            checkPermissions();
            // Taken from http://stackoverflow.com/questions/6302057/is-it-possible-that-when-click-edittext-it-will-show-dialog-message 3/29/2017
            editDate.setClickable(true);
            editDate.setOnClickListener(new View.OnClickListener() {
                String time;
                @Override
                public void onClick(View v) {
                    Calendar calendar;
                    final int year,month,day, hour, minute;
                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);

                    DatePickerDialog dpd = new DatePickerDialog(ViewMood.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            date = String.format("%02d/%02d/%d ",month+1,dayOfMonth,year);
                            TimePickerDialog tpd = new TimePickerDialog(ViewMood.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    time = String.format("%02d:%02d",hourOfDay,minute);
                                    date = date.concat(time);
                                    editDate.setText(date);
                                }
                            }, hour, minute, false);
                            tpd.setTitle("Select time");
                            tpd.show();
                        }
                    },year,month,day);
                    dpd.setTitle("Select date");
                    dpd.show();
                }
            });
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodPhoto();
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    String reasonText = editReasonText.getText().toString();
                    String reasonTrimmed = reasonText.trim();
                    int words = reasonTrimmed.isEmpty() ? 0 : reasonTrimmed.split("\\s+").length;
                    Emotion emotionEnumCheck = Emotion.values()[emotionSpinner.getSelectedItemPosition()];
                    if (emotionEnumCheck == NONE) {
                        // Taken from http://stackoverflow.com/questions/28235689/how-can-an-error-message-be-set-for-the-spinner-in-android 3/8/2017
                        TextView errorText = (TextView) emotionSpinner.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);
                        errorText.setText("Emotion required");
                    }
                    // Taken from http://stackoverflow.com/questions/8924599/how-to-count-the-exact-number-of-words-in-a-string-that-has-empty-spaces-between 3/22/2017
                    else if(reasonText.length() > 20 || words > 3 ){
                        editReasonText.setError("Reason must be less than 20 characters or 3 words.");
                    }
                    else if (editDate.getText().toString().trim().equals("")) {
                        editDate.setError("Date is required.");
                    }
                    else {
                        int emotionEnum = emotionSpinner.getSelectedItemPosition();
                        int socialEnum = socialSituationSpinner.getSelectedItemPosition();
                        if (position == -1 && moodImage.getTag() != null) {
                            setPhotoPath = moodImage.getTag().toString();
                            compressPhoto(setPhotoPath);
                            base64 = convertToBase64(setPhotoPath);
                            mood.setImage(base64);
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.CANADA);
                        Date selectedDate = new Date();
                        try {
                            selectedDate = sdf.parse(date);
                        } catch (Exception e) {
                            Log.i("Error","Could not convert string to date.");
                        }
                        if (lat != 0 && lon != 0) {
                            mood.setLocation(lat, lon);
                        }
                        if (date == null){
                            mood.setDate(currentDate);
                        }
                        else{
                            mood.setDate(selectedDate);
                        }
                        mood.setReasonText(reasonText);
                        mood.setEmotion(emotionEnum);
                        mood.setSocialSituation(socialEnum);
                        MoodController.getInstance().setMood(mood);
                        MoodController.getInstance().addMood(position, mood);
                        Intent output = new Intent(ViewMood.this, ViewMoodList.class);
                        setResult(RESULT_OK, output);
                        finish();
                    }
                }

            });
            viewMoodComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentController.getInstance();
                    Intent intent = new Intent(ViewMood.this, ViewComments.class);
                    intent.putExtra("moodID",mood.getId());
                    startActivity(intent);
                }
            });
        }
        else{
            addComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewMood.this);
                    builder.setTitle("Enter your comment");
                    final EditText input = new EditText(ViewMood.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String comment = input.getText().toString();
                            CommentController.getInstance().addComment(comment,mood.getId());
                            Toast.makeText(ViewMood.this, "Comment added!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });

            viewComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentController.getInstance();
                    Intent intent = new Intent(ViewMood.this, ViewComments.class);
                    intent.putExtra("moodID",mood.getId());
                    startActivity(intent);
                }
            });
        }
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMap = new Intent();
                intentMap.setClass(ViewMood.this, SeeMap.class);
                Mood.GeoLocation setLocation = mood.getLocation();
                if (position != 1)
                {
                    Double oldLat = setLocation.lat;
                    Double oldLon = setLocation.lon;
                    intentMap.putExtra("lat", oldLat);
                    intentMap.putExtra("lon", oldLon);
                }
                if (edit != 0) {
                    intentMap.putExtra("editable", 1);
                    if (setLocation.lat == 0 && setLocation.lon == 0){
                        Toast.makeText(ViewMood.this,"User has not set a location.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        startActivityForResult(intentMap,1);
                    }
                }
                else {
                    startActivityForResult(intentMap, 1);
                }
            }
        });

    }

    // Taken from https://github.com/CMPUT301W17T20/MyCameraTest1
    protected void moodPhoto(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MoodlyPhotos";
        File folder = new File(path);
        if(!folder.exists())
            folder.mkdirs();
        imagePath = path + File.separator + String.valueOf(System.currentTimeMillis()) + ".png";
        File imageFile = new File(imagePath);
        imageUri = Uri.fromFile(imageFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (requestCode == 0){
            if (resultCode == RESULT_OK) {
                moodImage.setImageDrawable(Drawable.createFromPath(imageUri.getPath()));
                moodImage.setTag(imageUri.getPath());
            }
        }
        else{
            if (resultCode == RESULT_OK){
                lat = intent.getDoubleExtra("lat",0);
                lon = intent.getDoubleExtra("long",0);
            }
        }
    }

    // Taken from http://stackoverflow.com/questions/477572/strange-out-of-memory-issue-while-loading-an-image-to-a-bitmap-object/ 3/29/2017
    protected void compressPhoto(String f){
        final int limit = 60;
        Bitmap b;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(f);
        } catch (Exception e) {
            Log.i("Error","File not found");
        }

        BitmapFactory.decodeStream(fis,null,o);

        try {
            fis.close();
        } catch (Exception e) {
            Log.i("Error","File not closed");
        }

        int scale = 1;
        while(o.outWidth / scale / 2 >= limit && o.outHeight / scale / 2 >= limit){
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;

        try {
            fis = new FileInputStream(f);
        } catch (Exception e) {
            Log.i("Error","File not found");
        }

        b = BitmapFactory.decodeStream(fis,null,o2);
        System.out.println(b.getAllocationByteCount());

        try {
            fis.close();
        } catch (Exception e) {
            Log.i("Error","File not closed");
        }
    }

    // Taken from http://stackoverflow.com/questions/25299438/how-do-i-get-the-image-that-i-took-and-submit-it-to-my-server 3/22/2017
    protected String convertToBase64(String location){
        FileInputStream fis = null;
        byte[] b;
        int bytesRead;
        try{
            fis = new FileInputStream(location);
        } catch(FileNotFoundException e){
            Log.i("Error","File not found");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8096];
        try{
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e){
            Log.i("Error","Cannot write to buffer");
        }
        b = baos.toByteArray();
        base64 = Base64.encodeToString(b, Base64.DEFAULT);
        return base64;
    }

    protected void decodeFromBase64(String toBeDecoded){
        // Taken from http://stackoverflow.com/questions/4837110/how-to-convert-a-base64-string-into-a-bitmap-image-to-show-it-in-a-imageview 3/23/2017
        byte[] decodedText = Base64.decode(toBeDecoded,Base64.DEFAULT);
        Bitmap decodedPhoto = BitmapFactory.decodeByteArray(decodedText,0,decodedText.length);
        moodImage.setImageBitmap(decodedPhoto);
    }

    // Taken from https://developer.android.com/training/permissions/requesting.html
    protected void checkPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions(ViewMood.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(ViewMood.this,new String[]{Manifest.permission.CAMERA},1);
            }
        }
    }

}
