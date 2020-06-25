package com.example.translator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.translator.DatabaseHelper.DatabaseHelper;

import java.sql.Blob;
import java.sql.SQLException;
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

    DatabaseHelper databaseHelper=new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        DatabaseHelper obj=new DatabaseHelper(this);
        Cursor cursor=obj.getHistoryResult();

        data=new ArrayList<>();
        allImages=new ArrayList<>();

        if (cursor.moveToLast()){
            do{
               String id = String.valueOf(cursor.getInt(0));
                String text = cursor.getString(1);
                String translation = String.valueOf(cursor.getString(2));
               String inputLanguage=cursor.getString(4);
               String outputLanguage=cursor.getString(5);
                String date=  cursor.getString(6);



                mapdata = new HashMap<>();
                mapImage=new HashMap<>();
                mapdata.put("id", id);
                mapdata.put("text", text);
                mapdata.put("translation", translation);
                mapdata.put("inputLanguage",inputLanguage);
                mapdata.put("outputLanguage",outputLanguage);

                byte[] image = cursor.getBlob(3);

                    if (image == null) {
                        mapImage.put("image",null);
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                        mapImage.put("image",  bitmap);
                    }


                mapdata.put("date",date);

               allImages.add(mapImage);
                data.add(mapdata);

             }while (cursor.moveToPrevious());
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
     //    Toast.makeText(getApplicationContext(),adapter.getItem(position).get("translation"),Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),adapter.getItem(position).get("id")+" Positon: "+position,Toast.LENGTH_SHORT).show();


        Intent intent=new Intent(this,TextTranslation.class);
        intent.putExtra("textExtracted",adapter.getItem(position).get("text"));
        intent.putExtra("textTranslation",adapter.getItem(position).get("translation"));
        this.startActivity(intent);

    }

    @Override
    public void onDeleteItemClick(int position) {
       // Toast.makeText(getApplicationContext(),"PAk", Toast.LENGTH_SHORT);
        String no=databaseHelper.deleteRecord("history",adapter.getItem(position).get("id"));
        if (no.equalsIgnoreCase("1")){
          //  Toast.makeText(getApplicationContext(),"Superb record Delted: "+no,Toast.LENGTH_SHORT).show();
        }else {
           //Toast.makeText(getApplicationContext(), "OOps record not deleted: "+no, Toast.LENGTH_SHORT).show();
        }
    }





}



