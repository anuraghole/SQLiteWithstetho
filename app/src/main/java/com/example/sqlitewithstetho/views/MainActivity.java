package com.example.sqlitewithstetho.views;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlitewithstetho.R;
import com.example.sqlitewithstetho.database.DatabaseHelper;
import com.example.sqlitewithstetho.database.model.Note;
import com.example.sqlitewithstetho.utils.MyDividerItemDecoration;
import com.example.sqlitewithstetho.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private NotesAdapter mAdapter;
    private List<Note> noteList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView mNotesView;
    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        mNotesView = findViewById(R.id.empty_notes_view);

        db = new DatabaseHelper(this);
        noteList.addAll(db.getAllNotes());

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteDialog(false, null, -1);
               /* Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
                sendIntent.putExtra("Anurag", PhoneNumberUtils.stripSeparators("919503583481")+"@s.whatsapp.net");
                startActivity(sendIntent);*/
            }
        });
        mAdapter = new NotesAdapter(this, noteList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionDialog(position);

            }
        }));
    }
    //changes done

    private void createNote(String note) {
        long id = db.insertNote(note);

        Note n = db.getNote(id);
        if (n != null) {
            noteList.add(0, n);

            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();

        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */

    private void updateNote(String note, int position) {
        Note n = noteList.get(position);
        n.setNote(note);
        db.updateNote(n);

        noteList.set(position, n);

        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */

    private void deleteNote(int position) {
        db.deleteNote(noteList.get(position));
        noteList.remove(position);
        mAdapter.notifyItemRemoved(position);
        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, noteList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflater= LayoutInflater.from(getApplicationContext());
        View view=layoutInflater.inflate(R.layout.note_dailog,null);

        AlertDialog.Builder alertDialogBuilderUserInput=new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote=view.findViewById(R.id.note);
        TextView dialogTitle=view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note!=null){
            inputNote.setText(note.getNote());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alertDialog=alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if no text enter

                if (TextUtils.isEmpty(inputNote.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter Note !", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    alertDialog.dismiss();
                }

                if (shouldUpdate && note!=null){
                    updateNote(inputNote.getText().toString(),position);
                }else {
                    createNote(inputNote.getText().toString());
                }
            }
        });
    }
    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        if (db.getNoteCount()>0){
            mNotesView.setVisibility(View.GONE);
        }else {
            mNotesView.setVisibility(View.VISIBLE);
        }
    }

}
