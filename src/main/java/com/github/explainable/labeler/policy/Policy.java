package com.github.explainable.labeler.policy;

import com.github.explainable.corelang.View;
import com.google.common.base.Preconditions;

import java.util.Set;

/**
 * Class representing a permission expression in the policy semiring.
 */
public abstract class Policy {
	private Policy() {
	}

	public abstract boolean evaluate(Set<View> granted);

	public abstract Policy whySo(Set<View> granted);

	public abstract Policy whyNot(Set<View> granted);

	public Policy or(Policy right) {
		return new Disjunction(this, right);
	}

	public Policy and(Policy right) {
		return new Conjunction(this, right);
	}

	// TODO: Unit test this method
	public abstract Policy simplify();

	public abstract <T> T match(PolicyMatcher<T> matcher);

	public static final Policy FALSE = new Policy() {
		@Override
		public boolean evaluate(Set<View> granted) {
			return false;
		}

		@Override
		public Policy whySo(Set<View> granted) {
			return this;
		}

		@Override
		public Policy whyNot(Set<View> granted) {
			return this;
		}

		@Override
		public Policy simplify() {
			return this;
		}

		@Override
		public <T> T match(PolicyMatcher<T> matcher) {
			return matcher.matchFalse();
		}

		@Override
		public String toString() {
			return "false";
		}
	};

	public static final Policy TRUE = new Policy() {
		@Override
		public boolean evaluate(Set<View> granted) {
			return true;
		}

		@Override
		public Policy whySo(Set<View> granted) {
			return this;
		}

		@Override
		public Policy whyNot(Set<View> granted) {
			return this;
		}

		@Override
		public Policy simplify() {
			return this;
		}

		@Override
		public <T> T match(PolicyMatcher<T> matcher) {
			return matcher.matchTrue();
		}

		@Override
		public String toString() {
			return "true";
		}
	};

	private static final class Indicator extends Policy {
		private final View value;

		Indicator(View value) {
			this.value = Preconditions.checkNotNull(value);
		}

		@Override
		public boolean evaluate(Set<View> granted) {
			return granted.contains(value);
		}

		@Override
		public Policy whySo(Set<View> granted) {
			return granted.contains(value) ? this : FALSE;
		}

		@Override
		public Policy whyNot(Set<View> granted) {
			return granted.contains(value) ? TRUE : this;
		}

		@Override
		public Policy simplify() {
			return this;
		}

		@Override
		public <T> T match(PolicyMatcher<T> matcher) {
			return matcher.matchView(value);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof Indicator)) {
				return false;
			}
			Indicator other = (Indicator) obj;
			return value.equals(other.value);
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public String toString() {
			return value.name();
		}
	}

	public static Policy of(View value) {
		return new Indicator(value);
	}

	private static final class Disjunction extends Policy {
		private final Policy left;

		private final Policy right;

		Disjunction(Policy left, Policy right) {
			this.left = Preconditions.checkNotNull(left);
			this.right = Preconditions.checkNotNull(right);
		}

		@Override
		public boolean evaluate(Set<View> granted) {
			return left.evaluate(granted) || right.evaluate(granted);
		}

		@Override
		public Policy whySo(Set<View> granted) {
			return left.whySo(granted).or(right.whySo(granted));
		}

		@Override
		public Policy whyNot(Set<View> granted) {
			return left.whyNot(granted).or(right.whyNot(granted));
		}

		@Override
		public Policy simplify() {
			Policy newLeft = left.simplify();
			Policy newRight = right.simplify();

			if (newLeft.equals(TRUE)) {
				return newLeft;
			}

			if (newRight.equals(TRUE)) {
				return newRight;
			}

			if (newLeft.equals(FALSE)) {
				return newRight;
			}

			if (newRight.equals(FALSE)) {
				return newLeft;
			}

			return newLeft.or(newRight);
		}

		@Override
		public <T> T match(PolicyMatcher<T> matcher) {
			return matcher.matchOr(left, right);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof Disjunction)) {
				return false;
			}
			Disjunction other = (Disjunction) obj;
			return left.equals(other.left) && right.equals(other.right);
		}

		@Override
		public int hashCode() {
			// Return a randomly generated constant plus the hash codes of the child nodes
			return -143983845 + 17 * left.hashCode() + 17 * 17 * right.hashCode();
		}

		@Override
		public String toString() {
			String leftString = (left instanceof Conjunction)
					? "(" + left.toString() + ")"
					: left.toString();

			String rightString = (right instanceof Conjunction)
					? "(" + right.toString() + ")"
					: right.toString();

			return leftString + " or " + rightString;
		}
	}

	private static final class Conjunction extends Policy {
		private final Policy left;

		private final Policy right;

		Conjunction(Policy left, Policy right) {
			this.left = Preconditions.checkNotNull(left);
			this.right = Preconditions.checkNotNull(right);
		}

		@Override
		public Policy whySo(Set<View> granted) {
			return left.whySo(granted).and(right.whySo(granted));
		}

		@Override
		public Policy whyNot(Set<View> granted) {
			return left.whyNot(granted).and(right.whyNot(granted));
		}

		@Override
		public boolean evaluate(Set<View> granted) {
			return left.evaluate(granted) && right.evaluate(granted);
		}

		@Override
		public Policy simplify() {
			Policy newLeft = left.simplify();
			Policy newRight = right.simplify();

			if (newLeft.equals(FALSE)) {
				return newLeft;
			}

			if (newRight.equals(FALSE)) {
				return newRight;
			}

			if (newLeft.equals(TRUE)) {
				return newRight;
			}

			if (newRight.equals(TRUE)) {
				return newLeft;
			}

			return newLeft.and(newRight);
		}

		@Override
		public <T> T match(PolicyMatcher<T> matcher) {
			return matcher.matchAnd(left, right);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof Conjunction)) {
				return false;
			}
			Conjunction other = (Conjunction) obj;
			return left.equals(other.left) && right.equals(other.right);
		}

		@Override
		public int hashCode() {
			// Return a randomly generated constant plus the hash codes of the child nodes
			return 1368035308 + 17 * left.hashCode() + 17 * 17 * right.hashCode();
		}

		@Override
		public String toString() {
			String leftString = (left instanceof Disjunction)
					? "(" + left.toString() + ")"
					: left.toString();

			String rightString = (right instanceof Disjunction)
					? "(" + right.toString() + ")"
					: right.toString();

			return leftString + " and " + rightString;
		}
	}
}
