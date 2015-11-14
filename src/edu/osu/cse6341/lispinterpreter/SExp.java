package edu.osu.cse6341.lispinterpreter;

//Author: Nandkumar Khobare
//khobare.1@osu.edu
//The Ohio State University, Columbus
//CSE-6341 Foundations of Programming Languages - Interpreter Part II

public class SExp {
    private int type; 				/* 1: integer atom; 2: symbolic atom 3: non-atom */
    private int value; 				/* if type is 1 */
    private String name; 			/* if type is 2 */
    private SExp left, right; 		/* if type is 3 */

    public boolean isAtom;
    public boolean isList;
    public boolean isNil;

    SExp() {
        isAtom = false;
        setType(Constants.UNASSIGNED);
        setName(null);
        isNil = false;
        isList = false;
        setLeft(null);
        setRight(null);
    }

    // Form the integer atom
    SExp(int num) {
        isAtom = true;
        setType(Constants.NUMERIC);
        setValue(num);
        setName(null);
        isNil = false;
        isList = false;
        setLeft(null);
        setRight(null);
    }

    // Form the symbolic atom
    SExp(String expression) {
        isAtom = true;
        setType(Constants.SYMBOLIC);
        setName(expression);
        if (name.equalsIgnoreCase("NIL")) {
            isNil = true;
        } else {
            isNil = false;
        }
        isList = false;
        setLeft(null);
        setRight(null);
    }

    // Form the non-atom
    SExp(SExp carSexp, SExp cdrSexp) {
        isAtom = false;
        setType(Constants.NONATOM);
        setName(null);
        isNil = false;
        isList = false;
        setLeft(carSexp);
        setRight(cdrSexp);
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param names the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the left
     */
    public SExp getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(SExp left) {
        this.left = left;
    }

    /**
     * @return the right
     */
    public SExp getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(SExp right) {
        this.right = right;
    }
}