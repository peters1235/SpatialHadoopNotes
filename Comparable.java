
/*
实现了这个接口的类的所有对象可以用这个接口来实现排序。这个顺序称为类的natural ordering （自然排列），compareTo方法则称为
natural comparison method
实现 这个接口的类 添加 到 Sortedmap  SortedList 时不用指定Cmoparator就能排序 

自然排列与 相等 比较的关系：
The natural ordering for a class C is said to be consistent with equals 
if and only if e1.compareTo(e2) == 0 has the same boolean value as e1.equals(e2) for every e1 and e2 of class C. 
Note that null is not an instance of any class, 
and e.compareTo(null) should throw a NullPointerException even though e.equals(null) returns false.
*/
public interface Comparable<T> {

	/*将本对象与传入的对象比，负数  0  正数 分别 表示 本对象 小于 等于 大于传入的对象
要求
1 x.compareTo(y) 与 y.compareTo(x) 反号， y.compareTo(x) 如果抛出异常x.compareTo(y) 也应该抛出异常
2  传播律 (x.compareTo(y)>0 && y.compareTo(z)>0)  则  x.compareTo(z)>0.
3   x.compareTo(y)==0 则   x.compareTo(z)  与y.compareTo(z) 同号 
4   最好  (x.compareTo(y)==0) == (x.equals(y))
 
It is strongly recommended, but not strictly required that (x.compareTo(y)==0) == (x.equals(y)). Generally speaking, any class that implements the Comparable interface and violates this condition should clearly indicate this fact. The recommended language is "Note: this class has a natural ordering that is inconsistent with equals."
 */
	public int compareTo(T o);