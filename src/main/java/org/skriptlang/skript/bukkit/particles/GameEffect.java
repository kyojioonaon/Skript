package org.skriptlang.skript.bukkit.particles;

import ch.njol.skript.util.EnumUtils;
import org.bukkit.Effect;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

/**
 * A class to hold metadata about {@link org.bukkit.Effect}s before playing.
 */
public class GameEffect {

	public static final EnumUtils<Effect> ENUM_UTILS = new EnumUtils<>(Effect.class, "game effect");

	/**
	 * The {@link Effect} that this object represents
	 */
	private final Effect effect;

	/**
	 * The optional extra data that some {@link Effect}s require.
	 */
	@Nullable
	private Object data;

	public GameEffect(Effect effect) {
		this.effect = effect;
	}

	public static GameEffect parse(String input) {
		Effect effect = ENUM_UTILS.parse(input.toLowerCase(Locale.ENGLISH));
		if (effect == null || effect.getData() != null) {
			return null;
		}
		return new GameEffect(effect);
	}

	public Effect getEffect() {
		return effect;
	}

	@Nullable
	public Object getData() {
		return data;
	}

	public boolean setData(Object data) {
		if (effect.getData() != null && effect.getData().isInstance(data)) {
			this.data = data;
			return true;
		}
		return false;
	}

	public String toString(int flags) {
		if (effect.getData() != null)
			return ENUM_UTILS.toString(getEffect(), flags);
		return toString();
	}

	static final String[] namesWithoutData = (String[]) Arrays.stream(Effect.values())
			.filter(effect -> effect.getData() == null)
			.map(Enum::name)
			.toArray();
	public static String[] getAllNamesWithoutData(){
		return namesWithoutData.clone();
	}


// TODO: add getters, setters, maybe builder class? Add spawn method.
}
