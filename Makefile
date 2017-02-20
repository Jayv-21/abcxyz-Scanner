JCC=javac
FLAGS=-d
DEST=./src/
SOURCE=./src/sample/*.java

all:
	$(JCC) $(FLAGS) $(DEST) $(SOURCE)

clean:
	rm $(DEST)sample/*.class
