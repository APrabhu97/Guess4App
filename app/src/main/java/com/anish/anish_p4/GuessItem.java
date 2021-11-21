package com.anish.anish_p4;

public class GuessItem {
    private String guess;
    private int correctGuesses;
    private int incorrectPositions;
    private char incorrectDigit;

    public GuessItem(String guess, int correctGuesses, int incorrectPositions, char incorrectDigit) {
        this.guess = guess;
        this.correctGuesses = correctGuesses;
        this.incorrectPositions = incorrectPositions;
        this.incorrectDigit = incorrectDigit;
    }

    public String getGuess() {
        return guess;
    }

    public int getCorrectGuesses() {
        return correctGuesses;
    }

    public int getIncorrectPositions() {
        return incorrectPositions;
    }

    public char getIncorrectDigit() {
        return incorrectDigit;
    }
}
