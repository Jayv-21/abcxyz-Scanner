JCC=javac
FLAGS=-d
DEST=.
SOURCE=./Scanner/*.java
S2=./Scanner/

all:
	$(JCC) $(FLAGS) $(DEST) $(SOURCE)
	jar cfe Scanner.jar sample.Main $(S2)*.class $(S2)*.fxml ./theme.css
	mv ./Scanner.jar ../env/Scanner.jar

clean:
	rm $(DEST)Scanner/*.class
