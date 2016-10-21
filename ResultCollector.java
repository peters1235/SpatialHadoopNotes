public interface ResultCollector<R> {
  public void collect(R r);

public interface ResultCollector2<R, S> {
  public void collect(R r, S s);
}