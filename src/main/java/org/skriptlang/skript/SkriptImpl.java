package org.skriptlang.skript;

import ch.njol.skript.SkriptAPIException;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.localization.Localizer;
import org.skriptlang.skript.registration.SyntaxRegistry;
import org.skriptlang.skript.util.Registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

final class SkriptImpl implements Skript {

	/**
	 * The addon instance backing this Skript.
	 */
	private final SkriptAddon addon;

	SkriptImpl(String name) {
		addon = new SkriptAddonImpl(this, name, Localizer.of(this));
		storeRegistry(SyntaxRegistry.class, SyntaxRegistry.empty());
	}

	/*
	 * Registry Management
	 */

	private static final Map<Class<?>, Registry<?>> registries = new ConcurrentHashMap<>();

	@Override
	public <R extends Registry<?>> void storeRegistry(Class<R> registryClass, R registry) {
		registries.put(registryClass, registry);
	}

	@Override
	public void removeRegistry(Class<? extends Registry<?>> registryClass) {
		registries.remove(registryClass);
	}

	@Override
	public boolean hasRegistry(Class<? extends Registry<?>> registryClass) {
		return registries.containsKey(registryClass);
	}

	@Override
	public <R extends Registry<?>> R registry(Class<R> registryClass) {
		//noinspection unchecked
		R registry = (R) registries.get(registryClass);
		if (registry == null)
			throw new NullPointerException("Registry not present for " + registryClass);
		return registry;
	}

	@Override
	public <R extends Registry<?>> R registry(Class<R> registryClass, Supplier<R> putIfAbsent) {
		//noinspection unchecked
		return (R) registries.computeIfAbsent(registryClass, key -> putIfAbsent.get());
	}

	/*
	 * SkriptAddon Management
	 */

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

		SkriptAddon addon = new SkriptAddonImpl(this, name, null);
		addons.add(addon);
		return addon;
	}

	@Override
	public @Unmodifiable Collection<SkriptAddon> addons() {
		return ImmutableSet.copyOf(addons);
	}

	/*
	 * SkriptAddon Implementation
	 */

	@Override
	public String name() {
		return addon.name();
	}

	@Override
	public SyntaxRegistry syntaxRegistry() {
		return registry(SyntaxRegistry.class);
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

		private final Skript skript;
		private final String name;
		private final Localizer localizer;

		SkriptAddonImpl(Skript skript, String name, @Nullable Localizer localizer) {
			this.skript = skript;
			this.name = name;
			this.localizer = localizer == null ? Localizer.of(this) : localizer;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public <R extends Registry<?>> void storeRegistry(Class<R> registryClass, R registry) {
			skript.storeRegistry(registryClass, registry);
		}

		@Override
		public void removeRegistry(Class<? extends Registry<?>> registryClass) {
			skript.removeRegistry(registryClass);
		}

		@Override
		public boolean hasRegistry(Class<? extends Registry<?>> registryClass) {
			return skript.hasRegistry(registryClass);
		}

		@Override
		public <R extends Registry<?>> R registry(Class<R> registryClass) {
			return skript.registry(registryClass);
		}

		@Override
		public <R extends Registry<?>> R registry(Class<R> registryClass, Supplier<R> putIfAbsent) {
			return skript.registry(registryClass, putIfAbsent);
		}

		@Override
		public SyntaxRegistry syntaxRegistry() {
			return skript.syntaxRegistry();
		}

		@Override
		public Localizer localizer() {
			return localizer;
		}

	}

	/*
	 * ViewProvider Implementation
	 */

	static final class UnmodifiableSkript implements Skript {

		private final Skript skript;
		private final SkriptAddon unmodifiableAddon;

		UnmodifiableSkript(Skript skript, SkriptAddon unmodifiableAddon) {
			this.skript = skript;
			this.unmodifiableAddon = unmodifiableAddon;
		}

		@Override
		public SkriptAddon registerAddon(String name) {
			throw new UnsupportedOperationException("Cannot register addons using an unmodifiable Skript");
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
		public <R extends Registry<?>> void storeRegistry(Class<R> registryClass, R registry) {
			unmodifiableAddon.storeRegistry(registryClass, registry);
		}

		@Override
		public void removeRegistry(Class<? extends Registry<?>> registryClass) {
			unmodifiableAddon.removeRegistry(registryClass);
		}

		@Override
		public boolean hasRegistry(Class<? extends Registry<?>> registryClass) {
			return unmodifiableAddon.hasRegistry(registryClass);
		}

		@Override
		public <R extends Registry<?>> R registry(Class<R> registryClass) {
			return unmodifiableAddon.registry(registryClass);
		}

		@Override
		public <R extends Registry<?>> R registry(Class<R> registryClass, Supplier<R> putIfAbsent) {
			return unmodifiableAddon.registry(registryClass, putIfAbsent);
		}

		@Override
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

	}

}
