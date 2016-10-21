
/**
 * A simple graphics class that draws directly on an a BufferedImage and does
 * not require an active X11 display.
  *只实现了一部分方法，其它都未实现
*/
class SVGGraphics extends Graphics2D implements Writable 
	void drawLine(int x1, int y1, int x2, int y2) {
	    linesStart.append(xs.size());
	    xs.append(x1 + tx);
	    xs.append(x2 + tx);
	    ys.append(y1 + ty);
	    ys.append(y2 + ty);
	
	void fillRect(int x, int y, int width, int height) {
	    rectanglesStart.append(xs.size());
	    xs.append(x + tx);
	    ys.append(y + ty);
	    xs.append(width);
	    ys.append(height);

	void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
	    polylinesStart.append(xs.size());
	    polylinesSize.append(nPoints);
	    xs.append(xPoints, 0, nPoints, tx);
	    ys.append(yPoints, 0, nPoints, ty);
 
	void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
	  polygonsStart.append(xs.size());
	  polygonsSize.append(nPoints);
	  xs.append(xPoints, 0, nPoints, tx);
	  ys.append(yPoints, 0, nPoints, ty);

	void mergeWith(SVGGraphics other, int tx, int ty) {
	  // 1- Merge points
	  int pointsShift = this.xs.size();
	  this.xs.append(other.xs, tx);
	  this.ys.append(other.ys, ty);
	  
	  // 2- Merge lines
	  this.linesStart.append(other.linesStart, pointsShift);

	  // 3- Merge polylines
	  this.polylinesStart.append(other.polylinesStart, pointsShift);
	  this.polylinesSize.append(other.polylinesSize);
	  
	  // 4- Merge polygons
	  this.polygonsStart.append(other.polygonsStart, pointsShift);
	  this.polygonsSize.append(other.polygonsSize);
	  
	  // 5- Merge rectangles
	  this.rectanglesStart.append(other.rectanglesStart, pointsShift);
	}

	
	void writeAsSVG(PrintStream p) {
	  // Write header
	  p.println("<?xml version='1.0' standalone='no'?>");
	  p.println("<!DOCTYPE svg '-//W3C/DTD SVG 1.1//EN' "
	      + "'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'>");
	  p.printf("<svg width='%d' height='%d' version='1.1' "
	      + "xmlns='http://www.w3.org/2000/svg'>\n", width, height);

	  // Retrieve all xs and ys as java arrays for efficiency
	  int[] xs = this.xs.underlyingArray();
	  int[] ys = this.ys.underlyingArray();
	  // 1- Draw all lines
	  if (!linesStart.isEmpty()) {
	    p.printf("<g style='stroke:rgb(0,0,0);'>\n");
	    for (int i = 0; i < linesStart.size(); i++) {
	      int lineStart = linesStart.get(i);
	      p.printf("<line x1='%d' y1='%d' x2='%d' y2='%d'/>\n",
	          xs[lineStart], ys[lineStart], xs[lineStart+1], ys[lineStart+1]);
	    }
	    p.printf("</g>\n");
	  }
	  
	  // 2- Draw all polygons
	  if (!polygonsStart.isEmpty()) {
	    p.printf("<g style='stroke:rgb(0,0,0);'>\n");
	    for (int i = 0; i < polygonsStart.size(); i++) {
	      int polygonStart = polygonsStart.get(i);
	      int polygonSize = polygonsSize.get(i);
	      p.print("<polygon points='");
	      for (int j = polygonStart; j < polygonStart + polygonSize; j++) {
	        p.printf("%d,%d ", xs[j], ys[j]);
	      }
	      p.println("'/>");
	    }
	    p.printf("</g>\n");
	  }
	  
	  // 3- Draw all polylines
	  if (!polylinesStart.isEmpty()) {
	    p.printf("<g style='stroke:rgb(0,0,0); fill:none;'>\n");
	    for (int i = 0; i < polylinesStart.size(); i++) {
	      int polylineStart = polylinesStart.get(i);
	      int polylineSize = polylinesSize.get(i);
	      p.print("<polyline points='");
	      for (int j = polylineStart; j < polylineStart + polylineSize; j++) {
	        p.printf("%d,%d ", xs[j], ys[j]);
	      }
	      p.println("'/>");
	    }
	    p.printf("</g>\n");
	    
	  }
	  // 4- Draw all rectangles
	  if (!rectanglesStart.isEmpty()) {
	    p.printf("<g style='stroke:rgb(0,0,0); fill:none;'>\n");
	    for (int i = 0; i < rectanglesStart.size(); i++) {
	      int rectStart = rectanglesStart.get(i);
	      p.printf("<rect x='%d' y='%d' width='%d' height='%d'/>\n",
	          xs[rectStart], ys[rectStart], xs[rectStart+1], ys[rectStart+1]);
	    }
	    p.printf("</g>\n");
	  }
	  
	  p.println("</svg>");
	}