#!/bin/sh

for i in *.json; do
curl -v -XPOST http://localhost:9090/event -d @${i}
done
