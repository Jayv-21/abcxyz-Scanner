#!/usr/bin/env bash 

#install dependencies
sudo apt-get update
sudo apt-get install -y default-jdk 
sudo apt-get install -y libpcap-dev 
sudo apt-get install -y make 
sudo apt-get install -y g++
sudo apt-get install -y ant
sudo apt-get install -y junit
sudo apt-get install -y subversion
sudo apt-get install -y less 
sudo apt-get install -y git
sudo apt-get install -y firefox
sudo apt-get install -y openjfx
sudo apt-get install -y gtk2-engines
sudo apt-get install -y libswt-gtk-3-java
sudo apt-get install -y libxslt1.1
sudo apt-get install -y libxtst6
sudo apt-get install -y libxxf86vm1
sudo apt-get install -y xfce4

#Create user
#sudo useradd -m -p dev dev
#echo "dev\ndev\n" | sudo passwd dev

#Setup environment variables
cd ~
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
echo $JAVA_HOME >> .bashrc
source ~/.bashrc
cp /usr/lib/jvm/java-8-openjdk-amd64/include/linux/jni_md.h /usr/lib/jvm/java-8-openjdk-amd64/include/

#Install Jpcap
cd /vagrant_data/env/ 
cp  -R ./Jpcap-master/ ~/
cd ~/Jpcap-master/
cd ./src/main/c/
make
cd ~/Jpcap-master/
cp ./src/main/c/libjpcap.so /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/
cp ~/Jpcap-master/lib/jpcap.jar /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/
cp ~/Jpcap-master/lib/jpcap.jar  /usr/share/java/ 
export CLASSPATH=$CLASSPATH:/usr/share/java/
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib/
source ~/.bashrc

sudo startxfce4&
