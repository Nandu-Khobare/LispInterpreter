================================================================================================================
Read Me

Nandkumar Khobare
khobare.1@osu.edu
The Ohio State University, Columbus
CSE-6341 Foundations of Programming Languages - Interpreter Part II
================================================================================================================

Requirements:
1. LispInterpreter requires JavaSE-1.7 for execution.
2. Make sure user has read access in the execution directory.

================================================================================================================

Assumptions for Input:
1. Input file should be in executable folder with name "Input.txt" if you're using Input file.
2. Enter SExpressions one by one or across many lines. Enter only $ on a single line to denote end of SExpression.
3. Enter $$ to exit.
4. Symbolic atoms can be entered in small cases. I did not put restriction on it.
5. Atoms either numeric or symbolic do not have length restriction. But care must be taken for integer atoms.
   If you put integer value more than MAX_INTEGER, error will be shown.
6. Signs if used for numeric atoms should have no space between number and sign itself. Ex. "+ 1" is not valid.
7. Multiple spaces, tabs, lines are allowed to be used in single expression.
8. As per suggestion for previous part, when $$ is encountered after an input, 
   interpreter will now produces output and then only exits.
================================================================================================================

Instructions for running interpreter:
1. Copy all the given project files in a directory.

2. Open stdlinux terminal in the project directory and run below command.
make
-- This compiles all the .java files and generates .class files.

3. Run below command in the same directory to start the interpreter.
	a. make run
		-- This will run the interpreter in mode where you can enter SExpressions as usual one by one in terminal window.
	b. make runwithinputfile
		-- In this mode you can give all the SExpressions in separate file "Input.txt". File should be present in the same directory. I have included sample input file which I used.
		
	Note:
	Alternatively, you can use below commands directly.
	a. java LispInterpreter
	b. java LispInterpreter < Input.txt (Assuming you have s-expressions in that file)
	
4. Run below command in the same directory.
make clean
-- Cleans the project directory with all the .class files.

================================================================================================================

Output:
1. Output will be printed on screen (after the sign >).
2. Any error messages if occurred will be shown. I tried as much as possible to be able to show accurate error messages.
3. In some cases when exception might occur, error message from exception is printed. Program will still continue to process inputs.
4. In case of error, interpreter will proceed with next expression ignoring current expression till $.
5. For one single s-expression, interpreter might give more than one error message.
6. Output will always be printed in upper case letters even though input is in lower case or mix case.

================================================================================================================