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
package org.skriptlang.skript.registry;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SyntaxElement;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;
import java.util.stream.Collectors;

class SyntaxInfoImpl<T extends SyntaxElement> implements SyntaxInfo<T> {
	
	private final SyntaxOrigin origin;
	private final Class<T> type;
	private final List<String> patterns;
	
	SyntaxInfoImpl(SyntaxOrigin origin, Class<T> type, List<String> patterns) {
		this.origin = origin;
		this.type = type;
		this.patterns = ImmutableList.copyOf(patterns);
	}
	
	@Override
	public SyntaxOrigin origin() {
		return origin;
	}
	
	@Override
	public Class<T> type() {
		return type;
	}
	
	@Override
	@Unmodifiable
	public List<String> patterns() {
		return patterns;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof SyntaxInfo))
			return false;
		SyntaxInfo<?> info = (SyntaxInfo<?>) other;
		return origin().equals(info.origin()) && type().equals(info.type()) &&
			patterns().equals(info.patterns());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(origin(), type(), patterns());
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("origin", origin())
			.add("type", type())
			.add("patterns", patterns())
			.toString();
	}
	
	static final class EventImpl<E extends SkriptEvent> extends StructureImpl<E> implements DefaultSyntaxInfos.Event<E> {
		
		private final String name;
		private final List<Class<? extends org.bukkit.event.Event>> events;
		
		EventImpl(
			SyntaxOrigin origin, Class<E> type, List<String> patterns,
			String name, List<Class<? extends org.bukkit.event.Event>> events
		) {
			super(origin, type, patterns.stream().map(EventImpl::pattern)
					.collect(Collectors.toList()), null);
			this.name = name;
			this.events = ImmutableList.copyOf(events);
		}
		
		@Override
		public String name() {
			return name;
		}
		
		@Override
		public List<Class<? extends org.bukkit.event.Event>> events() {
			return events;
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (!(other instanceof Event))
				return false;
			Event<?> event = (Event<?>) other;
			return origin().equals(event.origin()) && type().equals(event.type()) &&
				patterns().equals(event.patterns()) && name().equals(event.name());
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(origin(), type(), patterns(), name(), events());
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
				.add("origin", origin())
				.add("type", type())
				.add("patterns", patterns())
				.add("name", name())
				.add("events", events())
				.toString();
		}
		
		private static String pattern(String pattern) {
			return "[on] " + SkriptEvent.fixPattern(pattern) + " [with priority (lowest|low|normal|high|highest|monitor)]";
		}
		
	}
	
	static final class ExpressionImpl<E extends ch.njol.skript.lang.Expression<R>, R>
		extends SyntaxInfoImpl<E> implements DefaultSyntaxInfos.Expression<E, R> {
		
		private final Class<R> returnType;
		private final ExpressionType expressionType;
		
		ExpressionImpl(
			SyntaxOrigin origin, Class<E> type, List<String> patterns,
			Class<R> returnType, ExpressionType expressionType
		) {
			super(origin, type, patterns);
			if (returnType.isAnnotation() || returnType.isArray() || returnType.isPrimitive())
				throw new IllegalArgumentException("returnType must be a normal type");
			this.returnType = returnType;
			this.expressionType = expressionType;
		}
		
		@Override
		public Class<R> returnType() {
			return returnType;
		}
		
		@Override
		public ExpressionType expressionType() {
			return expressionType;
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (!(other instanceof Expression))
				return false;
			ExpressionImpl<?, ?> expression = (ExpressionImpl<?, ?>) other;
			return origin().equals(expression.origin()) && type().equals(expression.type()) &&
					patterns().equals(expression.patterns()) && returnType() == expression.returnType() &&
					expressionType().equals(expression.expressionType());
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(origin(), type(), patterns(), returnType(), expressionType());
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
				.add("origin", origin())
				.add("type", type())
				.add("patterns", patterns())
				.add("returnType", returnType())
				.add("expressionType", expressionType())
				.toString();
		}
		
	}
	
	static class StructureImpl<E extends org.skriptlang.skript.lang.structure.Structure>
		extends SyntaxInfoImpl<E> implements DefaultSyntaxInfos.Structure<E> {
		
		@Nullable
		private final EntryValidator entryValidator;
		
		StructureImpl(
			SyntaxOrigin origin, Class<E> type, List<String> patterns,
			@Nullable EntryValidator entryValidator
		) {
			super(origin, type, patterns);
			this.entryValidator = entryValidator;
		}
		
		@Override
		@Nullable
		public EntryValidator entryValidator() {
			return entryValidator;
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (!(other instanceof Structure))
				return false;
			Structure<?> structure = (Structure<?>) other;
			return origin().equals(structure.origin()) && type().equals(structure.type()) &&
					patterns().equals(structure.patterns()) &&
					Objects.equal(entryValidator(), structure.entryValidator());
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(origin(), type(), patterns(), entryValidator());
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
				.add("origin", origin())
				.add("type", type())
				.add("patterns", patterns())
				.add("entryValidator", entryValidator())
				.toString();
		}
		
	}
	
}
