import numpy as np
import matplotlib.pyplot as plt
import os
import sys
import pylab
import matplotlib.colors as mcol
import matplotlib.cm as cm

def plot(points, filename):

	x = points[0]
	y = points[1]
	z = points[2]
	val = points[3]
	
	minAsf = min(val)
	maxAsf = max(val)
	
	#Generate labels for bar
	tim = np.linspace(minAsf, maxAsf, 11)	
	
	# Make a user-defined colormap.
	cm1 = mcol.LinearSegmentedColormap.from_list("MyCmapName",["r","b"])
	
	# Make a normalizer that will map the time values from
	# [minAsf,maxAsf+1] -> [0,1].
	cnorm = mcol.Normalize(vmin=min(val),vmax=max(val))
	
	# Turn these into an object that can be used to map time values to colors and
	# can be passed to plt.colorbar().
	cpick = cm.ScalarMappable(norm=cnorm,cmap=cm1)
	cpick.set_array([])
	
	
	#F = plt.figure()
	#A = F.add_subplot(111)
	#for y, t in zip(ydat,tim):
	#    A.plot(xdat,y,color=cpick.to_rgba(t))
	
	
	from mpl_toolkits.mplot3d import Axes3D
	fig = plt.figure(figsize=plt.figaspect(0.5))
	ax = fig.add_subplot(1,1,1, projection='3d')
	ax.scatter(x,y,z, c=[cpick.to_rgba(v) for v in val] )
	plt.title(filename)
	plt.colorbar(cpick,label="ASF value")
	ax.view_init(30, 60)
	#plt.show()
	
	plt.savefig(filename + '.png', bbox_inches='tight')
	plt.savefig(filename + '.pdf', bbox_inches='tight')

if __name__ == "__main__":
	filename = sys.argv[1]
	f = open(filename)	
	numObj = int(f.readline())
	inputdata = [ [float(x) for x in line.strip().split(' ')] for line in f]
	datapoints = np.transpose(np.asarray(inputdata))
	plot(datapoints, filename.split('.')[0])