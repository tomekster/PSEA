#! /usr/bin/python

#Reads knapsack problem description in Zitzler-Thiele file format
#and creates new file called parsed_<original_filename> in following format:
# num_knapsacks
# num_items
# knapsack1_id
# knapsck1_capacity
# knapsack1_item1_id knapsack1_item2_id knapsack1_item3_id ...
# knapsack1_item1_weight knapsack1_item2_weight knapsack1_item3_weight ...
# knapsack1_item1_price knapsack1_item2_price knapsack1_item3_price ...
# Similarly for the rest of knapsacks...


from __future__ import print_function
import sys

class Knapsack:
    def __init__(self, id, capacity):
        self.id = id
        self.capacity = capacity
        self.item_ids = []
        self.weights = []
        self.profits = []

    def tostring(self):
        l1 = str(self.id) + "\n" + str(self.capacity)
        l2 = str(self.item_ids)[1:-1].replace(',','')
        l3 = str(self.weights)[1:-1].replace(',','')
        l4 = str(self.profits)[1:-1].replace(',','')

        return l1 + '\n' + l2 + '\n' + l3 + '\n' + l4 + '\n'
        #print(*self.item_ids, sep=' ')
        #print(*self.weights, sep=' ')
        #print(*self.profits, sep=' ')

class Item:
    def __init__(self,id,weight,profit):
        self.id = id
        self.weight = weight
        self.profit = profit

    def prt(self):
        print( str(self.id) + " " + str(self.weight) + " " + str(self.profit))
if(len(sys.argv) < 2):
    print('WRONG NUMBER OF ARGUMENTS!')
    print('Usage: python parse_knap.py kanp_def_filename')
    exit()

filename = sys.argv[1]
f = open(filename, 'r')
l = f.readline().split('(')[1].split() # Get num_knapsacks and num_items from first line
num_knapsacks = int(l[0])
num_items = int(l[2])

knapsacks = []

for k in range(num_knapsacks):
    f.readline() # '=' line

    l = f.readline().strip().split()
    knap_id = int(l[-1].replace(':', '')) # Get knapsack id

    l = f.readline().strip().split()
    capacity = int(l[-1]) # Get knapsack capacity

    knapsack = Knapsack(knap_id, capacity)

    items = []

    for i in range(num_items):
        item_id = int(f.readline().strip().split()[-1].replace(':',''))
        weight = int(f.readline().strip().split()[-1])
        profit = int(f.readline().strip().split()[-1])

        items.append(Item(item_id, weight, profit))
        items.sort(key=lambda x: x.id)

    for item in items:
        #item.prt()
        knapsack.item_ids.append(item.id)
        knapsack.weights.append(item.weight)
        knapsack.profits.append(item.profit)
    print(knapsack.tostring())
    knapsacks.append(knapsack)

out_name = "parsed_" + filename
fout = open(out_name, 'w')
fout.write(str(num_knapsacks) + "\n" + str(num_items) + '\n')
for k in knapsacks:
    fout.write(k.tostring(),)
