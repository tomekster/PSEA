import numpy as np
import matplotlib.pyplot as plt
import os
import sys
def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

def plot(l1, l2):
    ax = []
    fig = plt.figure(figsize=plt.figaspect(0.33))
    if len(l1[0]) == 2:
        x1 = l1[:,0]
        y1 = l1[:,1]
        x2 = l2[:,0]
        y2 = l2[:,1]
        ax = fig.add_subplot(131)
        ax.scatter(x1,y1, c='r', marker='o')
        ax.scatter(x2,y2, c='b',marker='^')
        ax = fig.add_subplot(132)
        ax.scatter(x1,y1, c='r', marker='o')
        ax = fig.add_subplot(133)
        ax.scatter(x2,y2, c='b',marker='^')

    elif len(l1[0]) == 3:
        from mpl_toolkits.mplot3d import Axes3D
        ax = fig.add_subplot(1,3,1, projection='3d')
        x1 = l1[:,0]
        y1 = l1[:,1]
        z1 = l1[:,2]
        x2 = l2[:,0]
        y2 = l2[:,1]
        z2 = l2[:,2]
        ax.scatter(x1,y1,z1, c='r')
        ax.scatter(x2,y2,z2, c='b')
        ax = fig.add_subplot(1,3,2, projection='3d')
        ax.scatter(x1,y1,z1, c='r')
        ax = fig.add_subplot(1,3,3, projection='3d')
        ax.scatter(x2,y2,z2, c='b')
    plt.show()

if __name__ == "__main__":
	f = open(sys.argv[1])
	numObj = int(f.readline())
	n = int(f.readline())
	m = int(f.readline())
	print(numObj)
	print(n)
	print(m)
	inputdata = [ [float(x) for x in line.strip().split(' ')] for line in f]
	#plot(np.asarray([[1,1,1]]), np.asarray([[2,2,2]]))
	if len(inputdata) != n+m:
		plot(np.asarray([[1,1,1]]), np.asarray([[2,2,2]]))
		print("Wrong number of input points provided!")
		exit(0);
	referenceFront = np.asarray(inputdata[:n])
	nondominatedSolutions = np.asarray(inputdata[n:])

	plot(referenceFront, nondominatedSolutions)