package com.example.translator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.translator.DatabaseHelper.DatabaseHelper;

import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class historyActivity extends AppCompatActivity implements RecyclerViewAdapter.IMyItemClickListener{
    RecyclerViewAdapter adapter;
    ArrayList<HashMap<String,String>> data;
    HashMap<String, String> mapdata;

    ArrayList<HashMap<String,Bitmap>> allImages;
    HashMap<String,Bitmap> mapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        DatabaseHelper obj=new DatabaseHelper(this);
        Cursor cursor=obj.getQueryResult();

        data=new ArrayList<>();
        allImages=new ArrayList<>();

        if (cursor.moveToFirst()){
            do{
               String id = String.valueOf(cursor.getInt(0));
                String text = cursor.getString(1);
                String translation = String.valueOf(cursor.getString(2));
               byte[] image = cursor.getBlob(3);
               String inputLanguage=cursor.getString(4);
               String outputLanguage=cursor.getString(5);
                String date=  cursor.getString(6);


                Bitmap bitmap= BitmapFactory.decodeByteArray(image, 0 , image.length);

                mapdata = new HashMap<>();
                mapImage=new HashMap<>();
                mapdata.put("id", id);
                mapdata.put("text", text);
                mapdata.put("translation", translation);
                mapdata.put("inputLanguage",inputLanguage);
                mapdata.put("outputLanguage",outputLanguage);
               mapImage.put("image", bitmap);

                mapdata.put("date",date);

               allImages.add(mapImage);
                data.add(mapdata);

             }while (cursor.moveToNext());
        }





        RecyclerView recyclerView=findViewById(R.id.recyclerView) ;
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new RecyclerViewAdapter(this,data,allImages,this);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onMyItemClick(  int position) {
       // Toast.makeText(getApplicationContext(),adapter.getItem(position).get("translation"),Toast.LENGTH_SHORT).show();

        Intent intent=new Intent(this,TextTranslation.class);
        intent.putExtra("textExtracted",adapter.getItem(position).get("text"));
        intent.putExtra("textTranslation",adapter.getItem(position).get("translation"));
        this.startActivity(intent);

    }

}
