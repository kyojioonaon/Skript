package org.skriptlang.skript.addon;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.skriptlang.skript.Skript;
import org.skriptlang.skript.localization.Localizer;
import org.skriptlang.skript.registration.SyntaxRegistry;
import org.skriptlang.skript.util.ViewProvider;

/**
 * A Skript addon is an extension to Skript that expands its features.
 * Typically, an addon instance may be obtained through {@link Skript#registerAddon(String)}.
 */
@ApiStatus.Experimental
public interface SkriptAddon extends ViewProvider<SkriptAddon> {

	/**
	 * @return The name of this addon.
	 */
	String name();

	/**
	 * @return A syntax registry for this addon's syntax.
	 */
	SyntaxRegistry syntaxRegistry();

	/**
	 * @return A localizer for this addon's localizations.
	 */
	Localizer localizer();

	/**
	 * A helper method for loading addon modules.
	 * @param modules The modules to load.
	 */
	default void loadModules(AddonModule... modules) {
		for (AddonModule module : modules) {
			module.load(this);
		}
	}

	/**
	 * Constructs an unmodifiable view of this addon.
	 * That is, the returned addon will return unmodifiable views of its {@link #syntaxRegistry()} and {@link #localizer()}.
	 * @return An unmodifiable view of this addon.
	 * @see SyntaxRegistry#unmodifiableView()
	 * @see Localizer#unmodifiableView()
	 */
	@Override
	@Contract("-> new")
	default SkriptAddon unmodifiableView() {
		return new SkriptAddonImpl.UnmodifiableAddon(this);
	}

}
