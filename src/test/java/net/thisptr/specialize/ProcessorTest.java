package net.thisptr.specialize;

import net.thisptr.specialize.annotation.InjectPrimitive;
import net.thisptr.specialize.annotation.InjectPrimitives;
import net.thisptr.specialize.annotation.Specialize;
import net.thisptr.specialize.annotation.Specializes;

import org.junit.Test;


public class ProcessorTest {
	
	@Specializes({
		@Specialize(type = {int.class, double.class}, key = "T"),
		@Specialize(type = {int.class, double.class}, key = "U")
	})
	public static class Pair<T, U> {
		public final T first;
		public final U second;
		
		public Pair(final T first, final U second) {
			this.first = first;
			this.second = second;
		}
	}
	
	@Specializes({
		@Specialize(type = {int.class, float.class}, key = "Item"),
		@Specialize(type = {int.class, double.class}, key = "Score", generic = false)
	})
	public static class BasicScoredItem<Item, Score> {
		public final Item item;
		public final Score score;
		
		public BasicScoredItem(final Item item, final Score score) {
			this.item = item;
			this.score = score;
		}
	}

	@InjectPrimitive(key = "T", type = double.class)
	public static class ScoredItem<Item> extends BasicScoredItem<Item, T> {
		public ScoredItem(final Item item, final T score) {
			super(item, score);
		}
	}
	
	@InjectPrimitives({
		@InjectPrimitive(key = "T", type = int.class),
		@InjectPrimitive(key = "U", type = int.class)
	})
	public static class IntIntPair extends Pair<T, U> {
		public IntIntPair(final T first, final U second) {
			super(first, second);
		}
	}
	
	@Specialize(type = {int.class, float.class}, key = "T")
	public static class AtomicValue<T> {
		private T value;
		
		public AtomicValue(final T value) {
			this.value = value;
		}
		
		public synchronized T getValue() {
			return value;
		}
		
		public synchronized void setValue(final T value) {
			this.value = value;
		}
	}

	@Specialize(type = {int.class, float.class}, key = "T", generic = false)
	@InjectPrimitive(type = int.class, key = "U")
	public static <T, U> T someProcess(final AtomicValue<T> i, final AtomicValue<U> p) {
		return i.getValue() + p.getValue();
	}

	@Specialize(key = "T", type = {int.class, float.class}, generic = false)
	public static <T> T sum(final T[] values) {
		T result = 0;
		for (final T value : values)
			result += value;
		return result;
	}

	@Test
	public void main() {
		AtomicValue<int> hoge =  new AtomicValue<int>(10);
		hoge = new AtomicValue<int>(20);
		hoge.setValue(20);
		final int value = hoge.getValue();
		System.out.println(value);

		final BasicScoredItem<String, double> a = new BasicScoredItem<String, double>("hoge", 2.0);
		final double aScore = a.score;
		System.out.println(aScore);

		final ScoredItem<String> b = new ScoredItem<String>("hoge", 2.0);
		final double bScore = b.score;
		System.out.println(bScore);

		final int value2 = someProcess(hoge, hoge);
	}
}
