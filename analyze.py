import numpy as np
import matplotlib.pyplot as plt
import os

SINGLE_DIR = 'SingleObjective'
PSEA_DIR = 'PSEA'
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
        psea_files = listdir_fullpath(os.path.join(PSEA_DIR, d))
        single_files = listdir_fullpath(os.path.join(SINGLE_DIR, d))
        psea_files.sort()
        single_files.sort()
        for p in PROBLEMS:
            fig = plt.figure()
            fig.suptitle(p + ' ' + d, fontsize=14, fontweight='bold')
            for decID, dec in enumerate(DECIDENTS):
                for i, filepath in enumerate(psea_files):
                    if not dec in filepath or not p in filepath:
                        continue
                    points = np.loadtxt(filepath, delimiter=',')
                    X = points[START_GEN:NUM_GEN,0]
                    MIN = points[START_GEN:NUM_GEN,1]
                    AVG = points[START_GEN:NUM_GEN,2]
                    MODEL_DIST = points[START_GEN:NUM_GEN,3]
                    print p + " " + d + " " + dec
                    #ax.scatter(X,MIN)
                    #ax.scatter(X,AVG)
                    ax = plt.subplot(3,3,decID + 1)
                    axes = plt.gca()
                    axes.set_ylim([0,1])
                    ax.set_title(dec)
                    plt.plot(X,MODEL_DIST)
                    plt.plot(X,MIN)
                    plt.plot(X,AVG)
            '''
            for decID, dec in enumerate(DECIDENTS):
                for i, filepath in enumerate(single_files):
                    if not dec in filepath or not p in filepath:
                        continue
                    points = np.loadtxt(filepath, delimiter=',')
                    X = points[START_GEN:NUM_GEN,0]
                    MIN = points[START_GEN:NUM_GEN,1]
                    AVG = points[START_GEN:NUM_GEN,2]
                    plt.subplot(3, 3, decID + 1)
                    plt.plot(X,MIN)
                    plt.plot(X,AVG)
            '''
        plt.show()

if __name__ == "__main__":
    plotAll()
