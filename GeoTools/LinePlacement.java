package org.geotools.styling;
//The "LinePlacement" specifies where and how a text label should be rendered relative to a line. 
public interface LinePlacement extends org.opengis.style.LinePlacement, LabelPlacement {
	//计算文本应该离线多远，必须是非负数
	Expression getPerpendicularOffset();
	public void setRepeated(boolean repeated);
	public void setGeneralized(boolean generalized);
	public void setAligned(boolean aligned);
	public void setGap(Expression gap);
	public void setInitialGap(Expression initialGap);