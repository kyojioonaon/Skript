/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package org.skriptlang.skript;

import ch.njol.util.NonNullPair;
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
	 * This implementation makes use of default implementations of required classes.
	 * Note that the default implementation is designed to prevent users from interacting directly with its addon components.
	 * That is, something like <code>skript.registry()</code> will return an unmodifiable view of the registry.
	 * As a result, this method also returns the modifiable addon backing the implementation.
	 * @param name The name for this Skript instance to use.
	 * @return A pair containing a default Skript implementation and the modifiable addon backing it.
	 */
	@Contract("_ -> new")
	static NonNullPair<Skript, SkriptAddon> createInstance(String name) {
		SkriptImpl skript = new SkriptImpl(name);
		return new NonNullPair<>(skript, skript.addon);
	}

	/**
	 * Registers the provided addon with this Skript and loads the provided modules.
	 * @param name The name of the addon to register.
	 */
	@Contract("_ -> new")
	SkriptAddon registerAddon(String name);

	/**
	 * @return An unmodifiable snapshot of addons currently registered with this Skript.
	 * It is not guaranteed that the individual addons will be modifiable (see {@link SkriptAddon#unmodifiableView(SkriptAddon)}).
	 */
	@Unmodifiable
	Collection<SkriptAddon> addons();

}
