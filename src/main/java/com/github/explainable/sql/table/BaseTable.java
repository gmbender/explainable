package com.github.explainable.sql.table;

import com.github.explainable.sql.constraint.EqualityConstraint;
import com.github.explainable.sql.type.PrimitiveType;
import com.github.explainable.util.LDVertex;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class that represents a single instance of a base table (i.e., a table that is stored by the DBMS
 * and is visible in the database schema.) Two instances of the same table can appear in the same
 * query; in this case, we will define two separate base tables. This will happen, e.g., for the
 * query {@code SELECT * FROM Table AS T1, Table AS T2}. If two columns reference the same table
 * instance, as in {@code SELECT X, Y FROM Table}, then only one BaseTable object will be created.
 */
public final class BaseTable extends Table {
	private final TypedRelation relation;

	private final String alias;

	private final NestedScope scope;

	// Lazily initialized the first time it's used; should only be accessed through columns().
	@Nullable
	private ImmutableList<BaseColumn> columns;

	@Nullable
	private LDVertex<Object, EqualityConstraint> conditionVertex;

	/**
	 * Internally, there's a cyclic dependency between {@link BaseTable} and {@link BaseColumn}: every
	 * {@link BaseTable} has a list of child columns, and every {@link BaseColumn} has a parent, which
	 * is a {@link BaseTable}. We get around the dependency while making both classes effectively
	 * immutable by using a private constructor and initializing the {@link #columns} attribute in a
	 * static factory method.
	 */
	private BaseTable(TypedRelation relation, String alias, NestedScope scope) {
		this.relation = Preconditions.checkNotNull(relation);
		this.alias = Preconditions.checkNotNull(alias);
		this.scope = Preconditions.checkNotNull(scope);

		// Postpone initializing the columns in order to avoid creating a cyclic dependency between
		// the constructors of BaseTable and {@link BaseColumn}.
		this.columns = null;

		this.conditionVertex = null;
	}

	/**
	 * Create a new {@link BaseTable} with the specified relation and alias. It no alias is specified
	 * then this method will default to using the relation name as an alias.
	 */
	public static BaseTable create(TypedRelation relation, String alias, NestedScope scope) {
		return new BaseTable(relation, alias, scope);
	}

	@Nonnull
	@Override
	public ImmutableList<BaseColumn> columns() {
		if (columns == null) {
			ImmutableList<String> fieldNames = relation.columnNames();
			ImmutableList<PrimitiveType> types = relation.type().columnTypes();

			ImmutableList.Builder<BaseColumn> columnsBuilder = ImmutableList.builder();
			for (int i = 0; i < fieldNames.size(); i++) {
				columnsBuilder.add(new BaseColumn(fieldNames.get(i), this, types.get(i)));
			}

			columns = columnsBuilder.build();
		}

		return columns;
	}

	@Override
	public String alias() {
		return alias;
	}

	public NestedScope scope() {
		return scope;
	}

	public TypedRelation relation() {
		return relation;
	}

	public LDVertex<Object, EqualityConstraint> getConditionVertex() {
		Preconditions.checkState(conditionVertex != null);
		return conditionVertex;
	}

	public void setConditionVertex(LDVertex<Object, EqualityConstraint> vertex) {
		conditionVertex = Preconditions.checkNotNull(vertex);
	}

	@Override
	public String toString() {
		return "BaseTable(" + relation + ")";
	}
}
