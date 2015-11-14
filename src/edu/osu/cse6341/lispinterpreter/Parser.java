package edu.osu.cse6341.lispinterpreter;

//Author: Nandkumar Khobare
//khobare.1@osu.edu
//The Ohio State University, Columbus
//CSE-6341 Foundations of Programming Languages - Interpreter Part II

import java.util.*;

public class Parser {
    private static TokenHandler tokenHandler = TokenHandler.getInstance();
    public SExp root;
    private static String inputLine;
    private boolean isParseFailure;
    private static boolean isEvalFailure;
    private static SExp trueSExp = new SExp("T");
    private static SExp nilSExp = new SExp("NIL");
    private static SExp dList = new SExp("NIL");
    public static Hashtable<String, Integer> userFuncParamCount = new Hashtable<String, Integer>();

    public Parser(String line) {
        tokenHandler.Tokenize(line);
        root = null;
        inputLine = line;
        setParseFailure(false);
        setEvalFailure(false);
    }

    public void startParsing() {
        setParseFailure(false);
        setEvalFailure(false);
        // Check for illegal characters in input string
        if (isIllegalCharsPresent(inputLine)) {
            System.out
                    .println("Error : Illegal character(s) present in the input.");
            setParseFailure(true);
            return;
        }

        root = new SExp();
        parseE(root);

        String currentToken = tokenHandler.getToken();
        if (currentToken != null) {
            System.out
                    .println("Error : More than one SExpression detected or extra tokens after complete expression.");
            setParseFailure(true);
            root = null;
        }
    }

    // Use PreOrder traversal to print the tree
    public void printSExpressionTree(SExp node) {

        if (node == null)
            return;
        if (node.getType() == Constants.SYMBOLIC) {
            System.out.print(node.getName());
            return;
        }
        if (node.getType() == Constants.NUMERIC) {
            System.out.print(node.getValue());
            return;
        }
        if (node.getType() == Constants.UNASSIGNED) {
            return;
        }

        System.out.print("(");
        printSExpressionTree(node.getLeft());
        System.out.print(".");
        printSExpressionTree(node.getRight());
        System.out.print(")");
        return;
    }

    private void parseE(SExp node) {
        String currentToken = tokenHandler.getToken();

        if (currentToken.equalsIgnoreCase("(")) {
            tokenHandler.setToken(null);
            // <E>::=(<X>
            parseX(node);
        } else {
            // <E>::= atom
            if (isSymbolicAtom(currentToken)) {
                // Symbolic atom
                node.setName(currentToken);
                node.isAtom = true;
                node.isNil = false;
                node.setType(Constants.SYMBOLIC);
            } else if (isNumericAtom(currentToken)) {
                // Numeric atom
                if (currentToken.charAt(0) == '+') {
                    currentToken = currentToken.substring(1);
                }
                try {
                    node.setValue(Integer.parseInt(currentToken));
                } catch (Exception ex) {
                    System.out
                            .println("Error : Parsing to integer value failed. Please check for : "
                                    + currentToken);
                    setParseFailure(true);
                }
                node.isAtom = true;
                node.isNil = false;
                node.setType(Constants.NUMERIC);
            } else {
                // Something not correct,tree should collapse
                node.setType(Constants.UNASSIGNED);
                System.out.println("Error : Illegal\\Empty atom : "
                        + currentToken);
                setParseFailure(true);
            }

            tokenHandler.setToken(null);
        }
    }

    private void parseX(SExp node) {
        String currentToken = tokenHandler.getToken();

        if (currentToken.equalsIgnoreCase(Constants.TOKEN_RIGHTPAREN)) {
            // <X>::=)
            tokenHandler.setToken(null);
            node.setName("NIL");
            node.isNil = true;
            node.isAtom = true;
            node.setType(Constants.SYMBOLIC);
        } else {
            tokenHandler.setToken(currentToken);

            // <X>::=<E><Y>
            SExp leftExp = new SExp();
            node.setLeft(leftExp);
            parseE(leftExp);

            SExp rightExp = new SExp();
            node.setRight(rightExp);
            parseY(rightExp);

            node.setType(Constants.NONATOM);
        }
    }

    private void parseY(SExp node) {
        String currentToken = tokenHandler.getToken();

        if (currentToken.equals(Constants.TOKEN_DOT)) {
            // <Y>::=.<E>)
            tokenHandler.setToken(null);
            parseE(node);
            String secondToken = tokenHandler.getToken();

            if (secondToken.equalsIgnoreCase(Constants.TOKEN_RIGHTPAREN)) {
                tokenHandler.setToken(null);
            } else {
                // System.out.println("Error");
            }
        } else {
            // <Y>::=<R>)
            tokenHandler.setToken(currentToken);
            parseR(node);
            String secondToken = tokenHandler.getToken();
            if (secondToken.equalsIgnoreCase(Constants.TOKEN_RIGHTPAREN)) {
                tokenHandler.setToken(null);
            } else {
                tokenHandler.setToken(secondToken);
            }
        }
    }

    private void parseR(SExp node) {
        String currentToken = tokenHandler.getToken();

        if (currentToken != null
                && !currentToken.equalsIgnoreCase(Constants.TOKEN_RIGHTPAREN)) {
            // <R>::=<E><R>
            SExp leftExp = new SExp();
            node.setLeft(leftExp);
            parseE(leftExp);

            SExp rightExp = new SExp();
            node.setRight(rightExp);
            parseR(rightExp);

            node.setType(Constants.NONATOM);
        } else {
            // <R>::=null
            node.setName("NIL");
            node.isNil = true;
            node.isAtom = true;
            node.setType(Constants.SYMBOLIC);
        }
    }

    // Check for illegal characters, returns true if present
    private static boolean isIllegalCharsPresent(String line) {
        int size = line.length();
        if (size < 1)
            return true;

        int i = 0;
        boolean isIllegal = false;
        char c;

        while (i < size) {
            c = line.charAt(i);
            if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c == '(')
                    || (c == ')') || (c == '.') || (c == '-') || (c == '+')
                    || (c == ' ') || (c == '\n') || (c == '\t') || (c == '$')) {
                if (c == '+' || c == '-') {
                    if (line.charAt(i + 1) == ' ') {
                        System.out
                                .println("Error : Integer should immediately follow after +\\- sign. Please no spaces there.");
                        isIllegal = true;
                        // Not needed
                        // } else if (line.charAt(i - 1) != ' ') {
                        // System.out
                        // .println("Error : Something about +\\- sign.");
                        // isIllegal = true;
                    }
                }

            } else {
                return true;
            }
            i++;
        }
        return isIllegal;
    }

    // Checks whether the token is a valid symbolic atom
    private static boolean isSymbolicAtom(String token) {
        int i = 0;

        if (token.charAt(i) >= 65 && token.charAt(i) <= 90) {
            // Should be a literal atom, check for the rest
            while (i < token.length()) {
                if ((token.charAt(i) >= 48 && token.charAt(i) <= 57)
                        || (token.charAt(i) >= 65 && token.charAt(i) <= 90)) {
                } else {
                    return false;
                }
                i++;
            }
        } else {
            return false;
        }
        return true;
    }

    // Checks whether the token is a valid numeric atom
    private static boolean isNumericAtom(String token) {
        int i = 0;

        if ((token.charAt(i) >= 48 && token.charAt(i) <= 57)
                || token.charAt(0) == '+' || token.charAt(0) == '-') {
            if (token.charAt(0) == '-' || token.charAt(0) == '+') {
                i++;
            }

            while (i < token.length()) {
                if (token.charAt(i) >= 48 && token.charAt(i) <= 57) {
                } else {
                    return false;
                }
                i++;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * @return the isParseFailure
     */
    public boolean isParseFailure() {
        return isParseFailure;
    }

    /**
     * @param isParseFailure the isParseFailure to set
     */
    public void setParseFailure(boolean isParseFailure) {
        this.isParseFailure = isParseFailure;
    }

	/* Part II code starts here */

    /**
     * @return the isEvalFailure
     */
    public boolean isEvalFailure() {
        return isEvalFailure;
    }

    /**
     * @param isEvalFailure the isEvalFailure to set
     */
    public static void setEvalFailure(boolean isEvalFailure) {
        Parser.isEvalFailure = isEvalFailure;
    }

    public SExp evaluateExpression(SExp root) {
        return eval(root, new SExp("NIL"), dList);
    }

    private static SExp eval(SExp root, SExp aList, SExp dList) {
        if (root.isAtom) {
            if (root.getType() == Constants.NUMERIC) {
                return root;
            } else if (root.getType() == Constants.SYMBOLIC) {
                if (root.getName().equalsIgnoreCase("T")) {
                    return new SExp("T");
                } else if (root.getName().equalsIgnoreCase("NIL")) {
                    return new SExp("NIL");
                } else {
                    if (isInAList(root, aList)) {
                        return getValueFromAList(root, aList);
                    } else {
                        // Should result in error
                        System.out.println("Error : Variable " + root.getName()
                                + " cannot be evaluated.");
                        setEvalFailure(true);
                        return nilSExp;
                    }
                }
            } else {
                return nilSExp;
            }
        } else {
            // Left child is function name
            String functionName = root.getLeft().getName();

            // Check for type of function
            if (functionName.equalsIgnoreCase("")) {
                System.out.println("Error : function is unknown.");
                setEvalFailure(true);
                return nilSExp;
            } else if (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_QUOTE)) {
                if (getNumOfParams(root.getRight()) == 1) {
                    return car(cdr(root));
                } else {
                    System.out
                            .println("Error : QUOTE() function must've a single input SExpression.");
                    setEvalFailure(true);
                    return nilSExp;
                }
            } else if (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_COND)) {
                return evcon(cdr(root), aList, dList);
            } else if (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_DEFUN)) {
                if (isValidFunction(root)) {
                    addToDList(cdr(root));
                    // We'll print the user defined function name
                    return new SExp(car(cdr(root)).getName());
                } else {
                    System.out
                            .println("Error : User defined function has some parameters wrong.");
                    setEvalFailure(true);
                    return nilSExp;
                }
            } else {
                return apply(root.getLeft(),
                        evlist(root.getRight(), aList, dList), aList, dList);
            }
        }
    }

    private static SExp evlist(SExp aparam, SExp aList, SExp dlist) {
        if (aparam.getName() != null
                && aparam.getName().equalsIgnoreCase("NIL")) {
            return nilSExp;
        } else {
            return cons(eval(car(aparam), aList, dlist),
                    evlist(cdr(aparam), aList, dlist));
        }
    }

    private static SExp apply(SExp function, SExp aparam, SExp aList, SExp dList) {
        int numOfParams = getNumOfParams(aparam);

        if (function.isAtom) {
            if (function.getName().equalsIgnoreCase(Constants.TOKEN_FUN_CAR)) {
                if (numOfParams == 1) {
                    return car(car(aparam));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_CDR)) {
                if (numOfParams == 1) {
                    return cdr(car(aparam));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_CONS)) {
                if (numOfParams == 2) {
                    return cons(car(aparam), car(cdr(aparam)));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_ATOM)) {
                if (numOfParams == 1) {
                    return atom(car(aparam));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_EQ)) {
                if (numOfParams == 2) {
                    return eq(car(aparam), car(cdr(aparam)));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_INT)) {
                if (numOfParams == 1) {
                    return isInt(car(aparam));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_GREATER)) {
                if (numOfParams == 2) {
                    return greater(car(aparam), car(cdr(aparam)));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_LESS)) {
                if (numOfParams == 2) {
                    return less(car(aparam), car(cdr(aparam)));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_NULL)) {
                if (numOfParams == 1) {
                    return isNull(car(aparam));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_PLUS)) {
                if (numOfParams == 2) {
                    return plus(car(aparam), car(cdr(aparam)));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_MINUS)) {
                if (numOfParams == 2) {
                    return minus(car(aparam), car(cdr(aparam)));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_TIMES)) {
                if (numOfParams == 2) {
                    return times(car(aparam), car(cdr(aparam)));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_QUOTIENT)) {
                if (numOfParams == 2) {
                    return quotient(car(aparam), car(cdr(aparam)));
                }
            } else if (function.getName().equalsIgnoreCase(
                    Constants.TOKEN_FUN_REMAINDER)) {
                if (numOfParams == 2) {
                    return remainder(car(aparam), car(cdr(aparam)));
                }
            } else {
                // User defined function code
                if (isFunctionDefined(function, dList)) {
                    // Check for number of parameters correctness
                    if (userFuncParamCount.get(function.getName()) == numOfParams) {
                        return eval(
                                cdr(getValueFromAList(function, dList)),
                                combineLists(
                                        car(getValueFromAList(function, dList)),
                                        aparam, aList), dList);
                    } else {
                        // Parameters do not match
                        System.out.println("Error : Function "
                                + function.getName()
                                + "() has incorrect number of parameters.");
                    }
                } else {
                    // No function found in dList
                    System.out
                            .println("Error : Function "
                                    + function.getName()
                                    + "() is neither built-in function or user-defined function.");
                }
            }
            setEvalFailure(true);
            return nilSExp;
        } else {
            System.out
                    .println("Error : Function cannot be non-atomic SExpression.");
            setEvalFailure(true);
            return nilSExp;
        }
    }

    private static SExp evcon(SExp exp, SExp aList, SExp dlist) {
        if (exp.getName() != null && exp.getName().equalsIgnoreCase("NIL")) {
            System.out
                    .println("Error: At least one condition should evaluate to true.");
            setEvalFailure(true);
            return nilSExp;
        } else {
            if (eval(car(car(exp)), aList, dlist).getName().equalsIgnoreCase(
                    "T")) {
                return eval(car(cdr(car(exp))), aList, dlist);
            } else {
                return evcon(cdr(exp), aList, dlist);
            }
        }
    }

    private static boolean isValidFunctionName(String functionName) {
        if (isNumericAtom(functionName)) {
            return false;
        }
        if ((functionName.equalsIgnoreCase(Constants.TOKEN_FUN_CAR))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_CDR))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_CONS))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_EQ))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_ATOM))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_NULL))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_INT))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_COND))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_QUOTE))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_DEFUN))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_PLUS))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_MINUS))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_TIMES))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_QUOTIENT))
                || (functionName
                .equalsIgnoreCase(Constants.TOKEN_FUN_REMAINDER))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_LESS))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_FUN_GREATER))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_TRUE))
                || (functionName.equalsIgnoreCase(Constants.TOKEN_NIL))) {
            return false;
        }
        return true;
    }

    private static boolean isValidFunction(SExp exp) {
        String fName = car(cdr(exp)).getName();
        SExp fExp = car(cdr(exp));

        if (fExp.getType() == Constants.SYMBOLIC && isValidFunctionName(fName)) {

            SExp temp = car(cdr(cdr(exp)));
            ArrayList<String> lsParams = new ArrayList<String>();

            while (temp != null) {
                if (temp.getLeft() != null) {
                    if (temp.getLeft().getType() == Constants.SYMBOLIC) {
                        String parameter = temp.getLeft().getName();
                        if (parameter.equalsIgnoreCase(Constants.TOKEN_TRUE)
                                || parameter
                                .equalsIgnoreCase(Constants.TOKEN_NIL)) {
                            System.out.println("Error : User function " + fName
                                    + "() has an invalid parameter.");
                        } else {
                            lsParams.add(parameter);
                        }
                    } else {
                        System.out.println("Error : User function " + fName
                                + "() has non-symbolic parameter name.");
                    }
                }
                temp = temp.getRight();
            }

            // Add the count information for future use
            userFuncParamCount.put(fName, lsParams.size());

            if (lsParams.size() != new HashSet<String>(lsParams).size()) {
                // Duplicate params
                System.out.println("Error : Function " + fName
                        + "() has duplicate parameters.");
                userFuncParamCount.remove(fName);
                setEvalFailure(true);
            }
        } else {
            System.out.println("Error : Function " + fName
                    + "() name is illegal.");
            setEvalFailure(true);
            return false;
        }
        return true;
    }

    private static int getNumOfParams(SExp exp) {
        SExp temp = exp;
        int num = 0;
        while (temp != null) {
            if (temp.getLeft() != null) {
                num++;
            }
            temp = temp.getRight();
        }
        return num;
    }

    private static void addToDList(SExp exp) {
        SExp temp = cons(car(exp), cons(car(cdr(exp)), car(cdr(cdr(exp)))));
        dList = cons(temp, dList);
    }

    private static boolean isFunctionDefined(SExp exp, SExp list) {
        if ((list.getName() != null && list.getName().equalsIgnoreCase("NIL"))
                || (list.getName() != null && exp.getName().equalsIgnoreCase(
                "NIL"))) {
            return false;
        }
        if (eq(exp, car(car(list))).getName().equalsIgnoreCase("T")) {
            return true;
        } else {
            return isFunctionDefined(exp, cdr(list));
        }
    }

    // Check if exp is in aList, return true if found, else false
    private static boolean isInAList(SExp exp, SExp aList) {
        if (aList == null || exp == null || aList.getLeft() == null) {
            return false;
        } else {
            if (car(aList) != null && car(car(aList)) != null) {
                if (exp.getName() != null
                        && exp.getName().equalsIgnoreCase(
                        (car(car(aList))).getName())) {
                    return true;
                } else {
                    return isInAList(exp, cdr(aList));
                }
            } else {
                return false;
            }
        }
    }

    private static SExp getValueFromAList(SExp exp, SExp aList) {
        if (aList == null) {
            return new SExp("NIL");
        } else {
            if (eq(exp, car(car(aList))).getName().equalsIgnoreCase("T")) {
                return (cdr(car(aList)));
            } else {
                return getValueFromAList(exp, cdr(aList));
            }
        }
    }

    private static SExp combineLists(SExp xlist, SExp ylist, SExp aList) {
        if (xlist.getName() != null && xlist.getName().equalsIgnoreCase("NIL")) {
            return aList;
        } else {
            return (cons(cons(car(xlist), car(ylist)),
                    combineLists(cdr(xlist), cdr(ylist), aList)));
        }
    }

    private static SExp car(SExp exp) {
        if ((exp.getName() != null && exp.getName().equalsIgnoreCase("NIL"))
                || (exp.getName() != null && exp.getName()
                .equalsIgnoreCase("T"))) {
            return nilSExp;
        } else {
            if (exp.isAtom == true) {
                System.out.println("Error : CAR cannot be applied to an atom.");
                setEvalFailure(true);
            }
            return exp.getLeft();
        }
    }

    private static SExp cdr(SExp exp) {
        if ((exp.getName() != null && exp.getName().equalsIgnoreCase("NIL"))
                || (exp.getName() != null && exp.getName()
                .equalsIgnoreCase("T"))) {
            return nilSExp;
        } else {
            if (exp.isAtom == true) {
                System.out.println("Error : CDR cannot be applied to an atom.");
                setEvalFailure(true);
            }
            return exp.getRight();
        }
    }

    private static SExp atom(SExp exp) {
        if (exp.isAtom == true && exp.getRight() == null) {
            return trueSExp;
        }
        return nilSExp;
    }

    private static SExp eq(SExp exp1, SExp exp2) {
        if (exp1.isAtom == true && exp2.isAtom == true) {
            if (exp1.getType() == Constants.SYMBOLIC
                    && exp2.getType() == Constants.SYMBOLIC) {
                if (exp1.getName().equalsIgnoreCase(exp2.getName())) {
                    return trueSExp;
                } else {
                    return nilSExp;
                }
            } else if (exp1.getType() == Constants.NUMERIC
                    && exp2.getType() == Constants.NUMERIC) {
                if (exp1.getValue() == exp2.getValue()) {
                    return trueSExp;
                } else {
                    return nilSExp;
                }
            }
            System.out.println("Error : EQ() must be applied to atoms");
            setEvalFailure(true);
            return nilSExp;
        } else {
            System.out.println("Error : EQ() must be applied to atoms.");
            setEvalFailure(true);
            return nilSExp;
        }
    }

    private static SExp cons(SExp exp1, SExp exp2) {
        return new SExp(exp1, exp2);
    }

    private static SExp isInt(SExp exp) {
        if (exp.isAtom == true) {
            if (exp.getType() == Constants.NUMERIC)
                return trueSExp;
            else
                return nilSExp;
        } else {
            System.out.println("Error : INT() cannot be applied to non-atom.");
            setEvalFailure(true);
            return nilSExp;
        }
    }

    private static SExp greater(SExp exp1, SExp exp2) {
        if (exp1.getType() == Constants.NUMERIC
                && exp2.getType() == Constants.NUMERIC) {
            if (exp1.getValue() > exp2.getValue()) {
                return trueSExp;
            } else {
                return nilSExp;
            }
        } else {
            System.out
                    .println("Error : GREATER() function must have numeric input.");
            setEvalFailure(true);
            return nilSExp;
        }
    }

    private static SExp less(SExp exp1, SExp exp2) {
        if (exp1.getType() == Constants.NUMERIC
                && exp1.getType() == Constants.NUMERIC) {
            if (exp1.getValue() < exp2.getValue()) {
                return trueSExp;
            } else {
                return nilSExp;
            }
        } else {
            System.out
                    .println("Error: LESS() function must have numeric input.");
            setEvalFailure(true);
            return nilSExp;
        }
    }

    private static SExp isNull(SExp exp) {
        if (exp != null) {
            if (exp.isAtom) {
                if (exp.getType() == Constants.SYMBOLIC
                        && exp.getName().equalsIgnoreCase("NIL")) {
                    return trueSExp;
                } else {
                    return nilSExp;
                }
            } else {
                return nilSExp;
            }
        } else {
            return nilSExp;
        }
    }

    private static SExp plus(SExp exp1, SExp exp2) {
        if (exp1.getType() == Constants.NUMERIC
                && exp2.getType() == Constants.NUMERIC) {
            return new SExp(exp1.getValue() + exp2.getValue());
        } else {
            System.out
                    .println("Error : PLUS() function must have numeric input.");
            setEvalFailure(true);
        }
        return nilSExp;
    }

    private static SExp minus(SExp exp1, SExp exp2) {
        if (exp1.getType() == Constants.NUMERIC
                && exp2.getType() == Constants.NUMERIC) {
            return new SExp(exp1.getValue() - exp2.getValue());
        } else {
            System.out
                    .println("Error : MINUS() function must have numeric input.");
            setEvalFailure(true);
        }
        return nilSExp;
    }

    private static SExp times(SExp exp1, SExp exp2) {
        if (exp1.getType() == Constants.NUMERIC
                && exp2.getType() == Constants.NUMERIC) {
            return new SExp(exp1.getValue() * exp2.getValue());
        } else {
            System.out
                    .println("Error : TIMES() function must have numeric input.");
            setEvalFailure(true);
        }
        return nilSExp;
    }

    private static SExp quotient(SExp exp1, SExp exp2) {
        if (exp1.getType() == Constants.NUMERIC
                && exp2.getType() == Constants.NUMERIC) {
            return new SExp(exp1.getValue() / exp2.getValue());
        } else {
            System.out
                    .println("Error : QUOTIENT() function must have numeric input.");
            setEvalFailure(true);
        }
        return nilSExp;
    }

    private static SExp remainder(SExp exp1, SExp exp2) {
        if (exp1.getType() == Constants.NUMERIC
                && exp2.getType() == Constants.NUMERIC) {
            return new SExp(exp1.getValue() % exp2.getValue());
        } else {
            System.out
                    .println("Error : REMAINDER() function must have numeric input.");
            setEvalFailure(true);
        }
        return nilSExp;
    }

}