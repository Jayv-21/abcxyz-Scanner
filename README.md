This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
Group Members
Donovan Wells
Jason Villegas
Kingsley Nyaosi
 
 
Scanner is a program that can capture TCP, UDP, ICMP, ICMPv6, ARP, and IGMP packets. 
 
Libpcap is the c library used to capture packets. Libpcap can be downloaded by via homebrew by running
    brew install libpcap-dev
    
    Homebrew can be downloaded by running the following command in terminal
    ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
    
To compile and run this program, simply run the following command in terminal from the project directory
    ./run.sh

Note: 
    Heavy network traffic may slow down or crash the program. The packets will be processed, but the processing
    may take longer. 