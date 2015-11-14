package edu.osu.cse6341.lispinterpreter;

//Author: Nandkumar Khobare
//khobare.1@osu.edu
//The Ohio State University, Columbus
//CSE-6341 Foundations of Programming Languages - Interpreter Part II

import java.util.*;

public class TokenHandler {
    private static TokenHandler instance = null;
    private static StringTokenizer tokenizer;
    private String token;

    private static String inputLine;

    public TokenHandler() {
        tokenizer = null;
        token = null;
        inputLine = null;
    }

    public static TokenHandler getInstance() {
        if (instance == null) {
            instance = new TokenHandler();
        }
        return instance;
    }

    public String getToken() {
        if (token != null) {
            return token;
        } else {
            if (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                while (Character.isWhitespace(token.charAt(0))) {
                    token = tokenizer.nextToken();
                }
            } else {
                // No more tokens
                token = null;
            }
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void Tokenize(String line) {
        inputLine = line;
        tokenizer = new StringTokenizer(inputLine, "() . \t \n", true);
        token = null;
    }
}