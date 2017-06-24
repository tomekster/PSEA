import numpy as np
import matplotlib.pyplot as plt
import os
import statistics

from itertools import zip_longest

#DIM = ['OBJ3', 'OBJ5', 'OBJ8']
DIM = ['OBJ8']
PROBLEMS = ['DTLZ1', 'DTLZ2', 'DTLZ3', 'DTLZ4']
DECIDENTS = ['1Balanced', '2LeftMostImportant', '3CentralMostImportant', '4RightMostImportant', '5LeftIrrelevant', '6CentralIrrelevant', '7RightIrrelevant', '8LinearyIncreasing', '9LinearyDecreasing']

START_GEN = 0

class Stats:
    def __init__(self, NUM_GEN, MIN, AVG, DIST):
        self.NUM_GEN = NUM_GEN
        self.MIN = MIN
        self.AVG = AVG
        self.MODEL_DIST = DIST

    def pad(self, totalLen):
        self.MIN = np.concatenate( (self.MIN, np.full( (totalLen - self.MIN.size), self.MIN[-1] ) ) )
        self.AVG = np.concatenate((self.AVG, np.full((totalLen - self.AVG.size), self.AVG[-1])))
        self.MODEL_DIST = np.concatenate((self.MODEL_DIST, np.full((totalLen - self.MODEL_DIST.size), self.MODEL_DIST[-1])))

def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

def readFile(filepath):
    points = np.loadtxt(filepath, delimiter=',')
    # ax = fig.add_subplot(3,4,i+1)
    NUM_GEN = len(points)
    MIN = points[START_GEN:NUM_GEN, 1]
    AVG = points[START_GEN:NUM_GEN, 2]
    MODEL_DIST = points[START_GEN:NUM_GEN, 3]
    print(filepath)
    return Stats(NUM_GEN, MIN, AVG, MODEL_DIST)

def combineStats(runStats):
    totalLen = max(s.NUM_GEN for s in runStats)

    for s in runStats:
        s.pad(totalLen)

    for s in runStats:
        assert len(s.MIN) == totalLen


    allMin = np.array([s.MIN for s in runStats]).T
    allAvg = np.array([s.AVG for s in runStats]).T
    allModelDist = np.array([s.MODEL_DIST for s in runStats]).T

    minStats = ( [min(l) for l in allMin], [statistics.median(l) for l in allMin], [max(l) for l in allMin] )
    avgStats = ( [min(l) for l in allAvg], [statistics.median(l) for l in allAvg], [max(l) for l in allAvg] )
    modelDistStats = ( [min(l) for l in allModelDist], [statistics.median(l) for l in allModelDist], [max(l) for l in allModelDist] )

    return (totalLen, minStats, avgStats, modelDistStats)

def plotAll():
    files = []
    for d in DIM:
        files = listdir_fullpath('24_06_17')
        files.sort()
        for p in PROBLEMS:
            fig = plt.figure()
            fig.suptitle(p + ' ' + d, fontsize=14, fontweight='bold')
            for decID, dec in enumerate(DECIDENTS):
                STATISTICS = []
                for filepath in files:
                    if not d in filepath or not dec in filepath or not p in filepath:
                        continue
                    STATISTICS.append(readFile(filepath))

                # At this point STATISTICS should be list filled with Stats objects.
                # Each Stats object describes single experiment run.
                # STATISTICS should contain only Stats objecets describing same
                # experiment "type" - same NUM_OBJ, DECISION_MAKER, PROBLEM.

                #We need to unify all Stats objects - every single experiment could have different number of generations,
                # so we fill each array (MIN, AVG and MODEL_DIST) to length of the longest one in any of Stats objects.
                # We pad them with last value in each of them respectively.

                if(len(STATISTICS) > 0 ):
                    totalLen, minStats, avgStats, modelDistStats = combineStats(STATISTICS)
                    X = range(totalLen)

                    ax = plt.subplot(3,3,decID + 1)
                    axes = plt.gca()
                    #Set max Y-value
                    axes.set_ylim([0,0.1])
                    ax.set_title(dec)

                    plt.plot(X, minStats[0], color='aqua')
                    plt.plot(X, minStats[1], color='blue')
                    plt.plot(X, minStats[2], color='steelblue')

                    plt.plot(X, avgStats[0], color='lime')
                    plt.plot(X, avgStats[1], color='yellowgreen')
                    plt.plot(X, avgStats[2], color='green')

                    plt.plot(X, modelDistStats[0], color='orange')
                    plt.plot(X, modelDistStats[1], color='red')
                    plt.plot(X, modelDistStats[2], color='darkred')
        plt.show()

if __name__ == "__main__":
    plotAll()
