import os

i = 0
while os.path.exists("example-%s.com.tmpl" % i):
    i += 1

fh = open("example-%s.com.tmpl" % i, "w")
