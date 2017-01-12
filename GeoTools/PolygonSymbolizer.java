package org.geotools.styling;

/*
包含Geometry和符号，控制地图上的显示
A symbolizer describes how a polygon feature should appear on a map. 

The symbolizer describes not just the shape that should appear but also such graphical properties as color and opacity. 

A symbolizer is obtained by specifying one of a small number of different types of symbolizer and then supplying parameters to overide its default behaviour. 

*/
interface PolygonSymbolizer 
  extends org.opengis.style.PolygonSymbolizer,Symbolizer {


//所有Symbolizers的父接口
public interface Symbolizer extends org.opengis.style.Symbolizer{

//给symbolizers 添加条件，并管理symbolizers 
public interface Rule extends org.opengis.style.Rule {
	 void setName(String name);

	 Description getDescription();
	 void setDescription(org.opengis.style.Description description);
	 String getTitle();
	 void setTitle(String title);
	 String getAbstract();
	 void setAbstract(String abstractStr);
	 void setMinScaleDenominator(double scale);
	 void setMaxScaleDenominator(double scale);
	 Filter getFilter();
	 void setFilter(Filter filter);
	 boolean hasElseFilter();
	 void setElseFilter(boolean isElse);


	 void setIsElseFilter( boolean isElse );
 
	 public GraphicLegend getLegend();
 
	 void setLegend( GraphicLegend legend);

	  Graphic[] getLegendGraphic();
	  void setLegendGraphic(Graphic[] graphics);
	  Symbolizer[] getSymbolizers();
	  List<org.geotools.styling.Symbolizer> symbolizers();
	  void setSymbolizers(Symbolizer[] symbolizers)
	  public OnLineResource getOnlineResource();
	  void setOnlineResource(OnLineResource online);

filter graphic symbolizer OnLineResource
/*
A Graphic is a "graphical symbol" with an inherent shape, color(s), and possibly size. 

A "graphic" can very informally be defined as "a little picture" and can be of
 either a raster or vector graphic source type. The term graphic is used since
  the term "symbol" is similar to "symbolizer" which is used in a difference 
  context in SLD. The graphical symbol to display can be provided either as an
   external graphical resource or as a Mark.
Multiple external URLs and marks can be referenced with the proviso that they 
all provide equivalent graphics in different formats. The 'hot spot' to use for
 positioning the rendering at a point must either be inherent from the external 
 format or be defined to be the "central point" of the graphic. 
*/
 interface Graphic extends GraphicLegend,
                                 org.opengis.style.Graphic,
                                 org.opengis.style.GraphicFill, 
                                 org.opengis.style.GraphicStroke 

package org.opengis.filter;
//查询条件中的Where
public interface Filter {
///////////////////////另一个包

package org.opengis.style;
public interface PolygonSymbolizer extends Symbolizer {
	Stroke getStroke();
	Fill getFill();
	Displacement getDisplacement();
	Expression getPerpendicularOffset();
	Object accept(StyleVisitor visitor, Object extraData);

package org.opengis.style;
public interface Symbolizer {

