import numpy as np
import matplotlib.pyplot as plt
import os
DIRS = ['3OBJ', '5OBJ', '8OBJ']
PROBLEMS = ['DTLZ1', 'DTLZ2', 'DTLZ3', 'DTLZ4']
DECIDENTS = ['1Balanced', '2Left', '3Central', '4Right', '5Left', '6Central', '7Right', '8Lineary', '9Lineary']

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
            for dec in DECIDENTS:
                fig = plt.figure()
                for i, filepath in enumerate(files):
                    if not dec in filepath or not p in filepath:
                        continue
                    points = np.loadtxt(filepath, delimiter=',')
                    #ax = fig.add_subplot(3,4,i+1)
                    X = points[START_GEN:NUM_GEN,0]
                    MIN = points[START_GEN:NUM_GEN,1]
                    AVG = points[START_GEN:NUM_GEN,2]
                    fig.suptitle(p + ' ' + d + ' ' + dec, fontsize=14, fontweight='bold')
                    print p + " " + d + " " + dec
                    #ax.scatter(X,MIN)
                    #ax.scatter(X,AVG)
                    plt.subplot(211)
                    plt.plot(X,MIN)
                    plt.subplot(212)
                    plt.plot(X,AVG)
                plt.show()

if __name__ == "__main__":
    plotAll()
