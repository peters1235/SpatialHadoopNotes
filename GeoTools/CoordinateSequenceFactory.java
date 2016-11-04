
/*
A factory to create concrete instances of CoordinateSequences.
 Used to configure GeometryFactorys to provide specific kinds of CoordinateSequences.
大概是生成 坐标串的工厂吧？
*/
interface CoordinateSequenceFactory
	CoordinateSequence create(Coordinate[] coordinates);

	//复制
	CoordinateSequence create(CoordinateSequence coordSeq);

	CoordinateSequence create(int size, int dimension);