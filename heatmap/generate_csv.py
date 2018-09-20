import os
import json
import csv


readings = {'lat': list(), 'long': list(), 'co': list()}
_base_path = 'dado_coletado_Iago'
files = [f for f in os.listdir(_base_path)]
# for f in files:
#     with open(os.path.join(_base_path, f)) as r:
#         data = json.load(r)
#         readings['lat'].append(data['lat'])
#         readings['long'].append(data['long'])
#         readings['co'].append(data['co'])

with open('output.csv', 'w', newline='') as csvfile:
    fieldnames = ['lat', 'lon', 'co']
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

    writer.writeheader()
    for f in files:
        with open(os.path.join(_base_path, f)) as r:
            data = json.load(r)
            if not data['lat'] or not data['long']:
                continue
            writer.writerow({'lat': data['lat'], 'lon': data['long'], 'co': data['co']})
    # writer.writerow({'first_name': 'Baked', 'last_name': 'Beans'})
    # writer.writerow({'first_name': 'Lovely', 'last_name': 'Spam'})
    # writer.writerow({'first_name': 'Wonderful', 'last_name': 'Spam'})
