package edu.osu.cse6341.lispinterpreter;

//Author: Nandkumar Khobare
//khobare.1@osu.edu
//The Ohio State University, Columbus
//CSE-6341 Foundations of Programming Languages - Interpreter Part II

import java.util.*;
import java.io.*;

public class LispInterpreter {
    static String strExpression = "";
    static boolean withinExp = false;
    static int cntLParen = 0;
    static int cntRParen = 0;
    static int cntTokens = 0;

    public static void main(String[] args) {
        try {
            String inputLine = "";
            String token = "";
            StringTokenizer tokenizer;

            System.out
                    .println("* * * * Please enter your input S-Exps followed by the $ sign on single line.");
            System.out
                    .println("* * * * To exit type $$ on single line and press enter.");
            System.out
                    .println("CSE6341 LISP Interpreter is now running . . . ");

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    System.in));
            while ((inputLine = input.readLine()) != null) {
                inputLine = inputLine.toUpperCase();
                // Indicates last s-expression and exits application
                // if (inputLine.equals("$$")) {
                // break;
                // } else {
                tokenizer = new StringTokenizer(inputLine, "() .", true);
                while (tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken();
                    if (token.charAt(0) == '(') {
                        cntLParen++;
                    } else if (token.charAt(0) == ')') {
                        cntRParen++;
                    } else if (token.charAt(0) == ' '
                            || token.charAt(0) == '\t') {
                        continue;
                    }

                    if (!inputLine.equals("$") && !inputLine.equals("$$")) {
                        strExpression += " ";
                        strExpression += token;
                        cntTokens++;
                    }

                    if (cntLParen > 0 && cntRParen == 0) {
                        withinExp = true;
                    }

                    if ((inputLine.equals("$") || inputLine.equals("$$"))
                            && cntLParen == 0 && cntRParen == 0
                            && cntTokens > 1) {
                        System.out
                                .println("Error : Invalid SExpression. Try enclosing it in parentheses or as a single atomic SExp.");
                        reset();
                    } else if (inputLine.equals("$") || inputLine.equals("$$")) {
                        if (cntLParen != cntRParen) {
                            System.out
                                    .println("Error : No. of left and right parentheses do not match.");
                            reset();
                        } else {
                            try {
                                performInterpretation();
                            } catch (Exception ex) {
                                System.out
                                        .println("Error : " + ex.getMessage());
                                reset();
                            }
                        }
                    }
                }
                // Indicates last s-expression and exits application
                if (inputLine.equals("$$"))
                    break;
                // }
            }

        } catch (Exception ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    private static void performInterpretation() {
        // Parser parser = new Parser(strExpression );
        // parser.startParsing();
        // if (parser.root != null
        // && parser.root.getType() != Constants.UNASSIGNED &&
        // !parser.isParseFailure()) {
        // // Print the LISP output start indicator followed by output
        // System.out.print(">");
        // parser.printSExpressionTree(parser.root);
        // System.out.println();
        // }
        // reset();

        Parser parser = new Parser(strExpression);
        parser.startParsing();
        if (parser.root != null
                && parser.root.getType() != Constants.UNASSIGNED
                && !parser.isParseFailure()) {
            SExp evaluation = parser.evaluateExpression(parser.root);
            if (!parser.isEvalFailure()) {
                if (evaluation.isAtom == true) {
                    if (evaluation.getType() == Constants.SYMBOLIC) {
                        System.out.print(">");
                        System.out.println(evaluation.getName());
                        System.out.println("");
                    } else if (evaluation.getType() == Constants.NUMERIC) {
                        System.out.print(">");
                        System.out.println(Integer.toString(evaluation
                                .getValue()));
                        System.out.println("");
                    }
                } else if (!evaluation.isAtom) {
                    System.out.print(">");
                    parser.printSExpressionTree(evaluation);
                    System.out.println();
                    // System.out.print(">");
                    // System.out.print(getPrintString(evaluation));
                    // System.out.println();
                }
            }
        }
        reset();
    }

	/*
     * private static String getPrintString(SExp eval) {
	 *
	 * String leftStr = "", rightStr = ""; SExp left = eval.getLeft(); SExp
	 * right = eval.getRight();
	 *
	 * // Do not print NIL if (eval.getType() == Constants.SYMBOLIC &&
	 * eval.getName().equalsIgnoreCase("NIL")) { return ""; } if (left.isAtom ==
	 * true) { if (left.getType() == Constants.SYMBOLIC) { leftStr =
	 * left.getName(); } else if (left.getType() == Constants.NUMERIC) { leftStr
	 * = Integer.toString(left.getValue()); } } else { leftStr =
	 * getPrintString(left); } if (right.isAtom == true) { if (right.getType()
	 * == Constants.SYMBOLIC) { rightStr = right.getName(); } else if
	 * (right.getType() == Constants.NUMERIC) { rightStr =
	 * Integer.toString(right.getValue()); } } else { rightStr =
	 * getPrintString(right); }
	 *
	 * return "(" + leftStr + "." + rightStr + ")"; }
	 */

    private static void reset() {
        withinExp = false;
        strExpression = "";
        cntLParen = 0;
        cntRParen = 0;
        cntTokens = 0;
    }
}