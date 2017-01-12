/*
只支持一种颜色？
Canvas目前只有这一个子类
*/
class ImageCanvas extends Canvas
	BufferedImage image

	Graphics2D graphics;

	//x方向上 pixels per input units 把空间坐标乘以这个值 得到 转图像坐标合多少个像素
	double xscale;

	double yscale;

	Color color;

	ImageCanvas(Rectangle inputMBR, int width, int height) {
	    super(inputMBR, width, height);
	    System.setProperty("java.awt.headless", "true");
	    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    // Calculate the scale of the image in terms of pixels per unit
	    xscale = image.getWidth() / getInputMBR().getWidth();
	    yscale = image.getHeight() / getInputMBR().getHeight();

	void write(DataOutput out) throws IOException {
	    super.write(out);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(getImage(), "png", baos);
	    baos.close();
	    byte[] bytes = baos.toByteArray();
	    out.writeInt(bytes.length);
	    out.write(bytes);

	void mergeWith(ImageCanvas another) {
	    Point offset = projectToImageSpace(another.getInputMBR().x1, another.getInputMBR().y1);
	    getOrCreateGrahics(false).drawImage(another.getImage(), offset.x, offset.y, null);

	BufferedImage getImage() {
		//清空对象什么作用?
	    if (graphics != null) {
	      graphics.dispose();
	      graphics = null;

	    return image;

	Graphics2D getOrCreateGrahics(boolean translate) {
	    if (graphics == null) {
	        // Create graphics for the first time
	        try {
	            graphics = image.createGraphics();
	        } catch (Throwable e) {
	            graphics = new SimpleGraphics(image);
	        }
	        if (translate) {
	            // Translate the graphics to adjust its origin with the input origin
	            graphics.translate((int)(-getInputMBR().x1 * xscale), (int)(-getInputMBR().y1 * yscale));
	            graphics.setColor(color);
	    return graphics;

	void drawShape(Shape shape) {
	    Graphics2D g = getOrCreateGrahics(true);
	    shape.draw(g, xscale, yscale);