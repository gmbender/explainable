package com.github.explainable.corelang;

import javax.annotation.Nullable;

final class Specialization extends ExtendableTermMap {
	private TermType multisetImageType;

	Specialization() {
		super();
		this.multisetImageType = TermType.NONE;
	}

	private Specialization(Specialization other) {
		super(other);
		this.multisetImageType = other.multisetImageType;
	}

	@Override
	boolean canExtend(Term source, Term target, @Nullable Term oldSource) {
		if (!source.canSpecialize(target, multisetImageType)) {
			return false;
		}

		return (oldSource == null) || target.canSpecializeFromBoth(source, oldSource);
	}

	@Override
	void didExtend(Term from, Term to) {
		if (from.type() == TermType.MULTISET_VARIABLE) {
			multisetImageType = to.type();
		}
	}

	@Override
	Specialization copy() {
		return new Specialization(this);
	}

	@Nullable
	@Override
	Specialization extend(Atom from, Atom to) {
		return (Specialization) super.extend(from, to);
	}
}
