package com.dmytro.caller;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.webrtc.IceCandidate;


public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Communicator communicator = Communicator.getInstance(this);

        //visualizing local media stream
        communicator.visualizeMediaStream((GLSurfaceView) this.findViewById(R.id.calleeView),
                communicator.getLocalMediaStream());

        IceCandidate[] iceCandidates = communicator.getListOfUsers();

        communicator.makeCall(iceCandidates[0]); //todo rewrite this temp line

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
