package com.github.explainable.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An implementation of {@link ProperStack} that uses an {@link java.util.ArrayList} to store the
 * stack's contents.
 */
public final class ProperArrayListStack<T> implements ProperStack<T> {
	private final List<T> elements;

	private ProperArrayListStack() {
		this.elements = Lists.newArrayList();
	}

	public static <T> ProperArrayListStack<T> create() {
		return new ProperArrayListStack<T>();
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ProperArrayListStack)) {
			return false;
		}

		ProperArrayListStack<?> other = (ProperArrayListStack<?>) o;

		if (!elements.equals(other.elements)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public T peek() {
		if (elements.isEmpty()) {
			throw new EmptyStackException();
		}

		return elements.get(elements.size() - 1);
	}

	@Override
	public T pop() {
		if (elements.isEmpty()) {
			throw new EmptyStackException();
		}

		return elements.remove(elements.size() - 1);
	}

	@Override
	public ProperArrayListStack<T> push(T element) {
		elements.add(element);
		return this;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int lastIndex = elements.size();

			@Override
			public boolean hasNext() {
				return lastIndex > 0;
			}

			@Override
			public T next() {
				if (lastIndex == 0) {
					throw new NoSuchElementException();
				}

				lastIndex--;
				return elements.get(lastIndex);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public String toString() {
		return "Stack[" + Joiner.on(", ").join(elements) + "]";
	}
}
