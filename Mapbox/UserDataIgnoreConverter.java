public final class UserDataIgnoreConverter implements IUserDataConverter
	//啥也不干，IUserDataConverter 是用来把要素的字段值转成mvt的tag的，

final class UserDataKeyValueMapConverter implements IUserDataConverter {
	//userData是一个字典，把其中的键值对添加到layerProps里，索引什么的以addTag的形式加
	//入featureBuilder中，要素id如果需要的话也记入featureBuilder
	void addTags(Object userData, MvtLayerProps layerProps, VectorTile.Tile.Feature.Builder featureBuilder) {
