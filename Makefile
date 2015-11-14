JFLAGS = -g

JAVAC = javac

COMPILE = $(JAVAC) $(JFLAGS)

CLASS_FILES = Constants.class SExp.class TokenHandler.class Parser.class LispInterpreter.class

all: $(CLASS_FILES)

%.class : %.java
	$(COMPILE) $<

clean:
	rm *.class
	
run:
	java LispInterpreter
	
runwithinputfile:
	java LispInterpreter < Input.txt