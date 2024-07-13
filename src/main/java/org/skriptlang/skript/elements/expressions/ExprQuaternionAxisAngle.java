package org.skriptlang.skript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

@Name("Quaternion Axis/Angle of Rotation")
@Description({
	"Returns the axis or angle that a quaternion will rotate with.",
	"All quaternions can be represented by a rotation of some amount around some axis, so this expression provides " +
	"the ability to get that angle/axis."
})
@Examples({
	"set {_quaternion} to axisAngle(45, vector(1, 2, 3))",
	"send axis of {_quaternion} # 1, 2, 3",
	"send angle of {_quaternion} # 45"
})
@Since("INSERT VERSION")
public class ExprQuaternionAxisAngle extends SimplePropertyExpression<Quaternionf, Object> {

	static {
		if (Skript.isRunningMinecraft(1, 19, 4))
			register(ExprQuaternionAxisAngle.class, Object.class, "[rotation] (angle|:axis)", "quaternions");
	}

	boolean isAxis;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		isAxis = parseResult.hasTag("axis");
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public @Nullable Object convert(Quaternionf from) {
		AxisAngle4f axisAngle = new AxisAngle4f(from);
		if (isAxis)
			return new Vector(axisAngle.x, axisAngle.y, axisAngle.z);
		return (float) (axisAngle.angle * 180 / Math.PI);
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		switch (mode) {
			case ADD, SET, REMOVE ->  {
				if (Changer.ChangerUtils.acceptsChange(getExpr(), ChangeMode.SET, Quaternionf.class))
					return CollectionUtils.array(isAxis ? Vector.class : Number.class);
				return null;
			}
		}
		return null;
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		assert delta != null; // reset/delete not supported
		Quaternionf[] quaternions = getExpr().getArray(event);
		AxisAngle4f axisAngle = new AxisAngle4f();
		if (isAxis && delta[0] instanceof Vector vector) {
			for (Quaternionf quaternion : quaternions) {
		 		axisAngle.set(quaternion);
				axisAngle.set(axisAngle.angle, (float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
				quaternion.set(axisAngle);
			}
		} else if (delta[0] instanceof Number number) {
			float angle = (float) (number.floatValue() / 180 * Math.PI);
			for (Quaternionf quaternion : quaternions) {
				axisAngle.set(quaternion);
				axisAngle.set(angle, axisAngle.x, axisAngle.y, axisAngle.z);
				quaternion.set(axisAngle);
			}
		}
		getExpr().change(event, quaternions, ChangeMode.SET);
	}

	@Override
	protected String getPropertyName() {
		return isAxis ? "axis" : "angle";
	}

	@Override
	public Class<?> getReturnType() {
		return isAxis ? Vector.class : Float.class;
	}

}
