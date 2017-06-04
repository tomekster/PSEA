import numpy as np
import matplotlib.pyplot as plt
import os
DIRS = ['3OBJ', '5OBJ', '8OBJ']
PROBLEMS = ['DTLZ1', 'DTLZ2', 'DTLZ3', 'DTLZ4']
DECIDENTS = ['1Balanced', '2LeftMostImportant', '3CentralMostImportant', '4RightMostImportant', '5LeftIrrelevant', '6CentralIrrelevant', '7RightIrrelevant', '8LinearyIncreasing', '9LinearyDecreasing']

START_GEN = 0
NUM_GEN = 1000


def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

def plotAll():
    files = []
    for d in DIRS:
        files = listdir_fullpath(d)
        files.sort()
        for p in PROBLEMS:
            fig = plt.figure()
            fig.suptitle(p + ' ' + d, fontsize=14, fontweight='bold')
            for decID, dec in enumerate(DECIDENTS):
                for i, filepath in enumerate(files):
                    if not dec in filepath or not p in filepath:
                        continue
                    points = np.loadtxt(filepath, delimiter=',')
                    #ax = fig.add_subplot(3,4,i+1)
                    X = points[START_GEN:NUM_GEN,0]
                    MIN = points[START_GEN:NUM_GEN,1]
                    AVG = points[START_GEN:NUM_GEN,2]
                    MODEL_DIST = points[START_GEN:NUM_GEN,3]
                    print p + " " + d + " " + dec
                    #ax.scatter(X,MIN)
                    #ax.scatter(X,AVG)
                    ax = plt.subplot(3,3,decID + 1)
                    axes = plt.gca()
                    axes.set_ylim([0,0.1])
                    ax.set_title(dec)
                    #plt.subplot(311)
                    plt.plot(X,MODEL_DIST)
                    #plt.subplot(312)
                    plt.plot(X,MIN)
                    #plt.subplot(313)
                    plt.plot(X,AVG)
        plt.show()

if __name__ == "__main__":
    plotAll()
