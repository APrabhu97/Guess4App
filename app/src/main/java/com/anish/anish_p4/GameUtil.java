package com.anish.anish_p4;

import android.os.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameUtil {

    public static String getRandomSecret(){
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

    public static GuessItem getGuessItem(Message msg){
        String guess = msg.getData().getString("guess");
        int correctDigits = msg.getData().getInt("correctDigits");
        int incorrectPositions = msg.getData().getInt("incorrectPositions");
        char missingDigit = msg.getData().getChar("missingDigit");
        return new GuessItem(guess,correctDigits,incorrectPositions,missingDigit);
    }

    public static char[] getCorrectDigits(String guess, String secret){
        char[] guessArr = guess.toCharArray();
        List<Character> res = new ArrayList<Character>();
        for(int i = 0; i < guess.length(); i++){
            if(guessArr[i] == secret.charAt(i)) res.add(guessArr[i]);
        }
        char[] r = new char[res.size()];
        int i=0;
        for(Character c : res)r[i++]=c.charValue();
        return r;
    }

    public static char[] getIncorrectPositions(String guess, String secret){
        char[] guessArr = guess.toCharArray();
        List<Character> res = new ArrayList<Character>();
        for(int i = 0; i < guess.length(); i++){
            if(secret.contains(String.valueOf(guessArr[i])) && guessArr[i] != secret.charAt(i)) res.add(guessArr[i]);
        }
        char[] r = new char[res.size()];
        int i=0;
        for(Character c : res)r[i++]=c.charValue();
        return r;
    }

    public static char getMissingDigit(String guess, String secret){
        char[] guessArr = guess.toCharArray();
        for(int i = 0; i < guess.length(); i++){
            if(!secret.contains(String.valueOf(guessArr[i]))) return guessArr[i];
        }
        return ' ';
    }

}
