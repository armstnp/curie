package io.klbz.curie;

import java.util.Objects;

/**
 * A tiny stateful object that can be either off or on.
 * Useful for detecting and/or testing side effects.
 */
public final class Toggle {
	private boolean isOn;

	private Toggle(boolean isOn){ this.isOn = isOn; }

	public static Toggle on(){ return new Toggle(true); }

	public static Toggle off(){ return new Toggle(false); }

	public void toggle(){ isOn = !isOn; }

	public void turnOn(){ isOn = true; }

	public void turnOff(){ isOn = false; }

	public boolean isOn(){ return isOn; }

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Toggle toggle = (Toggle) o;
		return isOn == toggle.isOn;
	}

	@Override
	public int hashCode(){ return Objects.hash(isOn); }

	@Override
	public String toString(){ return "Toggle{" + (isOn ? "on" : "off") + '}'; }
}
