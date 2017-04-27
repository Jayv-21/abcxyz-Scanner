JCC=javac
FLAGS=-cp
DEST=.
SOURCE=./Scanner/Controller.java\ ./Scanner/ConversationsController.java\ ./Scanner/InterfaceInfoController.java\ ./Scanner/Main.java\ ./Scanner/NetworkManager.java\ ./Scanner/PacketManager.java\ ./Scanner/PacketViewerController.java\ ./Scanner/package-info.java
S2=./Scanner/

all:
	$(JCC) $(FLAGS) jpcap.jar:.:$(DEST) ./Scanner/Controller.java ./Scanner/ConversationsController.java ./Scanner/InterfaceInfoController.java ./Scanner/Main.java ./Scanner/NetworkInterfaceManager.java ./Scanner/PacketManager.java ./Scanner/PacketViewerController.java ./Scanner/package-info.java
	jar cfe Scanner.jar Scanner.Main $(S2)*.class $(S2)*.fxml ./theme.css ./jpcap.jar
	mv ./Scanner.jar ../env/Scanner.jar

clean:
	rm $(DEST)Scanner/*.class
