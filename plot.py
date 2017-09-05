import numpy as np
import matplotlib.pyplot as plt
import os
import sys
import pylab

def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

def plot(points):
    ax = []
    fig = plt.figure(figsize=plt.figaspect(1))
    if len(points) == 3:
    	for point in points:
	        ax.scatter(points[0],points[1], c=points[2])
    elif len(points) == 4:
        from mpl_toolkits.mplot3d import Axes3D
        ax = fig.add_subplot(1,1,1, projection='3d')
        ax.scatter(points[0],points[1],points[2], c=points[3])
    plt.show()

if __name__ == "__main__":
	f = open(sys.argv[1])	
	numObj = int(f.readline())
	inputdata = [ [float(x) for x in line.strip().split(' ')] for line in f]
	datapoints = np.transpose(np.asarray(inputdata))
	plot(datapoints)