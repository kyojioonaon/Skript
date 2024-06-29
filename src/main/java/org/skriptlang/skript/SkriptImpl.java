package org.skriptlang.skript;

import ch.njol.skript.SkriptAPIException;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
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
	private final SkriptAddon addon;

	SkriptImpl(String name) {
		addon = new SkriptAddonImpl(name, SyntaxRegistry.empty(), Localizer.of(this));
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
		addons.add(addon);
		return addon;
	}

	@Override
	public @Unmodifiable Collection<SkriptAddon> addons() {
		return ImmutableSet.copyOf(addons);
	}

	//
	// SkriptAddon Implementation
	//

	@Override
	public String name() {
		return addon.name();
	}

	@Override
	public SyntaxRegistry syntaxRegistry() {
		return addon.syntaxRegistry();
	}

	@Override
	public Localizer localizer() {
		return addon.localizer();
	}

	@Override
	public void loadModules(AddonModule... modules) {
		addon.loadModules(modules);
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

	static final class UnmodifiableSkript implements Skript {

		private final Skript skript;

		UnmodifiableSkript(Skript skript) {
			this.skript = skript;
		}

		@Override
		public SkriptAddon registerAddon(String name) {
			throw new UnsupportedOperationException("Cannot register addons using an unmodifiable Skript.");
		}

		@Override
		public @Unmodifiable Collection<SkriptAddon> addons() {
			ImmutableSet.Builder<SkriptAddon> addons = ImmutableSet.builder();
			skript.addons().stream()
					.map(SkriptAddon::unmodifiableView)
					.forEach(addons::add);
			return addons.build();
		}

		@Override
		public String name() {
			return skript.name();
		}

		@Override
		public SyntaxRegistry syntaxRegistry() {
			return skript.syntaxRegistry().unmodifiableView();
		}

		@Override
		public Localizer localizer() {
			return skript.localizer().unmodifiableView();
		}

		@Override
		public void loadModules(AddonModule... modules) {
			throw new UnsupportedOperationException("Cannot load modules using an unmodifiable Skript.");
		}
	}

}
