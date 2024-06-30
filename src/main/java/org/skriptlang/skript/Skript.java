package org.skriptlang.skript;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.skriptlang.skript.addon.SkriptAddon;

import java.util.Collection;

/**
 * The main class for everything related to Skript.
 */
@ApiStatus.Experimental
public interface Skript extends SkriptAddon {

	/**
	 * Constructs a default implementation of a Skript.
	 * It makes use of the default implementations of required components.
	 * @param name The name for the Skript to use.
	 * @return A Skript.
	 */
	@Contract("_ -> new")
	static Skript of(String name) {
		return new SkriptImpl(name);
	}

	/**
	 * Registers the provided addon with this Skript and loads the provided modules.
	 * @param name The name of the addon to register.
	 */
	@Contract("_ -> new")
	SkriptAddon registerAddon(String name);

	/**
	 * @return An unmodifiable snapshot of addons currently registered with this Skript.
	 */
	@Unmodifiable Collection<SkriptAddon> addons();

	/**
	 * Constructs an unmodifiable view of this Skript.
	 * That is, the returned Skript will be unable to register new addons
	 *  and the individual addons from {@link #addons()} will be unmodifiable.
	 * Additionally, it will return unmodifiable views of its inherited {@link SkriptAddon} components.
	 * @return An unmodifiable view of this Skript.
	 */
	@Override
	@Contract("-> new")
	default Skript unmodifiableView() {
		return new SkriptImpl.UnmodifiableSkript(this, SkriptAddon.super.unmodifiableView());
	}

}
