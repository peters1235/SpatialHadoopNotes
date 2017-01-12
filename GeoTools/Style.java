package org.geotools.styling;
public interface Style extends org.opengis.style.Style {
	void setName(String name);
	    String getTitle();
	        String getAbstract();
	        public List<FeatureTypeStyle> featureTypeStyles();
	        public Symbolizer getDefaultSpecification();
	        FeatureTypeStyle[] getFeatureTypeStyles();