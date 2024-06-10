package org.skriptlang.skript.bukkit.particles.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Patterns;
import ch.njol.util.Kleenean;
import ch.njol.util.VectorMath;
import org.bukkit.Effect;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.particles.GameEffect;

public class ExprGameEffect extends SimpleExpression<GameEffect> {

	private static final Patterns<Effect> PATTERNS = new Patterns<>(new Object[][]{
		{"[record] song (of|using) %item%", Effect.RECORD_PLAY}, // shows the action bar too!
		{"[dispenser] [black|1:white] smoke effect [(in|with|using) direction] %direction/vector%", Effect.SMOKE},
		{"[foot]step sound [effect] (on|of|using) %item/blockdata%", Effect.STEP_SOUND},
		{"[:instant] [splash] potion break effect (with|of|using) [colour] %color%", Effect.POTION_BREAK}, // paper changes this type
		{"composter fill[ing] (succe[ss|ed]|1:fail[ure]) sound [effect]", Effect.COMPOSTER_FILL_ATTEMPT},
		{"villager plant grow[th] effect [(with|using) %number% particles]", Effect.VILLAGER_PLANT_GROW},
		{"[fake] bone meal effect [(with|using) %number% particles]", Effect.BONE_MEAL_USE},
		// post copper update (1.19?)
		{"(electric|lightning[ rod]|copper) spark effect [(in|using) the (1:x|2:y|3:z) axis]", Effect.ELECTRIC_SPARK},
		// paper only
		{"sculk (charge|spread) effect [(with|using) data %number%]", Effect.PARTICLES_SCULK_CHARGE}, // data explanation here https://discord.com/channels/135877399391764480/836220422223036467/1211040434852208660
		{"[finish] brush[ing] effect (with|using) %item/blockdata%", Effect.PARTICLES_AND_SOUND_BRUSH_BLOCK_COMPLETE},
		// 1.20.3
		{"trial spawner detect[ing|s] [%number%] player[s] effect", Effect.TRIAL_SPAWNER_DETECT_PLAYER}
	});

	static {
		// TODO: register the rest via the type parser
		// 	make sure to not parse the ones with data, so this class can handle it
		Skript.registerExpression(ExprGameEffect.class, GameEffect.class, ExpressionType.COMBINED, PATTERNS.getPatterns());
	}

	private GameEffect gameEffect;
	private Expression<?> data;
	private int variant;


	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		gameEffect = new GameEffect(PATTERNS.getInfo(matchedPattern));
		variant = parseResult.mark;
		if (expressions.length > 0)
			data = expressions[0];
		return true;
	}

	@Override
	protected GameEffect @Nullable [] get(Event event) {
		switch (gameEffect.getEffect()) {
			case SMOKE:
				return handleSmokeEffects(gameEffect, variant, data.getSingle(event));
			default:
				return setData(gameEffect, data.getSingle(event));
		}
	}

	private GameEffect @Nullable [] handleSmokeEffects(GameEffect gameEffect, int variant, Object data) {
		BlockFace blockFace;
		if (data instanceof Vector) {
			Vector vector = ((Vector) data);
			if (vector.isZero())
				return new GameEffect[0];
			blockFace = VectorMath.toNearestBlockFace(vector.normalize());
		} else if (data instanceof Direction) {
			Vector vector = ((Direction) data).getDirection();
			// what about relative directions
		}
		if (variant == 1)
			gameEffect = new GameEffect(Effect.SHOOT_WHITE_SMOKE);
		return setData(gameEffect, data);
	}

	private GameEffect @Nullable [] setData(GameEffect gameEffect, Object data){
		if (data == null)
			return new GameEffect[0]; // invalid data, must return nothing.
		boolean success = gameEffect.setData(data);
		if (!success)
			return new GameEffect[0]; // invalid data
		return new GameEffect[]{gameEffect};
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends GameEffect> getReturnType() {
		return null;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return null;
	}
}
