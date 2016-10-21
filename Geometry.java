/*
The JTS Topology Suite is a collection of Java classes that
implement the fundamental operations required to validate a given
geo-spatial data set to a known topological specification.

A representation of a planar, linear vector geometry.

Binary Predicates
Because it is not clear at this time what semantics for spatial analysis methods involving GeometryCollections would be useful,
GeometryCollections are not supported as arguments to binary predicates or the relate method.

Overlay Methods
The overlay methods return the most specific class possible to represent the result. 
If the result is homogeneous, a Point, LineString, or Polygon will be returned if the result contains a single element; 
otherwise, a MultiPoint, MultiLineString, or MultiPolygon will be returned. 
If the result is heterogeneous a GeometryCollection will be returned.
Because it is not clear at this time what semantics for set-theoretic methods involving GeometryCollections would be useful, 
GeometryCollections are not supported as arguments to the set-theoretic methods.

Representation of Computed Geometries
The SFS states that the result of a set-theoretic method is the "point-set" result of the usual set-theoretic definition of the operation (SFS 3.2.21.1). 
However, there are sometimes many ways of representing a point set as a Geometry.
The SFS does not specify an unambiguous representation of a given point set returned from a spatial analysis method. 
One goal of JTS is to make this specification precise and unambiguous. 
JTS will use a canonical form for Geometrys returned from spatial analysis methods. 
The canonical form is a Geometry which is simple and noded:

Simple means that the Geometry returned will be simple according to the JTS definition of isSimple.
Noded applies only to overlays involving LineStrings. 
It means that all intersection points on LineStrings will be present as endpoints of LineStrings in the result.
This definition implies that non-simple geometries which are arguments to spatial analysis methods 
must be subjected to a line-dissolve process to ensure that the results are simple.

Constructed Points And The Precision Model
The results computed by the set-theoretic methods may contain constructed points which are not present in the input Geometry s. 
These new points arise from intersections between line segments in the edges of the input Geometrys. 
In the general case it is not possible to represent constructed points exactly. 
This is due to the fact that the coordinates of an intersection point may contain twice as many bits of precision as the coordinates of the input line segments. 
In order to represent these constructed points explicitly, JTS must truncate them to fit the PrecisionModel.
Unfortunately, truncating coordinates moves them slightly. 
Line segments which would not be coincident in the exact result may become coincident in the truncated representation. 
This in turn leads to "topology collapses" -- situations where a computed element has a lower dimension than it would in the exact result.

When JTS detects topology collapses during the computation of spatial analysis methods, it will throw an exception. 
If possible the exception will report the location of the collapse.

Geometry Equality
There are two ways of comparing geometries for equality: structural equality and topological equality.

Structural Equality
Structural Equality is provided by the equalsExact(Geometry) method. This implements a comparison based on exact, structural pointwise equality. The equals(Object) is a synonym for this method, to provide structural equality semantics for use in Java collections. It is important to note that structural pointwise equality is easily affected by things like ring order and component order. In many situations it will be desirable to normalize geometries before comparing them (using the norm() or normalize() methods). equalsNorm(Geometry) is provided as a convenience method to compute equality over normalized geometries, but it is expensive to use. Finally, equalsExact(Geometry, double) allows using a tolerance value for point comparison.

Topological Equality
Topological Equality is provided by the equalsTopo(Geometry) method. It implements the SFS definition of point-set equality defined in terms of the DE-9IM matrix. To support the SFS naming convention, the method equals(Geometry) is also provided as a synonym. However, due to the potential for confusion with equals(Object) its use is discouraged.

Since equals(Object) and hashCode() are overridden, Geometries can be used effectively in Java collections

*/

abstract class Geometry