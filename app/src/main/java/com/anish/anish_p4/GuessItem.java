package com.anish.anish_p4;

public class GuessItem {
    private String guess;
    private char[] correctGuesses;
    private char[] incorrectPositions;
    private char incorrectDigit;

    public GuessItem(String guess, char[] correctGuesses, char[] incorrectPositions, char incorrectDigit) {
        this.guess = guess;
        this.correctGuesses = correctGuesses;
        this.incorrectPositions = incorrectPositions;
        this.incorrectDigit = incorrectDigit;
    }

    public String getGuess() {
        return guess;
    }

    public char[] getCorrectGuesses() {
        return correctGuesses;
    }

    public char[] getIncorrectPositions() {
        return incorrectPositions;
    }

    public char getIncorrectDigit() {
        return incorrectDigit;
    }
}
