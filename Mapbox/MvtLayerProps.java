//Support MVT features that must reference properties by their key and value index.
//要素的 字段-字段索引 字典  字段值-字段值索引  字典
class MvtLayerProps
	private LinkedHashMap<String, Integer> keys;
	private LinkedHashMap<Object, Integer> vals;


	public Integer keyIndex(String k) {
	    return keys.get(k);
	}

	public Integer valueIndex(Object v) {
	    return vals.get(v);
	