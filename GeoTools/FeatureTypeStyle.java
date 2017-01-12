package org.geotools.styling;
public interface FeatureTypeStyle extends org.opengis.style.FeatureTypeStyle{
    Rule[] getRules();

    void setOnlineResource(OnLineResource online);


    Symbolizer Rule FeatureTypeStyle  Style
    Layer layer = new FeatureLayer(featureSource, style);
    map.addLayer(layer);