package io.klbz.curie;

public final class Unit {
	public static final Unit unit = new Unit();

	private Unit(){}

	public static Unit unit(){ return unit; }

	@Override
	public boolean equals(Object obj){ return this == obj; }

	@Override
	public int hashCode(){ return 0; }

	@Override
	public String toString(){ return "Unit"; }
}
