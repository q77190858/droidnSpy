#!/bin/sh
dirname $0
cd `dirname $0`
echo "chmod lib..."
chmod -R 777 ./*
echo "chmod success!"
echo "init mono environment success!"
exit 0
