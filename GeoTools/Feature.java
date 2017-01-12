TypeFactory typeFactory = CommonFactoryFinder.getTypeFactory( null );
SimpleTypeFactory featureTypeFactory =   CommonFactoryFinder.getSimpleTypeFeatureFactory( null );

URI namespace = new URI("http://localhost/Flag/");
CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");

Name locationName = new NameImpl( namespace, "Location" );
InternationalString locationDescription = new SimpleInternationalString("Location of the base of this Flag, in WSG84");
GeometryAttributeType GEOM = typeFactory.createGeometryType( locationName, Point.class, crs, false, false, null, null, locationDescription );

Name idName = new NameImpl( namespace, "Id" );
AttributeType ID = typeFactory.createAttributeType( idName, Integer.class, false, false, null, null, null );

Name locationName = new NameImpl( namespace, "Name" );
AttributeType NAME = typeFactory.createAttributeType( nameName, String.class, false, false, null, null, null );

Name name = new NameImpl( new URI("http://localhost/"), "Flag" );
InternationalString description = new SimpleInternationalString("A Flag used to place a marker on the world");

AttributeDescriptor defaultGeoemtry = typeFactory.createAttributeDescriptor(GEOM, geomName, 1, 1, true, null );

List<AttributeDescriptor> types = new ArrayList<AttributeDescriptor>();
types.add( defaultGeometry );
types.add( typeFactory.createAttributeDescriptor(ID, idName, 1, 1, false, new Integer(0) ) );
types.add( typeFactory.createAttributeDescriptor(NAME, nameName, 1, 1, true, null ) );

final FeatureType FLAG = featureTypeFactory.createSimpleFeatureType( name, types, defaultGeometry, crs, Collections.EMPTY_SET, description );