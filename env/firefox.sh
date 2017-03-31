#!/usr/bin/env bash

ssh -X -p 2222 -i ./.vagrant/machines/default/virtualbox/private_key ubuntu@localhost firefox
