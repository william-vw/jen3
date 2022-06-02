package org.apache.jen3.reasoner.rulesys;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.apache.jen3.graph.Node;
import org.nevec.rjm.BigDecimalMath;

public class MathOp {

	public enum MathOperations {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, REMAINDER, EXPONENTIATION, LOGARITHM,

		NEGATION, ABSOLUTE_VALUE, ROUNDED, CEILING, FLOOR;

		public int getMaxPrecision(List<Node> nodes) {
			int prec = 0;
			switch (this) {

			case REMAINDER:
				return 2;

			case LOGARITHM:
			case DIVIDE:
				prec = Util.getMaxPrecision(nodes);
				if (prec < 4)
					return 4;
				else
					return prec;

			default:
				break;
			}

			return Util.getMaxPrecision(nodes);
		}

		public int getPrecision(Node node) {
			return Util.getPrecision(node);
		}

		public int getNoOpValue() {
			switch (this) {

			case ADD:
				return 0;

			case SUBTRACT:
				return 0;

			case MULTIPLY:
				return 1;

			case DIVIDE:
				return 1;

			default:
				return 0;
			}
		}

		public MathOperations inverse() {
			switch (this) {

			case ADD:
				return SUBTRACT;

			case SUBTRACT:
				return ADD;

			case MULTIPLY:
				return DIVIDE;

			case DIVIDE:
				return MULTIPLY;

			default:
				return null;
			}
		}
	}

	public static class ByteOp {

		public static byte apply(MathOperations op, byte v) {
			switch (op) {
			case NEGATION:
				return (byte) -v;
			case ABSOLUTE_VALUE:
				return (byte) Math.abs(v);
			case ROUNDED:
			case CEILING:
			case FLOOR:
				return v;
			default:
				return v;
			}
		}

		public static byte apply(MathOperations op, byte v1, byte v2) {
			switch (op) {
			case ADD:
				return (byte) (v1 + v2);
			case SUBTRACT:
				return (byte) (v1 - v2);
			case MULTIPLY:
				return (byte) (v1 * v2);
			case DIVIDE:
				return (byte) (v1 / v2);
			case REMAINDER:
				return (byte) (v1 % v2);
			case EXPONENTIATION:
				return (byte) Math.pow(v1, v2);
			case LOGARITHM:
				// courtesy
				// https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
				return (byte) (Math.log(v1) / Math.log(v2) + 1e-10);
			default:
				return v1;
			}
		}
	}

	public static class ShortOp {

		public static short apply(MathOperations op, short v) {
			switch (op) {
			case NEGATION:
				return (short) -v;
			case ABSOLUTE_VALUE:
				return (short) Math.abs(v);
			case ROUNDED:
			case CEILING:
			case FLOOR:
				return v;
			default:
				return v;
			}
		}

		public static short apply(MathOperations op, short v1, short v2) {
			switch (op) {
			case ADD:
				return (short) (v1 + v2);
			case SUBTRACT:
				return (short) (v1 - v2);
			case MULTIPLY:
				return (short) (v1 * v2);
			case DIVIDE:
				return (short) (v1 / v2);
			case REMAINDER:
				return (short) (v1 % v2);
			case EXPONENTIATION:
				return (short) Math.pow(v1, v2);
			case LOGARITHM:
				return (short) (Math.log(v1) / Math.log(v2) + 1e-10);
			default:
				return v1;
			}
		}
	}

	public static class IntOp {

		public static int apply(MathOperations op, int v) {
			switch (op) {
			case NEGATION:
				return -v;
			case ABSOLUTE_VALUE:
				return Math.abs(v);
			case ROUNDED:
			case CEILING:
			case FLOOR:
				return v;
			default:
				return v;
			}
		}

		public static int apply(MathOperations op, int v1, int v2) {
			switch (op) {
			case ADD:
				return v1 + v2;
			case SUBTRACT:
				return v1 - v2;
			case MULTIPLY:
				return v1 * v2;
			case DIVIDE:
				return v1 / v2;
			case REMAINDER:
				return v1 % v2;
			case EXPONENTIATION:
				return (int) Math.pow(v1, v2);
			case LOGARITHM:
				return (int) (Math.log(v1) / Math.log(v2) + 1e-10);
			default:
				return v1;
			}
		}
	}

	public static class LongOp {

		public static long apply(MathOperations op, long v) {
			switch (op) {
			case NEGATION:
				return -v;
			case ABSOLUTE_VALUE:
			case ROUNDED:
			case CEILING:
			case FLOOR:
				return v;
			default:
				return v;
			}
		}

		public static long apply(MathOperations op, long v1, long v2) {
			switch (op) {
			case ADD:
				return v1 + v2;
			case SUBTRACT:
				return v1 - v2;
			case MULTIPLY:
				return v1 * v2;
			case DIVIDE:
				return v1 / v2;
			case REMAINDER:
				return v1 % v2;
			case EXPONENTIATION:
				return (long) Math.pow(v1, v2);
			case LOGARITHM:
				return (long) (Math.log(v1) / Math.log(v2) + 1e-10);
			default:
				return v1;
			}
		}
	}

	public static class FloatOp {

		public static float apply(MathOperations op, float v) {
			switch (op) {
			case NEGATION:
				return -v;
			case ABSOLUTE_VALUE:
				return Math.abs(v);
			case ROUNDED:
				return Math.round(v);
			case CEILING:
				return (float) Math.ceil(v);
			case FLOOR:
				return (float) Math.floor(v);
			default:
				return v;
			}
		}

		public static float apply(MathOperations op, float v1, float v2) {
			switch (op) {
			case ADD:
				return v1 + v2;
			case SUBTRACT:
				return v1 - v2;
			case MULTIPLY:
				return v1 * v2;
			case DIVIDE:
				return v1 / v2;
			case ROUNDED:
				float factor = (float) Math.pow(10, (int) v2);
				return Math.round(v1 * factor) / factor;
			case REMAINDER:
				return v1 % v2;
			case EXPONENTIATION:
				return (float) Math.pow(v1, v2);
			case LOGARITHM:
				return (float) (Math.log(v1) / Math.log(v2) + 1e-10);
			default:
				return v1;
			}
		}
	}

	public static class DoubleOp {

		public static double apply(MathOperations op, double v) {
			switch (op) {
			case NEGATION:
				return -v;
			case ABSOLUTE_VALUE:
				return Math.abs(v);
			case ROUNDED:
				return Math.round(v);
			case CEILING:
				return Math.ceil(v);
			case FLOOR:
				return Math.floor(v);
			default:
				return v;
			}
		}

		public static double apply(MathOperations op, double v1, double v2) {
			switch (op) {
			case ADD:
				return v1 + v2;
			case SUBTRACT:
				return v1 - v2;
			case MULTIPLY:
				return v1 * v2;
			case DIVIDE:
				return v1 / v2;
			case ROUNDED:
				double factor = Math.pow(10, (int) v2);
				return Math.round(v1 * factor) / factor;
			case REMAINDER:
				return v1 % v2;
			case EXPONENTIATION:
				return Math.pow(v1, v2);
			case LOGARITHM:
				return (Math.log(v1) / Math.log(v2) + 1e-10);
			default:
				return v1;
			}
		}
	}

	public static class BigDecimalOp {

		public static BigDecimal apply(MathOperations op, BigDecimal v) {
			switch (op) {
			case NEGATION:
				return v.negate();
			case ABSOLUTE_VALUE:
				return v.abs();
			case ROUNDED:
				if (v.signum() == -1)
					return v.setScale(0, RoundingMode.HALF_DOWN);
				else
					return v.setScale(0, RoundingMode.HALF_UP);
				// round doesn't seem to work for numbers 0<n<1 and 0>n>-1
				// (https://docs.oracle.com/javase/7/docs/api/java/math/RoundingMode.html)
			case CEILING:
				return v.setScale(0, RoundingMode.CEILING);
			case FLOOR:
				return v.setScale(0, RoundingMode.FLOOR);
			default:
				return v;
			}
		}

		public static BigDecimal apply(MathOperations op, BigDecimal v1, BigDecimal v2) {
			MathContext ctx = MathContext.DECIMAL64;

			switch (op) {
			case ADD:
				return v1.add(v2, ctx);
			case SUBTRACT:
				return v1.subtract(v2, ctx);
			case MULTIPLY:
				return v1.multiply(v2, ctx);
			case DIVIDE:
				// needed to avoid
				// non-terminating decimal expansion error
				return v1.divide(v2, ctx);
			case ROUNDED:
				return v1.round(new MathContext(v2.intValue() + 1, RoundingMode.HALF_UP));
			case REMAINDER:
				return v1.remainder(v2);
			case EXPONENTIATION:
				return BigDecimalMath.pow(v1, v2).round(ctx);
			case LOGARITHM:
				return new BigDecimal(Math.log(v1.doubleValue()) / Math.log(v2.doubleValue()) + 1e-10).round(ctx);
			default:
				return v1;
			}
		}
	}
}
