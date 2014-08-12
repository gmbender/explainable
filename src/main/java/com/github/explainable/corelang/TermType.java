package com.github.explainable.corelang;

public enum TermType {
	NONE {
		@Override
		boolean isExistential() {
			throw new UnsupportedOperationException("NONE.isExistential()");
		}
	},
	DIST_VARIABLE {
		@Override
		boolean isExistential() {
			return false;
		}
	},
	MULTISET_VARIABLE {
		@Override
		boolean isExistential() {
			return true;
		}
	},
	SET_VARIABLE {
		@Override
		boolean isExistential() {
			return true;
		}
	},
	CONSTANT {
		@Override
		boolean isExistential() {
			return false;
		}
	};

	abstract boolean isExistential();
}
