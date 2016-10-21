/*
describes an Image with an accessible buffer of image data.
由ColorModel 和Raster 组成 。Raster的SampleModel中的波段数、波段类型必须和ColorModel中的一样
BufferedImage 对象的左我角坐标必须为0 0 
传递给BufferedImage的 Raster 必须满足 minX=0 and minY=0. 

这个类使用Raster的数据存取方法以及 ColorModel来进行颜色相关的设置

*/
class BufferedImage extends java.awt.Image implements WritableRenderedImage, Transparency
	Graphics2D createGraphics() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return env.createGraphics(this);