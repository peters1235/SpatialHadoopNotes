class MvtLayerParams
	int tileSize;
	int extent;

	public MvtLayerParams() {
        this(256, 4096);

    public MvtLayerParams(int tileSize, int extent) {
        this.tileSize = tileSize;
        this.extent = extent;
        this.ratio = extent / tileSize;
       