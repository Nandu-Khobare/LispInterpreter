package edu.osu.cse6341.lispinterpreter;

//Author: Nandkumar Khobare
//khobare.1@osu.edu
//The Ohio State University, Columbus
//CSE-6341 Foundations of Programming Languages - Interpreter Part II

public final class Constants {
    public static int UNASSIGNED = 0;
    public static int NUMERIC = 1;
    public static int SYMBOLIC = 2;
    public static int NONATOM = 3;

    public static final String TOKEN_DOT = ".";
    public static final String TOKEN_TRUE = "T";
    public static final String TOKEN_NIL = "NIL";
    public static final String TOKEN_LEFTPAREN = "(";
    public static final String TOKEN_RIGHTPAREN = ")";

    public static final String TOKEN_FUN_CAR = "CAR";
    public static final String TOKEN_FUN_CDR = "CDR";
    public static final String TOKEN_FUN_CONS = "CONS";
    public static final String TOKEN_FUN_EQ = "EQ";
    public static final String TOKEN_FUN_ATOM = "ATOM";
    public static final String TOKEN_FUN_NULL = "NULL";
    public static final String TOKEN_FUN_INT = "INT";
    public static final String TOKEN_FUN_COND = "COND";
    public static final String TOKEN_FUN_QUOTE = "QUOTE";
    public static final String TOKEN_FUN_DEFUN = "DEFUN";
    public static final String TOKEN_FUN_PLUS = "PLUS";
    public static final String TOKEN_FUN_MINUS = "MINUS";
    public static final String TOKEN_FUN_TIMES = "TIMES";
    public static final String TOKEN_FUN_QUOTIENT = "QUOTIENT";
    public static final String TOKEN_FUN_REMAINDER = "REMAINDER";
    public static final String TOKEN_FUN_LESS = "LESS";
    public static final String TOKEN_FUN_GREATER = "GREATER";
}