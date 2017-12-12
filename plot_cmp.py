import numpy as np
import matplotlib.pyplot as plt
import os
import sys
import pylab

colors = ('r', 'b', 'g', 'm', 'c', 'y', 'k', 'violet', 'orange')

labels = ("(10,10)", "(20,10)", "(20,20)", "(30,10)", "(30,20)", "(30,30)", "Optimal value", 'Benchmark', 'NSGA-III')

def listdir_fullpath(d):
    return [os.path.join(d, f) for f in os.listdir(d)]

def plot(points, filename):
	print(filename)
	print(len(points))
	ax = []
	if len(points) == 2:
		fig = plt.figure(figsize=plt.figaspect(0.5))
		ax = fig.add_subplot(111)
		ax.set_title(filename)
		idSet = set(points[1])
		points = np.transpose(points)
		for id in idSet:
			curve = [a for a,b in points if b == id]
			curve = curve[:600]
			if id < len(labels):
				#ax.semilogy(range(len(curve)), curve, c=colors[int(id)], label=labels[int(id)])
				ax.plot(range(len(curve)), curve, c=colors[int(id)], label=labels[int(id)])
			else:
				#ax.semilogy(range(len(curve)), curve, c=colors[int(id)])
				ax.plot(range(len(curve)), curve, c=colors[int(id)])
			
		ax.get_yaxis().get_major_formatter().labelOnlyBase = False
		lgd = plt.legend(bbox_to_anchor=(0.8, 1), loc=2, borderaxespad=0.)
		#plt.grid('on')
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
	plt.savefig(filename + '.png', bbox_extra_artists=(lgd,), bbox_inches='tight')
	plt.savefig(filename + '.pdf', bbox_extra_artists=(lgd,), bbox_inches='tight')
	plt.show()

if __name__ == "__main__":
	filename = sys.argv[1]
	f = open(filename)	
	numObj = int(f.readline())
	inputdata = [ [float(x) for x in line.strip().split(' ')] for line in f]
	datapoints = np.transpose(np.asarray(inputdata))
	plot(datapoints, filename.split('.')[0])
