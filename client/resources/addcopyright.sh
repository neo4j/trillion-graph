#!/bin/bash  

# From https://github.com/atapas/add-copyright
# How to run: 
# find .. -type d -name "node_modules" -prune -o -name "*.js" -print0 | xargs -0 ./addcopyright.sh
# find .. -type d -name "node_modules" -prune -o -name "*.ts" -print0 | xargs -0 ./addcopyright.sh

for x in $*; do  
head -$COPYRIGHTLEN $x | diff copyright.txt - || ( ( cat copyright.txt; echo; cat $x) > /tmp/file;  
mv /tmp/file $x )  
done 
