
SimpleFeatureType extends FeatureType  extends ComplexType extends AttributeType  extends PropertyType 
interface SimpleFeature extends Feature  extends ComplexAttribute extends Attribute extends Property

CoordinateReferenceSystem dataCRS = SimpleFeatureType.getCoordinateReferenceSystem();
CoordinateReferenceSystem worldCRS = MapContent.getCoordinateReferenceSystem(); 

MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);

SimpleFeatureCollection featureCollection = featureSource.getFeatures();

DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
Map<String, Serializable> create = new HashMap<>();
create.put("url", file.toURI().toURL());
create.put("create spatial index", Boolean.TRUE);
DataStore dataStore = factory.createNewDataStore(create);
/*
Create a SimpleFeatureType with the same content;
 just updating the geometry attribute to match the provided coordinate reference system*/
SimpleFeatureType featureType = SimpleFeatureTypeBuilder.retype(schema, worldCRS);
/*
Creates storage for a new featureType. 

The provided featureType we be accessable by the typeName provided by featureType.getTypeName().
*/
dataStore.createSchema(featureType);

//Get the name of the new Shapefile, which will be used to open the FeatureWriter
/*
Gets the names of feature types available in this DataStore.
 Please note that this is not guaranteed to return a list of unique names since the same unqualified name may be present in separate namespaces within the DataStore.
注释 都看不懂了是几个意思？
*/
String createdName = dataStore.getTypeNames()[0];

Transaction transaction = new DefaultTransaction("Reproject");

try ( FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
					/*Gets a FeatureWriter that can add new features to the DataStore. 
					The FeatureWriter will return false when its hasNext() method is called, 、
					but next() can be used to acquire new features.
					*/
                dataStore.getFeatureWriterAppend(createdName, transaction);
    SimpleFeatureIterator iterator = featureCollection.features()){
    while (iterator.hasNext()) {
        // copy the contents of each feature and transform the geometry
        SimpleFeature feature = iterator.next();
        SimpleFeature copy = writer.next();
        copy.setAttributes(feature.getAttributes());

        Geometry geometry = (Geometry) feature.getDefaultGeometry();
        		//Transforms the geometry using the default transformer.
        Geometry geometry2 = JTS.transform(geometry, transform);       
    	    final GeometryCoordinateSequenceTransformer transformer = new GeometryCoordinateSequenceTransformer();
    	    transformer.setMathTransform(transform);

    	    return transformer.transform(geom);

        copy.setDefaultGeometry(geometry2);
        writer.write();
    }
    transaction.commit();
    JOptionPane.showMessageDialog(null, "Export to shapefile complete");
} catch (Exception problem) {
    problem.printStackTrace();
    transaction.rollback();
    JOptionPane.showMessageDialog(null, "Export to shapefile failed");
} finally {
    transaction.close();


 
//用坐标串作为输入生成Geometry对象，不会对输入的坐标进行取舍处理，they are not rounded to the supplied PrecisionModel.
class GeometryFactory implements Serializable


//创建CRS，坐标参考系统
    CoordinateReferenceSystem crs = org.geotools.referencing.CRS.decode("EPSG:26910", false);

    //CRS在内部使用CRSAuthorityFactory，所以也可以写成
    String code = "26910";
    CRSAuthorityFactory crsAuthorityFactory = ReferencingFactoryFinder.getCRSAuthorityFactory(
            "EPSG", null);
    CoordinateReferenceSystem crs = crsAuthorityFactory.createCoordinateReferenceSystem(code);


    //将WKT解析成CRS要用到CRSFactory
    CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
    String wkt = "PROJCS[\"UTM_Zone_10N\", " + "GEOGCS[\"WGS84\", " + "DATUM[\"WGS84\", "
            + "SPHEROID[\"WGS84\", 6378137.0, 298.257223563]], " + "PRIMEM[\"Greenwich\", 0.0], "
            + "UNIT[\"degree\",0.017453292519943295], " + "AXIS[\"Longitude\",EAST], "
            + "AXIS[\"Latitude\",NORTH]], " + "PROJECTION[\"Transverse_Mercator\"], "
            + "PARAMETER[\"semi_major\", 6378137.0], "
            + "PARAMETER[\"semi_minor\", 6356752.314245179], "
            + "PARAMETER[\"central_meridian\", -123.0], "
            + "PARAMETER[\"latitude_of_origin\", 0.0], " + "PARAMETER[\"scale_factor\", 0.9996], "
            + "PARAMETER[\"false_easting\", 500000.0], " + "PARAMETER[\"false_northing\", 0.0], "
            + "UNIT[\"metre\",1.0], " + "AXIS[\"x\",EAST], " + "AXIS[\"y\",NORTH]]";
    
    CoordinateReferenceSystem crs = crsFactory.createFromWKT(wkt);

    http://docs.geotools.org/latest/userguide/library/referencing/internal.html


    //mine
    GEOGCS["China Geodetic Coordinate System 2000", 
      DATUM["China 2000", 
        SPHEROID["CGCS2000", 6378137.0, 298.257222101, AUTHORITY["EPSG","1024"]], 
        AUTHORITY["EPSG","1043"]], 
      PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]], 
      UNIT["degree", 0.017453292519943295], 
      AXIS["Geodetic latitude", NORTH], 
      AXIS["Geodetic longitude", EAST], 
      AUTHORITY["EPSG","4490"]]

    GEOGCS["China Geodetic Coordinate System 2000", 
      DATUM["China 2000", 
        SPHEROID["CGCS2000", 6378137.0, 298.257222101, AUTHORITY["EPSG","1024"]], 
        AUTHORITY["EPSG","1043"]], 
      PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]], 
      UNIT["degree", 0.017453292519943295], 
      AXIS["Geodetic longitude", EAST], 
      AXIS["Geodetic latitude", NORTH], 
      AUTHORITY["EPSG","4490"]]