package com.github.explainable.corelang;

import javax.annotation.Nullable;

final class Homomorphism extends ExtendableTermMap {
	Homomorphism() {
		super();
	}

	private Homomorphism(Homomorphism original) {
		super(original);
	}

	@Override
	boolean canExtend(Term source, Term target, @Nullable Term oldSource) {
		if (!source.canFold(target)) {
			return false;
		}

		return (oldSource == null) || target.canFoldFromBoth(source, oldSource);
	}

	@Override
	void didExtend(Term from, Term to) {
		// Do nothing.
	}

	@Override
	Homomorphism copy() {
		return new Homomorphism(this);
	}

	/**
	 * Determine whether there is a homomorphism from one set of atoms to another. There is one
	 * technical difference between the homomorphisms used in this method and the formal definition
	 * provided by Cohen: we do not require every distinguished variable in {@code other} to appear in
	 * the homomorphism's image. This means that a homomorphism can be extend multiple times with
	 * different atoms.
	 */
	@Nullable
	@Override
	Homomorphism extend(Atom from, Atom to) {
		return (Homomorphism) super.extend(from, to);
	}
}
