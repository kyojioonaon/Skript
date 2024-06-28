package org.skriptlang.skript.addon;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.skriptlang.skript.Skript;
import org.skriptlang.skript.localization.Localizer;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * A Skript addon is an extension to Skript that expands its features.
 * Typically, an addon instance may be obtained through {@link Skript#registerAddon(String)}.
 */
@ApiStatus.Experimental
public interface SkriptAddon {

	/**
	 * Constructs an unmodifiable view of an addon.
	 * That is, the returned addon will return unmodifiable views of its {@link #registry()} and {@link #localizer()}.
	 * @param addon The addon backing this unmodifiable view.
	 * @return An unmodifiable view of <code>addon</code>.
	 * @see SyntaxRegistry#unmodifiableView(SyntaxRegistry)
	 * @see Localizer#unmodifiableView(Localizer)
	 */
	@Contract("_ -> new")
	@UnmodifiableView
	static SkriptAddon unmodifiableView(SkriptAddon addon) {
		return new SkriptAddonImpl.UnmodifiableAddon(addon);
	}

	/**
	 * @return The name of this addon.
	 */
	String name();

	/**
	 * @return A syntax registry for this addon's syntax.
	 */
	SyntaxRegistry registry();

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

}