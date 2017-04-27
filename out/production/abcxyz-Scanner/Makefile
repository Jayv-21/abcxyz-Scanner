JCC=javac
FLAGS=-cp
DEST=.
SOURCE=./Scanner/*.java
S2=./Scanner/

all:
	$(JCC) $(FLAGS) jpcap.jar:.:$(DEST) $(SOURCE)
	jar cfe Scanner.jar sample.Main $(S2)*.class $(S2)*.fxml ./theme.css ./jpcap.jar
	mv ./Scanner.jar ../env/Scanner.jar

clean:
	rm $(DEST)Scanner/*.class
