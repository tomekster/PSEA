#!/usr/bin/python
import os;
import numpy as np
import matplotlib.pyplot as plt

def readNLines(N, myfile):
    block = [next(myfile) for x in xrange(N)]
    return block

def addList(l1, l2):
    return [sum(x) for x in zip(l1,l2)]

dir = './'

numGen      = 0
popSize     = 0
numSolDir   = 0
numVar      = 0
numObj      = 0
numElicit   = 0

algNames        = ["NSGAIII_", "SingleCrit_"]
problemNames    = ["DTLZ1_", "DTLZ2_", "DTLZ3_", "DTLZ4_","WFG6_", "WFG7_"]
objs          = [3, 5, 8, 10, 15]

for problemName in problemNames:
    for obj in objs:
        for algName in algNames:
            prefix = algName + problemName + str(obj)
            filelist = [dir + x for x in os.listdir(dir) if prefix in x and not 'RES' in x and not 'pdf' in x]

            res = []
            for filename in filelist:
                f = open(filename);
                numGen, popSize, numSolDir, numVar, numObj, numElicit = [int(x) for x in readNLines(1, f)[0].split()];
                if len(res) == 0:
                    res = [0.0 for x in range(numGen)]
                data = [float(x.strip()) for x in readNLines(numGen, f)]
                res = addList(res, data)
            res = [x/len(filelist) for x in res]
            outFile = open("RES_" + algName + problemName + str(obj), 'w')
            if len(res) > 0:
                #for r in res:
                #    outFile.write(str(r))
                x = range(len(res));
                plt.plot(x, res)
                plt.savefig(problemName + str(obj) + '.pdf', bbox_inches='tight')
        print problemName + str(obj)
        plt.show()
        plt.clf()
