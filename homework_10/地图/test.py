file = open('input.txt','r')
output = open('output.txt','w')

for line in file:
    for ch in line:
        if (ch != '\n'):
            output.write('%s '%ch)
    output.write('\n')


print('finished')
output.close()
file.close()
