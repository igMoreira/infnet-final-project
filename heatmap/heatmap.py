import pandas as pd
import folium
from folium.plugins import HeatMap

for_map = pd.read_csv('output.csv', sep=',')

m = folium.Map(location=[for_map.lat.mean(), for_map.lon.mean()],
               width=600,
               height=600,
               tiles="CartoDB positron",
               detect_retina=True,
               zoom_control=False,
               zoom_start=16)


good_readings = for_map[(for_map.co < 9)][['lat', 'lon']].values
moderate_readings = for_map[(for_map.co >= 9) & (for_map.co < 11)][['lat', 'lon']].values
bad_readings = for_map[(for_map.co >= 11) & (for_map.co < 13)][['lat', 'lon']].values
reallybad_readings = for_map[(for_map.co >= 13)][['lat', 'lon']].values

blur = 15
radius = 10
m.add_child(HeatMap(good_readings,
                    gradient={1: 'lime'},
                    blur=blur,
                    radius=radius))
m.add_child(HeatMap(moderate_readings,
                    gradient={1: 'yellow'},
                    blur=blur,
                    radius=radius))
m.add_child(HeatMap(bad_readings,
                    gradient={1: 'orange'},
                    blur=blur,
                    radius=radius))
m.add_child(HeatMap(reallybad_readings,
                    gradient={1: 'red'},
                    blur=blur,
                    radius=radius))
title_html = """
<div style="position: fixed; z-index:9999; top: 10px; left: 0px; width: 600px; height: auto; padding: 10px">
    <h1>Mapa de calor com concentrações de CO</h1>
</div>
"""
legend_html = """
<div style="position: fixed; z-index:9999; top: 420px; left: 400px; width: 180px; height: auto; font-size:14px; background: #FFF; padding: 10px">
    <h4>Legenda</h4>
    <ul style="list-style-type: none; margiin: 0; padding: 0;">
        <li><i class="fa fa-square" style="color:lime;"></i><span style="margin-left: 5px;">Boa (0–9 ppm)</span></li>
        <li><i class="fa fa-square" style="color:yellow"></i><span style="margin-left: 5px;">Moderada (9-11 ppm)</span></li>
        <li><i class="fa fa-square" style="color:orange"></i><span style="margin-left: 5px;">Ruim (11-13 ppm)</span></li>
        <li><i class="fa fa-square" style="color:red"></i><span style="margin-left: 5px;">Muito Ruim (>13 ppm)</span></li>
    </ul>
</div>
"""
m.get_root().html.add_child(folium.Element(title_html))
m.get_root().html.add_child(folium.Element(legend_html))
m.save('heatmap.html')
