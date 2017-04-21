class MvtLayerBuild 
	public static VectorTile.Tile.Layer.Builder newLayerBuilder(String layerName, MvtLayerParams mvtLayerParams) {
	    final VectorTile.Tile.Layer.Builder layerBuilder = VectorTile.Tile.Layer.newBuilder();
	    layerBuilder.setVersion(2);
	    layerBuilder.setName(layerName);
	    layerBuilder.setExtent(mvtLayerParams.extent);

	    return layerBuilder;