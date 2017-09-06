import numpy as np
import matplotlib.pyplot as plt
import os
import sys
import pylab

colors = ('r', 'b', 'g', 'c', 'm', 'y', 'k')

def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

def plot(points, filename):
	print(filename)
	print(len(points))
	ax = []
	if len(points) == 2:
		fig = plt.figure(figsize=plt.figaspect(0.5))
		plt.subplot(121)
		plt.title(filename)
		idSet = set(points[1])
		points = np.transpose(points)
		for id in idSet:
			curve = [a for a,b in points if b == id] 
			plt.plot(range(len(curve)), curve)
		plt.subplot(122)
		plt.title(filename + "(250+ gen)")
		for id in idSet:
			curve = [a for a,b in points if b == id] 
			plt.plot(range(len(curve))[250:], curve[250:])
	else:
		fig = plt.figure(figsize=plt.figaspect(1))
		
		if len(points) == 3:
			for point in points:
				ax.scatter( points[0], points[1], c=[colors[id] for id in points[2] ] )
		elif len(points) == 4:
			from mpl_toolkits.mplot3d import Axes3D
			ax = fig.add_subplot(1,1,1, projection='3d')
			ax.scatter(points[0],points[1],points[2], c=[ colors[int(id)] for id in points[3] ] )
			ax.view_init(30, 60)
		plt.title(filename)
	plt.savefig(filename + '.png', bbox_inches='tight')
	plt.savefig(filename + '.pdf', bbox_inches='tight')
	#plt.show()

if __name__ == "__main__":
	filename = sys.argv[1]
	f = open(filename)	
	numObj = int(f.readline())
	inputdata = [ [float(x) for x in line.strip().split(' ')] for line in f]
	datapoints = np.transpose(np.asarray(inputdata))
	plot(datapoints, filename.split('.')[0])