package com.anish.anish_p4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Boolean threadLock = false;

    private TextView secret1, secret2;
    private Thread player1, player2;
    RecyclerView guesses1, guesses2;
    private int turn = 1;
    //TODO: look into HandleMessage sample app and maybe use HandleThread class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        secret1 = (TextView) findViewById(R.id.secretValue1);
        secret2 = (TextView) findViewById(R.id.secretValue2);
        guesses1 = (RecyclerView) findViewById(R.id.guesses1);
        guesses2 = (RecyclerView) findViewById(R.id.guesses2);

        startNewGame();
    }

    public void onNewGameClicked(View v){
        player1.interrupt();
        player2.interrupt();
        startNewGame();
    }

    private void startNewGame(){
        player1 = new Thread(new Player1()) ;
        player1.start();
        player2 = new Thread(new Player2()) ;
        player2.start();
    }

    public class Player1 implements Runnable{

        @Override
        public void run() {
            final String s = getRandomSecret();
            mHandler.post(() -> {
                secret1.setText(s);
                GuessAdapter adapter = new GuessAdapter(new ArrayList<GuessItem>());
                guesses1.setHasFixedSize(true);
                guesses1.setAdapter(adapter);
                guesses1.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }) ;
        }
    }

    public class Player2 implements Runnable{
        @Override
        public void run() {
            final String s = getRandomSecret();
            mHandler.post(() -> {
                secret2.setText(s);
                GuessAdapter adapter = new GuessAdapter(new ArrayList<GuessItem>());
                guesses2.setHasFixedSize(true);
                guesses2.setAdapter(adapter);
                guesses2.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }) ;
        }
    }

    private String getRandomSecret(){
        List<Integer> nums = new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8,9));
        String s = "";
        Random rand = new Random();
        for(int i = 0; i < 4; i++){
            int digit = nums.get(rand.nextInt(nums.size()));
            s += digit;
            nums.remove(new Integer(digit));
        }
        return s;
    }
}