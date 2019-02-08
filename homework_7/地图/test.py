file = open('mapgui.txt','r')
output = open('map.txt','w')

for line in file:
    for ch in line:
        if (ch != '\n'):
            output.write('%s '%ch)
    output.write('\n')


print('finished')
output.close()
file.close()
