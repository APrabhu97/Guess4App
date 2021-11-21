package com.anish.anish_p4;

import static java.util.stream.Collectors.toList;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int RESPONSE = 0;
    private static final int GUESS = 1;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    RecyclerView guesses1, guesses2;
    private TextView secret1, secret2;
    private HandlerThread player1, player2;
    private Handler p1Handler, p2Handler;
    private ProgressBar progressBar1, progressBar2;
    private final long delay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        secret1 = (TextView) findViewById(R.id.secretValue1);
        secret2 = (TextView) findViewById(R.id.secretValue2);
        guesses1 = (RecyclerView) findViewById(R.id.guesses1);
        guesses2 = (RecyclerView) findViewById(R.id.guesses2);
        progressBar1 = (ProgressBar) findViewById(R.id.simpleProgressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.simpleProgressBar2);
    }

    private void finishGame(String playerName){
        //displaying the winner
        player1.interrupt();
        player2.interrupt();
        if(playerName == null){
            Toast.makeText(this, "Draw. No successful guess within 20 chances", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, playerName + " has won.", Toast.LENGTH_LONG).show();
        }

    }

    public void onNewGameClicked(View v) {
        //closing threads and starting new game
        if(player1 != null)player1.interrupt();
        if(player2 != null)player2.interrupt();
        progressBar1.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.INVISIBLE);
        startNewGame();
    }

    private void startNewGame() {
        player2 = new Player2("Player2");
        player2.start();
        player1 = new Player1("Player1");
        player1.start();
    }

    public class Player1 extends HandlerThread {
        String secret;
        List<GuessItem> guesses;
        Queue<String> guessQueue = new LinkedList<String>();
        List<Integer> base = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
        List<Integer> available = base.stream().map(val -> new Integer(val)).collect(toList());

        public Player1(String name) {
            super(name);
        }

        private String generateGuess() {
            //creating a guess of unique digits
            //P1 logic: selecting the first available digit for each position
            // if guessQueue is not empty, send its top value
            if(!guessQueue.isEmpty()) return guessQueue.remove();
            String res = "";
            res += available.get(0);
            res += getUniqueDigit(available, res);
            res += getUniqueDigit(available, res);
            res += getUniqueDigit(available, res);
            return res;
        }

        public String getUniqueDigit(List<Integer> list, String res){
            for(int i : list){
                if(!res.contains(""+i)) return String.valueOf(i);
            }
            return "";
        }

        private void sendGuess() {
            //creating a message with the guess and sending it to be P2
            Message m = p2Handler.obtainMessage();
            m.what = GUESS;
            String guess = generateGuess();
            Bundle bundle = m.getData();
            bundle.putString("guess", guess);
            m.sendToTarget();
            Toast.makeText(MainActivity.this, "Player 1 has guessed: " + guess, Toast.LENGTH_SHORT).show();
        }

        private void sendResponse(String guess) {
            //sending the response for P2's guess
            Message m = p2Handler.obtainMessage();
            m.what = RESPONSE;
            Bundle bundle = m.getData();
            bundle.putString("guess", guess);
            bundle.putInt("correctDigits", GameUtil.getCorrectDigits(guess, secret).length);
            bundle.putInt("incorrectPositions", GameUtil.getIncorrectPositions(guess, secret).length);
            bundle.putChar("missingDigit", GameUtil.getMissingDigit(guess, secret));
            m.sendToTarget();
        }

        private void handleResponse(Message msg) {
            //handling the response from P2 for P1's guess
            GuessItem guessItem = GameUtil.getGuessItem(msg);
            guesses.add(guessItem);
            mHandler.post(() -> {
                GuessAdapter adapter = new GuessAdapter(guesses);
                guesses1.setAdapter(adapter);
                guesses1.post(() -> guesses1.scrollToPosition(guesses.size() - 1));
            });
            if(guessItem.getCorrectGuesses() == 4){
                //Player 1 has won
                mHandler.post(() -> finishGame("Player 1"));
                return;
            }
            if(guesses.size() == 20){
                //finishing game if no one guesses within 20 chances
                mHandler.post(() -> finishGame(null));
                return;
            }
            if(guessItem.getIncorrectDigit() == ' '){
                //all digits guessed are correct
                //generating all permutations of the digits and putting them in the guess queue
                List<String> store = new ArrayList<String>();
                generatePermutations(guessItem.getGuess(), store, "");
                store.remove(guessItem.getGuess());
                guessQueue.addAll(store);
            }
            handleMissingDigit(guessItem.getIncorrectDigit());

        }

        private void generatePermutations(String guess, List<String> store, String perm){
            if (guess.length() == 0)store.add(perm);

            for(int i = 0 ;i < guess.length(); i++) {
                char ch = guess.charAt(i);
                String l = guess.substring(0, i);
                String r = guess.substring(i + 1);
                generatePermutations(l + r, store, perm + ch);
            }
        }

        private void handleMissingDigit(char missingDigit) {
            //removing missing digit from all available options
            Integer val = Integer.valueOf(Character.getNumericValue(missingDigit));
            available.remove(val);
        }

        private void handleGuess(Message msg){
            //handling an input guess, sending response, and then sending a new guess after 2 sec
            try{
                mHandler.post(() -> progressBar2.setVisibility(View.VISIBLE));
                Thread.sleep(delay);
                mHandler.post(() -> progressBar2.setVisibility(View.INVISIBLE));
                sendResponse(msg.getData().getString("guess"));
                Thread.sleep(delay);
                sendGuess();
            }
            catch(Exception e){
                Log.e("Player1", e.toString());
            }
        }

        @Override
        protected void onLooperPrepared() {
            secret = GameUtil.getRandomSecret();
            guesses = new ArrayList<>();
            p1Handler = new Handler(getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case GUESS:
                            handleGuess(msg);
                            break;
                        case RESPONSE:
                            handleResponse(msg);
                            break;
                    }
                }
            };
            mHandler.post(() -> {
                secret1.setText(secret);
                GuessAdapter adapter = new GuessAdapter(guesses);
                guesses1.setHasFixedSize(true);
                guesses1.setAdapter(adapter);
                guesses1.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            });
            try{
                Thread.sleep(delay);
                sendGuess();
            }
            catch(Exception e){
                Log.e("Player1", e.toString());
            }
        }
    }

    public class Player2 extends HandlerThread {
        String secret;
        List<GuessItem> guesses;
        List<Integer> unavailable = new ArrayList<>();

        public Player2(String name) {
            super(name);
        }

        private void sendGuess() {
            //sending a new message with guess to P1
            Message m = p1Handler.obtainMessage();
            m.what = GUESS;
            Bundle bundle = m.getData();
            String guess = generateGuess();
            bundle.putString("guess", guess);
            m.sendToTarget();
            Toast.makeText(MainActivity.this, "Player 2 has guessed: " + guess, Toast.LENGTH_SHORT).show();
        }

        public String getUniqueDigit(List<Integer> unavailable, String res){
            //generating a random digit until it is unique and not part of unavailable array
            Random rand = new Random();
            int i = rand.nextInt(10);
            while(res.contains(""+i) || unavailable.contains(i)){
                i = rand.nextInt(10);
            }
            return ""+i;
        }

        private String generateGuess() {
            //P2 logic: generating a random number and maintaining an array with unavailable values
            //unavailable values show which values should not be used
            String res = "";
            res +=  getUniqueDigit(unavailable, res);
            res += getUniqueDigit(unavailable, res);
            res += getUniqueDigit(unavailable, res);
            res += getUniqueDigit(unavailable, res);
            return res;
        }

        private void sendResponse(String guess) {
            //sending response back to P2
            Message m = p1Handler.obtainMessage();
            m.what = RESPONSE;
            Bundle bundle = m.getData();
            bundle.putString("guess", guess);
            bundle.putInt("correctDigits", GameUtil.getCorrectDigits(guess, secret).length);
            bundle.putInt("incorrectPositions", GameUtil.getIncorrectPositions(guess, secret).length);
            bundle.putChar("missingDigit", GameUtil.getMissingDigit(guess, secret));
            m.sendToTarget();
        }

        private void handleGuess(Message msg) {
            //handling guess from P1, sending its response, and sending a new guess after 2 secs
            try {
                mHandler.post(() -> progressBar1.setVisibility(View.VISIBLE));
                Thread.sleep(delay);
                mHandler.post(() -> progressBar1.setVisibility(View.INVISIBLE));
                sendResponse(msg.getData().getString("guess"));
                Thread.sleep(delay);
                sendGuess();
            } catch (Exception e) {
                Log.e("Player2", e.toString());
            }
        }

        @Override
        protected void onLooperPrepared() {
            guesses = new ArrayList<>();
            secret = GameUtil.getRandomSecret();
            p2Handler = new Handler(getLooper()) {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case GUESS:
                            handleGuess(msg);
                            break;
                        case RESPONSE:
                            handleResponse(msg);
                            break;
                    }
                }
            };
            mHandler.post(() -> {
                secret2.setText(secret);
                GuessAdapter adapter = new GuessAdapter(guesses);
                guesses2.setHasFixedSize(true);
                guesses2.setAdapter(adapter);
                guesses2.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            });

        }

        private void handleResponse(Message msg) {
            //handling response from P1 for P2's guess
            GuessItem guessItem = GameUtil.getGuessItem(msg);
            guesses.add(guessItem);
            mHandler.post(() -> {
                GuessAdapter adapter = new GuessAdapter(guesses);
                guesses2.setAdapter(adapter);
                guesses2.post(() -> guesses2.scrollToPosition(guesses.size() - 1));
            });
            if(guessItem.getCorrectGuesses() == 4){
                mHandler.post(() -> finishGame("Player 2"));
                return;
            }
            if(guesses.size() == 20){
                //finishing game if no one guesses within 20 chances
                mHandler.post(() -> finishGame(null));
                return;
            }
            handleMissingDigit(guessItem.getIncorrectDigit());
        }

        private void handleMissingDigit(char missingDigit) {
            //adding missing digit to the unavailable array
            Integer val = Integer.valueOf(Character.getNumericValue(missingDigit));
            unavailable.add(val);
        }
    }
}