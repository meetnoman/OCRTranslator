package com.example.translator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    ArrayList<HashMap<String,String>> mData;
    ArrayList<HashMap<String,Bitmap>> mImage;
    LayoutInflater mInflater;
    private IMyItemClickListener mClickListener;



    public RecyclerViewAdapter(Context context,ArrayList<HashMap<String, String>> data,ArrayList<HashMap<String, Bitmap>> image
    ,IMyItemClickListener iMyItemClickListener){

        this.mInflater=LayoutInflater.from(context);
        this.mData=data;
        this.mImage=image;
        this.mClickListener=iMyItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=mInflater.inflate(R.layout.recycler_row, parent,false);
        return new ViewHolder(view,mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        HashMap<String,String> data=mData.get(position);
        HashMap<String,Bitmap> image=mImage.get(position);

        holder.translation.setText(data.get("inputLanguage")+" To "+data.get("outputLanguage")+" Translation");

            if (image.get("image")==null){

                holder.textEnter.setVisibility(View.VISIBLE);
                holder.textEnter.setText(data.get("text"));
                holder.imageView.setVisibility(View.GONE);
            }else {
                holder.imageView.setImageBitmap(image.get("image"));
            }

        holder.timerView.setText(data.get("date"));
//        holder.delete.setText("Delete");


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView viewTranslation,timerView,translation,textEnter;
        ImageView imageView;
        Button delete;

        IMyItemClickListener clickListener;

        public ViewHolder(@NonNull View itemView,IMyItemClickListener iMyItemClickListener) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            viewTranslation=itemView.findViewById(R.id.viewTranslation);
            translation=itemView.findViewById(R.id.translation);
            textEnter=itemView.findViewById(R.id.textEnter);
            delete=itemView.findViewById(R.id.delete);
            timerView=itemView.findViewById(R.id.timer);

            this.clickListener=iMyItemClickListener;

            viewTranslation.setOnClickListener(this);
            delete.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(clickListener != null) {

                if (view.getId()==viewTranslation.getId()) {
                    clickListener.onMyItemClick(getAdapterPosition());
                    //Toast.makeText( view.getContext(),"View Translation Position:"+getAdapterPosition(),Toast.LENGTH_SHORT).show();
                }
                else if (view.getId()==delete.getId()){
                    clickListener.onDeleteItemClick(getAdapterPosition());
                   mData.remove(getAdapterPosition());
                   mImage.remove(getAdapterPosition());
                   notifyDataSetChanged();
                     //Toast.makeText( view.getContext(),"Delete Position:",Toast.LENGTH_SHORT).show();
                }



            }
        }
    }

    HashMap<String, String> getItem(int id)
    {
        return mData.get(id);
    }

    public void setClickListener(IMyItemClickListener itemClickListener)
    {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this interface (method) to respond to click events
    public interface IMyItemClickListener
    {
        void onMyItemClick( int position);
        void onDeleteItemClick(int position);
    }




}

