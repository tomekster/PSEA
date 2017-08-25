import numpy as np
import matplotlib.pyplot as plt
import os
import statistics

from itertools import zip_longest

#DIM = ['OBJ3', 'OBJ5', 'OBJ8']
DIM = ['OBJ8']
PROBLEMS = ['DTLZ1', 'DTLZ2', 'DTLZ3', 'DTLZ4']
DECIDENTS = ['1Balanced', '2LeftMostImportant', '3CentralMostImportant', '4RightMostImportant', '5LeftIrrelevant', '6CentralIrrelevant', '7RightIrrelevant', '8LinearyIncreasing', '9LinearyDecreasing']
colors = ['aqua', 'blue', 'steelblue', 'lime', 'green', 'yellowgreen', 'orangered', 'red', 'darkred']
markers = ['.', 'o', '^']

def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

def readFile(filepath):
    points = np.loadtxt(filepath, delimiter=',')
    # ax = fig.add_subplot(3,4,i+1)
    print(filepath)
    return points.T

def combineStats(runStats):
    totalLen = max([len(s[0]) for s in runStats])

    def pad(totalLen, params):
        res = np.copy(params)
        res.resize((len(res)), totalLen)
        for p_idx, p in enumerate(params):
            for idx in range(p.size, totalLen):
                res[p_idx][idx] = p[-1]
        return res

    for idx, run in enumerate(runStats):
        runStats[idx] = pad(totalLen, run)

    for run in runStats:
        for p in run:
            assert len(p) == totalLen

    cube = np.dstack(tuple(runStats))
    return  np.array( [ [ (min(gen), statistics.median(gen), max(gen)) for gen in layer] for layer in cube])

def plot(plotId, dec, X, data, title_suf):
    ax = plt.subplot(2, 9, plotId)
    axes = plt.gca()
    # Set max Y-value
    axes.set_ylim([data[-1,-1,-1],data[-1,-1,-1] + 0.1])
    ax.set_title(dec + title_suf)

    for paramId in range(len(data)):
        for type in range(3):
            plt.plot(X, data[paramId, :, type], color=colors[paramId * 3 + type], marker=markers[type], markersize=1)

DIR = 'l4Cheb'

def plotAll():
    files = []
    for d in DIM:
        files = listdir_fullpath(DIR)
        files.sort()
        print(files)
        for p in PROBLEMS:
            fig = plt.figure()
            fig.suptitle(p + ' ' + d, fontsize=14, fontweight='bold')
            for decID, dec in enumerate(DECIDENTS):
                runs_descr = []
                for filepath in files:
                    if not d in filepath or not dec in filepath or not p in filepath:
                        continue
                    runs_descr.append(readFile(filepath))

                if(len(runs_descr) > 0 ):
                    combinedStats = combineStats(runs_descr)

                    X = range(len(combinedStats[0]))

                    plot(decID+1, dec, X, combinedStats[1:4], "CHEB")
                    plot(decID+10,dec,X, combinedStats[4:7], "EUC")

            plt.savefig(DIR + '/+' + p + ' ' + d + '.png',bbox_inches='tight')
            plt.savefig(DIR + '/+' + p + ' ' + d + '.pdf',bbox_inches='tight')
        #plt.show()

if __name__ == "__main__":
    plotAll()
