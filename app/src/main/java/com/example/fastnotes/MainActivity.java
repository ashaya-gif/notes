package com.example.fastnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech t1;
    String filepath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // File mediaStorageDir= new File(getApplicationContext().getExternalFilesDir( null).getPath(),"/aaa");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


                //File write logic here
                // return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);


            }
            // File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
            //       + "/aaaaa");
            // if (! mediaStorageDir.exists()){
            //   if (! mediaStorageDir.mkdirs()){
            //   }
            //  }



            t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.UK);
                    }
                }
            });

            ImageView buttonspeak = findViewById(R.id.btnspeak);
            buttonspeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvn = findViewById(R.id.tvnnotes);
                    String toSpeak = tvn.getText().toString();
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);


                }
            });



            ImageView buttonshare = findViewById(R.id.btnshare);
            buttonshare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvn=findViewById(R.id.tvnnotes);

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, tvn.getText().toString());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });


        }

       // private void sfile(String mtext){

        }
   // }

    private void saveToFile(String okok) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "/notes/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "abc.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(okok);
            writer.flush();
            writer.close();
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
   private String readFile(String filep) {



       File fileEvents = new File(filep);




       StringBuilder text = new StringBuilder();
       try {
           BufferedReader br = new BufferedReader(new FileReader(fileEvents));
           String line;
           while ((line = br.readLine())!= null) {
               text.append(line);
               text.append('\n');
           }
           br.close();
       } catch (IOException e) { }
       String result = text.toString();
       return result;
   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1002:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri fileUri = data.getData();
                       // Log.i(LOG_TAG, "Uri: " + fileUri);


                        try {
                            filepath =  getPath(fileUri);
                            TextView tvn = findViewById(R.id.tvnnotes);
                            tvn.setText(readFile(filepath));
                        } catch (Exception e) {
                           // Log.e(LOG_TAG, "Error: " + e);
                           // Toast.makeText(this.getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Uri uri) {

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }
    @Override
    public void onBackPressed() {
//  save code
        TextView tvn = findViewById(R.id.tvnnotes);
        String mynotes = tvn.getText().toString();
        saveToFile(mynotes);
        super.onBackPressed();
    }




    @Override
    protected void onResume() {
        super.onResume();

        TextView tvn=findViewById(R.id.tvnnotes);

        String filepath= Environment.getExternalStorageDirectory().getPath()
                +  File.separator + "notes" + File.separator +"abc.txt";
        tvn.setText(readFile(filepath));
        // open code
    }
    @Override
    protected void onPause() {
        super.onPause();
//save code
        TextView tvn = findViewById(R.id.tvnnotes);
        String mynotes = tvn.getText().toString();
        saveToFile(mynotes);

    }

}