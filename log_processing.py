
# To run this file, add the path(s) for the log files in the array below, then run:
# python3 log_processing.py
# The average TS and TJ will be printed out in the terminal!

paths = ["/Users/rithvijpochampally/Desktop/scaled_master.txt",
         "/Users/rithvijpochampally/Desktop/scaled_slave.txt"]
#/Users/rithvijpochampally/Desktop/
ts_total = 0
tj_total = 0
count = 0
for path in paths:
    file = open(path, 'r')
    for line in file:
        if line and line[0].isnumeric():
            ts, tj = line.split()
            ts_total += int(ts)
            tj_total += int(tj)
            count += 1
    file.close()

print("Average TS:", float(round((ts_total/count)/1000000, 2)))
print("Average TJ:", float(round((tj_total/count)/1000000, 2)))