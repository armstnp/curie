package io.klbz.curie;

import java.util.Objects;

/**
 * A tiny stateful object that carries a value.
 * Useful for detecting and/or testing side effects.
 */
public final class Box<T> {
	private T value;

	private Box(T value){ this.value = value; }

	public static <T> Box<T> boxed(T value){ return new Box<>(value); }

	public T getValue(){ return value; }

	public void setValue(T newValue){ this.value = newValue; }

	public boolean contains(T testValue){ return Objects.equals(value, testValue); }

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Box<?> box = (Box<?>) o;
		return Objects.equals(value, box.value);
	}

	@Override
	public int hashCode(){ return Objects.hash(value); }

	@Override
	public String toString(){ return "Box{" + value + '}'; }
}
