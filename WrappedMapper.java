 class WrappedMapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> 
    extends Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {

    	public Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>.Context
    	getMapContext(MapContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> mapContext) {
    	    return new Context(mapContext);

    	public class Context 
    	    extends Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>.Context {