package com.tom.luckydraw;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Random;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private ArrayAdapter<String> adapter;
    private ListView list;
    private TextView counter;
    int count = 0;
    int drawCount = 0;
    Random random = new Random();
    private LinearLayout drawingContaner;
    private Spinner luckys;
    private ArrayAdapter<String> luckyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        luckyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1 );
        luckys.setAdapter(luckyAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "抽獎中", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startDrawing();
            }
        });
        Firebase.setAndroidContext(this);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        list.setAdapter(adapter);
        Firebase poolRef = new Firebase("https://project-2300394258190908664.firebaseio.com/pool");
        poolRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add((String) dataSnapshot.child("name").getValue());
                count++;
                updateCounter();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.remove((String) dataSnapshot.child("name").getValue());
                count--;
                updateCounter();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "onCancelled");
            }
        });
    }

    private void startDrawing() {
//        drawingContaner.setVisibility(View.VISIBLE);
        int randomSecond = random.nextInt(60)+30;
        new DrawingTask().execute(randomSecond);
        /*DrawingThread drawing = new DrawingThread(randomSecond);
        drawing.start();
        int lucky = random.nextInt(count);
        luckyPosition.setText(String.valueOf(lucky));
        String name = adapter.getItem(lucky);
        luckyName.setText(name);*/
    }

    class DrawingTask extends AsyncTask<Integer, Integer, Integer>{
        private int position;
        private AlertDialog dialog;
        private TextView luckyPosition;
        private TextView luckyName;
        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setView(R.layout.drawing)
                    .setTitle("抽獎中")
                    .setPositiveButton("OK", null).show();
            luckyPosition = (TextView) dialog.findViewById(R.id.drawing_position);
            luckyName = (TextView) dialog.findViewById(R.id.drawing_name);
//            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            for (int i=0; i<params[0]; i++){
                position = random.nextInt(count);
                publishProgress(position);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return position;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int pos  = values[0];
            luckyPosition.setText(String.valueOf(pos));
            luckyName.setText(adapter.getItem(pos));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            int lucky = integer;
            luckyName.setTextColor(Color.MAGENTA);
            luckyPosition.setTextColor(Color.MAGENTA);
            //
            String name = luckyName.getText().toString();
            adapter.remove(name);
            luckyAdapter.add(name);
            luckyAdapter.notifyDataSetChanged();
            count = adapter.getCount();
            updateCounter();
//            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        }
    }

    private void findViews() {
        list = (ListView) findViewById(R.id.list);
        counter = (TextView) findViewById(R.id.counter);
//        drawingContaner = (LinearLayout) findViewById(R.id.drawing_container);
        luckys = (Spinner) findViewById(R.id.luckys);

    }

    private void updateCounter() {
        counter.setText(String.valueOf(count));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
