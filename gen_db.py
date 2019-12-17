#!/usr/bin/python3

import sys

if len(sys.argv) != 2:
    print("usage: gen_db.py <rows>")
    sys.exit(0)

for i in range(0, int(sys.argv[1])):
    print("\"fruit%d\",\"%d\"" % (i, i))
