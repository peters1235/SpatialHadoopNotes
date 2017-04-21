TileGeomResult tileGeom = JtsAdapter.createTileGeom(inputGeom, tileEnvelope, geomFactory,
                DEFAULT_MVT_PARAMS, ACCEPT_ALL_FILTER);
final VectorTile.Tile mvt = encodeMvt(DEFAULT_MVT_PARAMS, tileGeom);
	// Build MVT
	final VectorTile.Tile.Builder tileBuilder = VectorTile.Tile.newBuilder();

	// Create MVT layer
	final VectorTile.Tile.Layer.Builder layerBuilder = MvtLayerBuild.newLayerBuilder("layerNameHere", mvtParams);
	final MvtLayerProps layerProps = new MvtLayerProps();
	final UserDataIgnoreConverter ignoreUserData = new UserDataIgnoreConverter();

	// MVT tile geometry to MVT features
	final List<VectorTile.Tile.Feature> features = JtsAdapter.toFeatures(tileGeom.mvtGeoms, layerProps, ignoreUserData);
	layerBuilder.addAllFeatures(features);
	MvtLayerBuild.writeProps(layerBuilder, layerProps);

	// Build MVT layer
	final VectorTile.Tile.Layer layer = layerBuilder.build();

	// Add built layer to MVT
	tileBuilder.addLayers(layer);

	/// Build MVT
	return tileBuilder.build();