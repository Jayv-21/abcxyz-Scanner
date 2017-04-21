#!/bin/bash

cd ./src/
make
cd ../env/
java -jar Scanner.jar
