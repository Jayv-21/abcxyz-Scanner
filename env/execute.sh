#!/usr/bin/env bash

rm /Users/gregorypontejos/.ssh/known_hosts
ssh -Y -p 2222 -i .vagrant/machines/default/virtualbox/private_key vagrant@localhost "cd /vagrant_data/; ./run.sh" 
