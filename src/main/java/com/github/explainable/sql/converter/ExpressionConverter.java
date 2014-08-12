package com.github.explainable.sql.converter;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.ast.expression.*;
import com.github.explainable.sql.ast.expression.SqlBinaryExpression.BinaryOperator;
import com.github.explainable.sql.ast.expression.SqlTableComparison.SqlTableComparisonKind;
import com.github.explainable.sql.ast.expression.SqlUnaryAggregate.AggregationFunction;
import com.github.explainable.sql.ast.select.SqlSelectStmt;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.explainable.sql.converter.ConverterUtils.checkUnsupportedFeature;
import static com.github.explainable.sql.converter.ConverterUtils.checkUnsupportedFlag;

final class ExpressionConverter implements ExpressionVisitor {
	private static final long FQL_USER_UID = 4L;

	private final MasterConverter master;

	@Nullable
	private SqlExpression result;

	ExpressionConverter(MasterConverter master) {
		this.master = master;
		this.result = null;
	}

	@Nullable
	SqlExpression convert(Expression expression) {
		// This silly dance is necessary to ensure that none of the accept(...) methods forget to
		// set the "result" variable. The code would be so much cleaner if accept(...) had a non-void
		// return type. Sigh.
		result = null;
		expression.accept(this);
		Preconditions.checkNotNull(result);
		SqlExpression realResult = result;
		result = null;
		return realResult;
	}

	@Override
	public void visit(NullValue nullValue) {
		result = new SqlNull();
	}

	@Override
	public void visit(Function function) {
		if (function.isEscaped()) {
			throw new SqlException("Unsupported Feature: Escaped Functions");
		}

		List<SqlExpression> arguments = null;
		if (function.getParameters() != null) {
			arguments = Lists.newArrayList();
			for (Expression expression : function.getParameters().getExpressions()) {
				arguments.add(master.convert(expression));
			}
		}

		String functionName = function.getName().toUpperCase();
		if (functionName.equals("COUNT")) {
			result = convertCount(function, arguments);
		} else if (functionName.equals("AVG")) {
			result = convertStandardAggregate(function, arguments, AggregationFunction.AVG);
		} else if (functionName.equals("MAX")) {
			result = convertStandardAggregate(function, arguments, AggregationFunction.MAX);
		} else if (functionName.equals("MIN")) {
			result = convertStandardAggregate(function, arguments, AggregationFunction.MIN);
		} else if (functionName.equals("SUM")) {
			result = convertStandardAggregate(function, arguments, AggregationFunction.SUM);
		} else if (functionName.equals("ME")) {
			result = convertFacebookMe(function, arguments);
		} else {
			throw new SqlException("Unrecognized Function: " + function);
		}
	}

	@Override
	public void visit(InverseExpression inverseExpression) {
		result = new SqlNegate(convert(inverseExpression.getExpression()));
	}

	@Override
	public void visit(JdbcParameter jdbcParameter) {
		throw new SqlException("Unsupported Feature: JdbcParameter");
	}

	@Override
	public void visit(JdbcNamedParameter jdbcNamedParameter) {
		throw new SqlException("Unsupported Feature: JdbcNamedParameter");
	}

	@Override
	public void visit(DoubleValue doubleValue) {
		result = new SqlNumericConstant(doubleValue.getValue());
	}

	@Override
	public void visit(LongValue longValue) {
		result = new SqlNumericConstant(longValue.getValue());
	}

	@Override
	public void visit(DateValue dateValue) {
		throw new SqlException("Unsupported Constant Type: DateValue");
	}

	@Override
	public void visit(TimeValue timeValue) {
		throw new SqlException("Unsupported Constant Type: TimeValue");
	}

	@Override
	public void visit(TimestampValue timestampValue) {
		throw new SqlException("Unsupported Constant Type: TimestampValue");
	}

	@Override
	public void visit(Parenthesis parenthesis) {
		result = convert(parenthesis.getExpression());
	}

	@Override
	public void visit(StringValue stringValue) {
		result = new SqlStringConstant(stringValue.getValue());
	}

	@Override
	public void visit(Addition addition) {
		checkUnsupportedFlag(addition.isNot(), "NOT for addition");
		result = new SqlBinaryExpression(
				convert(addition.getLeftExpression()),
				convert(addition.getRightExpression()),
				BinaryOperator.ADDITION);
	}

	@Override
	public void visit(Division division) {
		checkUnsupportedFlag(division.isNot(), "NOT for division");
		result = new SqlBinaryExpression(
				convert(division.getLeftExpression()),
				convert(division.getRightExpression()),
				BinaryOperator.DIVISION);
	}

	@Override
	public void visit(Multiplication multiplication) {
		checkUnsupportedFlag(multiplication.isNot(), "NOT for multiplication");
		result = new SqlBinaryExpression(
				convert(multiplication.getLeftExpression()),
				convert(multiplication.getRightExpression()),
				BinaryOperator.MULTIPLICATION);
	}

	@Override
	public void visit(Subtraction subtraction) {
		checkUnsupportedFlag(subtraction.isNot(), "NOT for subtraction");
		result = new SqlBinaryExpression(
				convert(subtraction.getLeftExpression()),
				convert(subtraction.getRightExpression()),
				BinaryOperator.SUBTRACTION);
	}

	@Override
	public void visit(AndExpression andExpression) {
		checkUnsupportedFlag(andExpression.isNot(), "NOT AND");
		result = new SqlBinaryExpression(
				convert(andExpression.getLeftExpression()),
				convert(andExpression.getRightExpression()),
				BinaryOperator.AND);
	}

	@Override
	public void visit(OrExpression orExpression) {
		checkUnsupportedFlag(orExpression.isNot(), "NOT OR");
		result = new SqlBinaryExpression(
				convert(orExpression.getLeftExpression()),
				convert(orExpression.getRightExpression()),
				BinaryOperator.OR);
	}

	@Override
	public void visit(Between between) {
		throw new SqlException("Unsupported Feature: Between");
	}

	@Override
	public void visit(EqualsTo equalsTo) {
		checkUnsupportedFlag(equalsTo.isNot(), "NOT =");
		result = new SqlBinaryExpression(
				convert(equalsTo.getLeftExpression()),
				convert(equalsTo.getRightExpression()),
				BinaryOperator.EQUALS_TO);
	}

	@Override
	public void visit(GreaterThan greaterThan) {
		checkUnsupportedFlag(greaterThan.isNot(), "NOT >");
		result = new SqlBinaryExpression(
				convert(greaterThan.getLeftExpression()),
				convert(greaterThan.getRightExpression()),
				BinaryOperator.GREATER_THAN);
	}

	@Override
	public void visit(GreaterThanEquals greaterThanEquals) {
		checkUnsupportedFlag(greaterThanEquals.isNot(), "NOT >=");
		result = new SqlBinaryExpression(
				convert(greaterThanEquals.getLeftExpression()),
				convert(greaterThanEquals.getRightExpression()),
				BinaryOperator.GREATER_THAN_EQUALS);
	}

	@Override
	public void visit(InExpression in) {
		if (in.getOldOracleJoinSyntax() != SupportsOldOracleJoinSyntax.NO_ORACLE_JOIN) {
			throw new SqlException("Unsupported Feature: Oracle Join Syntax");
		}

		checkUnsupportedFeature(in.getLeftItemsList(), "Left items list in IN clause");

		SqlExpression left = convert(in.getLeftExpression());
		SqlExpression right = master.convert(in.getRightItemsList());
		result = new SqlIn(left, right, in.isNot());
	}

	@Override
	public void visit(IsNullExpression isNull) {
		result = new SqlIsNull(convert(isNull.getLeftExpression()), isNull.isNot());
	}

	@Override
	public void visit(LikeExpression like) {
		checkUnsupportedFeature(like.getEscape(), "LIKE ESCAPE");
		result = new SqlLike(
				convert(like.getLeftExpression()),
				convert(like.getRightExpression()),
				like.isNot());
	}

	@Override
	public void visit(MinorThan minorThan) {
		checkUnsupportedFlag(minorThan.isNot(), "NOT <");
		result = new SqlBinaryExpression(
				convert(minorThan.getLeftExpression()),
				convert(minorThan.getRightExpression()),
				BinaryOperator.SMALLER_THAN);
	}

	@Override
	public void visit(MinorThanEquals minorThanEquals) {
		checkUnsupportedFlag(minorThanEquals.isNot(), "NOT <=");
		result = new SqlBinaryExpression(
				convert(minorThanEquals.getLeftExpression()),
				convert(minorThanEquals.getRightExpression()),
				BinaryOperator.SMALLER_THAN_EQUALS);
	}

	@Override
	public void visit(NotEqualsTo notEqualsTo) {
		checkUnsupportedFlag(notEqualsTo.isNot(), "NOT <>");
		result = new SqlBinaryExpression(
				convert(notEqualsTo.getLeftExpression()),
				convert(notEqualsTo.getRightExpression()),
				BinaryOperator.NOT_EQUALS_TO);
	}

	@Override
	public void visit(Column column) {
		checkUnsupportedFeature(column.getTable().getSchemaName(), "Column Schema");
		checkUnsupportedFeature(column.getTable().getAlias(), "Column Alias in expression");
		checkUnsupportedFeature(column.getTable().getPivot(), "PIVOT");

		result = new SqlColumnReference(column.getColumnName(), column.getTable().getName());
	}

	@Override
	public void visit(SubSelect subSelect) {
		result = new SqlSubSelect(master.convert(subSelect.getSelectBody()));
	}

	@Override
	public void visit(CaseExpression caseExpr) {
		throw new SqlException("Unsupported Feature: CaseExpression");
	}

	@Override
	public void visit(WhenClause when) {
		throw new SqlException("Unsupported Feature: WhenClause");
	}

	@Override
	public void visit(ExistsExpression exists) {
		SqlExpression right = convert(exists.getRightExpression());
		if (!(right instanceof SqlSubSelect)) {
			throw new SqlException("Argument to EXISTS must be a sub-select: " + right);
		}
		result = new SqlExists((SqlSubSelect) right, exists.isNot());
	}

	@Override
	public void visit(AllComparisonExpression allComparison) {
		checkUnsupportedFeature(allComparison.getSubSelect().getAlias(), "ALIAS");
		checkUnsupportedFeature(allComparison.getSubSelect().getPivot(), "PIVOT");

		SqlSelectStmt subExpression = master.convert(
				allComparison.getSubSelect().getSelectBody());

		result = new SqlTableComparison(
				new SqlSubSelect(subExpression), SqlTableComparisonKind.ALL);
	}

	@Override
	public void visit(AnyComparisonExpression anyComparison) {
		checkUnsupportedFeature(anyComparison.getSubSelect().getAlias(), "ALIAS");
		checkUnsupportedFeature(anyComparison.getSubSelect().getPivot(), "PIVOT");

		SqlSelectStmt subExpression = master.convert(
				anyComparison.getSubSelect().getSelectBody());

		result = new SqlTableComparison(
				new SqlSubSelect(subExpression), SqlTableComparisonKind.ANY);
	}

	@Override
	public void visit(Concat concat) {
		throw new SqlException("Unsupported Feature: String Concatenation");
	}

	@Override
	public void visit(Matches matches) {
		throw new SqlException("Unsupported Feature: Matches");
	}

	@Override
	public void visit(BitwiseAnd bitwiseAnd) {
		checkUnsupportedFlag(bitwiseAnd.isNot(), "NOT &");
		result = new SqlBinaryExpression(
				convert(bitwiseAnd.getLeftExpression()),
				convert(bitwiseAnd.getRightExpression()),
				BinaryOperator.BITWISE_AND);
	}

	@Override
	public void visit(BitwiseOr bitwiseOr) {
		checkUnsupportedFlag(bitwiseOr.isNot(), "NOT |");
		result = new SqlBinaryExpression(
				convert(bitwiseOr.getLeftExpression()),
				convert(bitwiseOr.getRightExpression()),
				BinaryOperator.BITWISE_OR);
	}

	@Override
	public void visit(BitwiseXor bitwiseXor) {
		checkUnsupportedFlag(bitwiseXor.isNot(), "NOT ^");
		result = new SqlBinaryExpression(
				convert(bitwiseXor.getLeftExpression()),
				convert(bitwiseXor.getRightExpression()),
				BinaryOperator.BITWISE_XOR);
	}

	@Override
	public void visit(CastExpression castExpression) {
		throw new SqlException("Unsupported Feature: CastExpression");
	}

	@Override
	public void visit(Modulo modulo) {
		checkUnsupportedFlag(modulo.isNot(), "NOT %");
		result = new SqlBinaryExpression(
				convert(modulo.getLeftExpression()),
				convert(modulo.getRightExpression()),
				BinaryOperator.MODULO);
	}

	@Override
	public void visit(AnalyticExpression analytic) {
		throw new SqlException("Unsupported Feature: AnalyticExpression");
	}

	@Override
	public void visit(ExtractExpression extract) {
		throw new SqlException("Unsupported Feature: ExtractExpression");
	}

	@Override
	public void visit(IntervalExpression interval) {
		throw new SqlException("Unsupported Feature; IntervalExpression");
	}

	private SqlExpression convertCount(Function function, @Nullable List<SqlExpression> args) {
		if (args == null) {
			if (function.isDistinct()) {
				throw new SqlException("Unsupported Feature: COUNT(DISTINCT *)");
			}

			return new SqlCountAll();
		} else {
			if (args.size() != 1) {
				throw new SqlException("COUNT takes exactly one argument");
			}

			return new SqlUnaryAggregate(args.get(0), AggregationFunction.COUNT,
					function.isDistinct());
		}
	}

	private SqlExpression convertFacebookMe(Function function, @Nullable List<SqlExpression> args) {
		// TODO: Avoid adding special cases for FQL.
		if (args != null) {
			throw new SqlException(function.getName() + " does not take any arguments");
		}

		return new SqlNumericConstant(FQL_USER_UID);
	}

	private SqlExpression convertStandardAggregate(
			Function function,
			@Nullable List<SqlExpression> args,
			AggregationFunction type) {
		if (args == null || args.size() != 1) {
			throw new SqlException(function.getName() + " takes exactly one argument");
		}

		return new SqlUnaryAggregate(args.get(0), type, function.isDistinct());
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("result", result)
				.toString();
	}
}
