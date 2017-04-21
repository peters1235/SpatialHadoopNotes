class JtsAdapter
	static List<VectorTile.Tile.Feature> toFeatures(Collection<Geometry> flatGeoms,
	                                                           MvtLayerProps layerProps,
	                                                           IUserDataConverter userDataConverter) 
	    final List<VectorTile.Tile.Feature> features = new ArrayList<>();
	    final Vec2d cursor = new Vec2d();

	    VectorTile.Tile.Feature nextFeature;

	    for(Geometry nextGeom : flatGeoms) {
	        cursor.set(0d, 0d);
	        nextFeature = toFeature(nextGeom, cursor, layerProps, userDataConverter);
	        if(nextFeature != null) {
	            features.add(nextFeature);
	        }
	    }

	    return features;

	static VectorTile.Tile.Feature toFeature(Geometry geom,
	                                                     Vec2d cursor,
	                                                     MvtLayerProps layerProps,
	                                                     IUserDataConverter userDataConverter)
		//根据Geometry类型分别处理点、线、面，
		//然后把属性加进去
		//但是取属性时取的是 Geometry.getUserData