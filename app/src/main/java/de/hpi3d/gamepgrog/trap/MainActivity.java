package de.hpi3d.gamepgrog.trap;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent registerPlayer = new Intent(this, BackendManagerIntentService.class);
        registerPlayer.putExtra(BackendManagerIntentService.KEY_MANAGE_TYPE, BackendManagerIntentService.MANAGE_PLAYER_REGISTRATION);
        startService(registerPlayer);

    }





}
