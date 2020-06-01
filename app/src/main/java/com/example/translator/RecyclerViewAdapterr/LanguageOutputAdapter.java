package com.example.translator.RecyclerViewAdapterr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.translator.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LanguageOutputAdapter extends RecyclerView.Adapter<LanguageOutputAdapter.ViewHolder> {


    List<String> mLang;
    List<Integer> mDownload;
    LayoutInflater mInflater;
    private IMyItemClickListener mClickListener;

    public LanguageOutputAdapter(Context context, List<String> data, List<Integer> download, IMyItemClickListener iMyItemClickListener){
        this.mInflater=LayoutInflater.from(context);
        mLang=data;
        mDownload=download;
        this.mClickListener=iMyItemClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.recycler_row_langauge_output, parent,false);
        return new ViewHolder(view,mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String lang=mLang.get(position);
        int down=mDownload.get(position);
        holder.outputLanguage.setText(lang);
        if (down==1){
            holder.outputLanguage.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);

        }else {
            holder.outputLanguage.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_file_download, 0);
        }

    }

    @Override
    public int getItemCount() {
        return mLang.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Button outputLanguage;
        IMyItemClickListener clickListener;


        public ViewHolder(@NonNull View itemView, IMyItemClickListener iMyItemClickListener ) {
            super(itemView );
            outputLanguage=itemView.findViewById(R.id.outputLanguage);
            this.clickListener=iMyItemClickListener;

            outputLanguage.setOnClickListener(this);

        }
        String getItem(int id)
        {
            return mLang.get(id);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onMyItemClick(getAdapterPosition());
            }
        }
     }


    public void setClickListener( IMyItemClickListener itemClickListener)
    {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this interface (method) to respond to click events
    public interface IMyItemClickListener
    {
        void onMyItemClick( int position);
    }


}


