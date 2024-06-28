package org.skriptlang.skript.addon;

import org.skriptlang.skript.localization.Localizer;
import org.skriptlang.skript.registration.SyntaxRegistry;

class SkriptAddonImpl {

	static class UnmodifiableAddon implements SkriptAddon {

		private final SkriptAddon addon;
		private final SyntaxRegistry unmodifiableRegistry;
		private final Localizer unmodifiableLocalizer;

		UnmodifiableAddon(SkriptAddon addon) {
			this.addon = addon;
			this.unmodifiableRegistry = SyntaxRegistry.unmodifiableView(addon.syntaxRegistry());
			this.unmodifiableLocalizer = Localizer.unmodifiableView(addon.localizer());
		}

		@Override
		public String name() {
			return addon.name();
		}

		@Override
		public SyntaxRegistry syntaxRegistry() {
			return unmodifiableRegistry;
		}

		@Override
		public Localizer localizer() {
			return unmodifiableLocalizer;
		}

		@Override
		public void loadModules(AddonModule... modules) {
			throw new UnsupportedOperationException("Modules cannot be loaded with an unmodifiable addon.");
		}

	}

}
