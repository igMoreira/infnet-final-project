import gmplot as gm
import json
import os
import gmaps as plotter
from ipywidgets.embed import embed_minimal_html

_base_path = 'dado_coletado_Iago'
files = [f for f in os.listdir(_base_path)]


readings = {'lat':list(), 'long':list(), 'co':list()}

figure_layout = {'width': '500px', 'height': '700px', 'margin': '0 auto 0 auto'}
fig = plotter.figure(center=(-22.9149622, -43.1759341), zoom_level=16, layout=figure_layout, map_type="TERRAIN")
test = list()
for f in files:
    with open(os.path.join(_base_path, f)) as r:
        data = json.load(r)
        readings['lat'].append(data['lat'])
        readings['long'].append(data['long'])
        readings['co'].append(data['co'])
        test.append((data['lat'], data['long']))

hl = plotter.heatmap_layer(test, weights=readings['co'], max_intensity=50, point_radius=7.0)
fig.add_layer(hl)
embed_minimal_html('export.html', views=[fig])
