package com.example.sqlitewithstetho.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sqlitewithstetho.R;
import com.example.sqlitewithstetho.database.model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    private Context context;
    private List<Note> notesList;

    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Note note=notesList.get(position);
        holder.note.setText(note.getNote());
        holder.dot.setText(Html.fromHtml("&#8226;"));
        holder.timestamp.setText(dateFormat(note.getTimestamp()));

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
    public String dateFormat(String dateStr){

        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date=sdf.parse(dateStr);
            SimpleDateFormat sdfOut=new SimpleDateFormat("MMM d");
            return sdfOut.format(date);

        }catch (ParseException e){

        }
        return "";
    }

    public   class  MyViewHolder extends RecyclerView.ViewHolder{
        public TextView note;
        public TextView dot;
        public TextView timestamp;
        public MyViewHolder(View view) {
            super(view);

            note = view.findViewById(R.id.note);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }
}
