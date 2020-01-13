#!/usr/bin/python3

import sys

if len(sys.argv) != 2:
    print("usage: gen_db.py <rows>")
    sys.exit(0)

for i in range(0, int(sys.argv[1])):
    print("\"u%d\",\"p%d\"" % (i, i))
