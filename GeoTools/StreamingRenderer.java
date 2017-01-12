StreamingRenderer 

    //目前用的
    void paint(Graphics2D graphics, Rectangle paintArea,ReferencedEnvelope mapArea) 
    	//计算出空间坐标到图像坐标之间的仿射变换之后 ，调用下面的paint
    	paint(graphics, paintArea, mapArea, RendererUtilities.worldToScreenTransform(mapArea, paintArea));
    		
    		RendererUtilities
    			/**
    		    * Helper class for building affine transforms. We use one instance per thread,
    		    * in order to avoid the need for {@code synchronized} statements.
    		    */
    		    private static final ThreadLocal<GridToEnvelopeMapper> gridToEnvelopeMappers =
    		        new ThreadLocal<GridToEnvelopeMapper>() {
    		            @Override
    		            protected GridToEnvelopeMapper initialValue() {
    		                final GridToEnvelopeMapper mapper = new GridToEnvelopeMapper();
    		                mapper.setGridType(PixelInCell.CELL_CORNER);
    		                return mapper;
    		            }
    		    };

    			AffineTransform worldToScreenTransform(ReferencedEnvelope mapExtent, Rectangle paintArea) {
    				final Envelope2D genvelope = new Envelope2D(mapExtent);
    				final GridToEnvelopeMapper m = (GridToEnvelopeMapper) gridToEnvelopeMappers.get();
    				m.setGridRange(new GridEnvelope2D(paintArea));
    				m.setEnvelope(genvelope);
    				return m.createAffineTransform().createInverse(); 






    void paint(Graphics2D graphics, Rectangle paintArea,ReferencedEnvelope mapArea, AffineTransform worldToScreen)
    	//先检查参数
 



	//worldToScreen 把世界坐标转成屏幕坐标 
	//paintArea 单位为像素
	//还需要 检查 图层坐标系如果与BoundingBox rendering CoordinateSystem 是否不同，是的话，还得进行转换
	/* 这两个好使些
	paint(Graphics2D graphics, Rectangle paintArea, ReferencedEnvelope mapArea) 
	 paint(Graphics2D graphics, Rectangle paintArea, ReferencedEnvelope mapArea, AffineTransform worldToScreen)
	*/
	void paint(Graphics2D graphics, Rectangle paintArea,AffineTransform worldToScreen) {
		//算出要绘制的空间范围，再绘制
	    mapArea = RendererUtilities.createMapEnvelope(paintArea,worldToScreen);
        paint(graphics, paintArea, mapArea, worldToScreen);
        	paint( graphics, paintArea, new ReferencedEnvelope(mapArea, mapContent.getCoordinateReferenceSystem()),worldToScreen);




org.geotools.geometry.Envelope2D
	//Java2D 和 GeoAPI 之间转换


/*
计算从n维的格网空间映射到某一外包的仿射变换
A helper class for building n-dimensional affine transform mapping grid ranges to envelopes. 
The affine transform will be computed automatically from the information specified by the setGridRange and setEnvelope methods, which are mandatory.
All other setter methods are optional hints about the affine transform to be created. This builder is convenient when the following conditions are meet: 

•Pixels coordinates (usually (x,y) integer values inside the rectangle specified by the grid range) are expressed in some coordinate reference system known at compile time. 
This is often the case. For example the CRS attached to BufferedImage has always (column, row) axis, with the origin (0,0) in the upper left corner, and row values increasing down.

•"Real world" coordinates (inside the envelope) are expressed in arbitrary horizontal coordinate reference system. Axis directions may be (North, West), or (East, North), etc..
	In such case (and assuming that the image's CRS has the same characteristics than the BufferedImage's CRS described above): 

•swapXY shall be set to true if the "real world" axis order is (North, East) instead of (East, North). 
 This axis swapping is necessary for mapping the (column, row) axis order associated to the image CRS.


•In addition, the "real world" axis directions shall be reversed (by invoking reverseAxis(dimension)) if their direction is WEST (x axis) or NORTH (y axis), 
 in order to get them oriented toward the EAST or SOUTH direction respectively. 
 The later may seems unatural, but it reflects the fact that row values are increasing down in an BufferedImage's CRS.
*/

org.geotools.referencing.operation.builder.GridToEnvelopeMapper
	
	public void setGridRange(final GridEnvelope gridRange) {
	    ensureNonNull("gridRange", gridRange); //判断非空
	    ensureDimensionMatch(gridRange, envelope, true); //判断维数相同
	    if (!Utilities.equals(this.gridRange, gridRange)) { //比较相等
	        this.gridRange = gridRange;
	        reset(); //刷新
	
	public void setEnvelope(final Envelope envelope) {
	    ensureNonNull("envelope", envelope);
	    ensureDimensionMatch(gridRange, envelope, false);
	    if (!Utilities.equals(this.envelope, envelope)) {
	        this.envelope = envelope;
	        reset();
	    
	public AffineTransform createAffineTransform() throws IllegalStateException {
	    final MathTransform transform = createTransform();
	    	
	    if (transform instanceof AffineTransform) {
	        return (AffineTransform) transform;




