JCC=javac
FLAGS=-d
DEST=.
SOURCE=./sample/*.java
S2=./sample/

all:
	$(JCC) $(FLAGS) $(DEST) $(SOURCE)
	jar cfe Scanner.jar sample.Main $(S2)*.class $(S2)*.fxml ./theme.css
	mv ./Scanner.jar ../env/Scanner.jar

clean:
	rm $(DEST)sample/*.class
