package org.skriptlang.skript;

import ch.njol.skript.SkriptAPIException;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.localization.Localizer;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

final class SkriptImpl implements Skript {

	/**
	 * The addon instance backing this Skript.
	 */
	final SkriptAddon addon;

	/**
	 * {@link SkriptAddon#unmodifiableView(SkriptAddon)} of {@link #addon}.
	 */
	private final SkriptAddon unmodifiableAddon;

	SkriptImpl(String name) {
		addon = new SkriptAddonImpl(name, SyntaxRegistry.empty(), Localizer.of(this));
		unmodifiableAddon = SkriptAddon.unmodifiableView(addon);
	}

	//
	// SkriptAddon Management
	//

	private static final Set<SkriptAddon> addons = new HashSet<>();

	@Override
	public SkriptAddon registerAddon(String name) {
		// make sure an addon is not already registered with this name
		for (SkriptAddon addon : addons) {
			if (name.equals(addon.name())) {
				throw new SkriptAPIException(
					"An addon (provided by '" + addon.getClass().getName() + "') with the name '" + name + "' is already registered"
				);
			}
		}

		SkriptAddon addon = new SkriptAddonImpl(name, this.addon.syntaxRegistry(), null);
		addons.add(SkriptAddon.unmodifiableView(addon));
		return addon;
	}

	@Override
	@Unmodifiable
	public Collection<SkriptAddon> addons() {
		return ImmutableSet.copyOf(addons);
	}

	//
	// SkriptAddon Implementation
	//

	@Override
	public String name() {
		return unmodifiableAddon.name();
	}

	@Override
	@UnmodifiableView
	public SyntaxRegistry syntaxRegistry() {
		return unmodifiableAddon.syntaxRegistry();
	}

	@Override
	public Localizer localizer() {
		return unmodifiableAddon.localizer();
	}

	@Override
	public void loadModules(AddonModule... modules) {
		unmodifiableAddon.loadModules(modules);
	}

	private static final class SkriptAddonImpl implements SkriptAddon {

		private final String name;
		private final SyntaxRegistry registry;
		private final Localizer localizer;

		SkriptAddonImpl(String name, SyntaxRegistry registry, @Nullable Localizer localizer) {
			this.name = name;
			this.registry = registry;
			this.localizer = localizer == null ? Localizer.of(this) : localizer;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public SyntaxRegistry syntaxRegistry() {
			return registry;
		}

		@Override
		public Localizer localizer() {
			return localizer;
		}

	}

}
