/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.ConvergingAlgorithmImpl;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import myprinter.FieldPrinter;

/**
 * Provide a default implementation for several functions useful to generic
 * solvers.
 *
 * @version $Revision$ $Date$
 */
public abstract class UnivariateRealSolverImpl
    extends ConvergingAlgorithmImpl implements UnivariateRealSolver {

    public static Map oref_map = new HashMap();

	public static int eid_7au3e = 0;

	public static void addToORefMap(String msig, Object obj) {
		List l = (List) oref_map.get(msig);
		if (l == null) {
			l = new ArrayList();
			oref_map.put(msig, l);
		}
		l.add(obj);
	}

	public static void clearORefMap() {
		oref_map.clear();
		eid_7au3e = 0;
	}

	/** Maximum error of function. */
    protected double functionValueAccuracy;

    /** Default maximum error of function. */
    protected double defaultFunctionValueAccuracy;

    /** Indicates where a root has been computed. */
    protected boolean resultComputed = false;

    /** The last computed root. */
    protected double result;

    /** Value of the function at the last computed result. */
    protected double functionValue;

    /** The function to solve.
     * @deprecated as of 2.0 the function to solve is passed as an argument
     * to the {@link #solve(UnivariateRealFunction, double, double)} or
     * {@link UnivariateRealSolverImpl#solve(UnivariateRealFunction, double, double, double)}
     * method. */
    @Deprecated
    protected UnivariateRealFunction f;

    /**
     * Construct a solver with given iteration count and accuracy.
     *
     * @param f the function to solve.
     * @param defaultAbsoluteAccuracy maximum absolute error
     * @param defaultMaximalIterationCount maximum number of iterations
     * @throws IllegalArgumentException if f is null or the
     * defaultAbsoluteAccuracy is not valid
     * @deprecated as of 2.0 the function to solve is passed as an argument
     * to the {@link #solve(UnivariateRealFunction, double, double)} or
     * {@link UnivariateRealSolverImpl#solve(UnivariateRealFunction, double, double, double)}
     * method.
     */
    @Deprecated
    protected UnivariateRealSolverImpl(final UnivariateRealFunction f,
                                       final int defaultMaximalIterationCount,
                                       final double defaultAbsoluteAccuracy) {
        super(defaultMaximalIterationCount, defaultAbsoluteAccuracy);
        if (f == null) {
            throw MathRuntimeException.createIllegalArgumentException("function to solve cannot be null");
        }
        this.f = f;
        this.defaultFunctionValueAccuracy = 1.0e-15;
        this.functionValueAccuracy = defaultFunctionValueAccuracy;
    }

    /**
     * Construct a solver with given iteration count and accuracy.
     *
     * @param defaultAbsoluteAccuracy maximum absolute error
     * @param defaultMaximalIterationCount maximum number of iterations
     * @throws IllegalArgumentException if f is null or the
     * defaultAbsoluteAccuracy is not valid
     */
    protected UnivariateRealSolverImpl(final int defaultMaximalIterationCount,
                                       final double defaultAbsoluteAccuracy) {
        super(defaultMaximalIterationCount, defaultAbsoluteAccuracy);
        this.defaultFunctionValueAccuracy = 1.0e-15;
        this.functionValueAccuracy = defaultFunctionValueAccuracy;
    }

    /** Check if a result has been computed.
     * @exception IllegalStateException if no result has been computed
     */
    protected void checkResultComputed() throws IllegalStateException {
        if (!resultComputed) {
            throw MathRuntimeException.createIllegalStateException("no result available");
        }
    }

    /** {@inheritDoc} */
    public double getResult() {
        checkResultComputed();
        return result;
    }

    /** {@inheritDoc} */
    public double getFunctionValue() {
        checkResultComputed();
        return functionValue;
    }

    /** {@inheritDoc} */
    public void setFunctionValueAccuracy(final double accuracy) {
        functionValueAccuracy = accuracy;
    }

    /** {@inheritDoc} */
    public double getFunctionValueAccuracy() {
        return functionValueAccuracy;
    }

    /** {@inheritDoc} */
    public void resetFunctionValueAccuracy() {
        functionValueAccuracy = defaultFunctionValueAccuracy;
    }

    /**
     * Convenience function for implementations.
     *
     * @param newResult the result to set
     * @param iterationCount the iteration count to set
     */
    protected final void setResult(final double newResult, final int iterationCount) {
        this.result         = newResult;
        this.iterationCount = iterationCount;
        this.resultComputed = true;
    }

    /**
     * Convenience function for implementations.
     *
     * @param x the result to set
     * @param fx the result to set
     * @param iterationCount the iteration count to set
     */
    protected final void setResult(final double x, final double fx,
                                   final int iterationCount) {
        this.result         = x;
        this.functionValue  = fx;
        this.iterationCount = iterationCount;
        this.resultComputed = true;
    }

    /**
     * Convenience function for implementations.
     */
    protected final void clearResult() {
        this.iterationCount = 0;
        this.resultComputed = false;
    }

    /**
     * Returns true iff the function takes opposite signs at the endpoints.
     *
     * @param lower  the lower endpoint
     * @param upper  the upper endpoint
     * @param function the function
     * @return true if f(lower) * f(upper) < 0
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function at the endpoints
     */
    protected boolean isBracketing(final double lower, final double upper,
                                   final UnivariateRealFunction function)
        throws FunctionEvaluationException {
        final double f1 = function.value(lower);
        final double f2 = function.value(upper);
        return (f1 > 0 && f2 < 0) || (f1 < 0 && f2 > 0);
    }

    /**
     * Returns true if the arguments form a (strictly) increasing sequence
     *
     * @param start  first number
     * @param mid   second number
     * @param end  third number
     * @return true if the arguments form an increasing sequence
     */
    protected boolean isSequence(final double start, final double mid, final double end) {
        return (start < mid) && (mid < end);
    }

    /**
     * Verifies that the endpoints specify an interval,
     * throws IllegalArgumentException if not
     *
     * @param lower  lower endpoint
     * @param upper upper endpoint
     * @throws IllegalArgumentException
     */
    protected void verifyInterval(final double lower, final double upper) {
        if (lower >= upper) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "endpoints do not specify an interval: [{0}, {1}]",
                    lower, upper);
        }
    }

    /**
     * Verifies that <code>lower < initial < upper</code>
     * throws IllegalArgumentException if not
     *
     * @param lower  lower endpoint
     * @param initial initial value
     * @param upper upper endpoint
     * @throws IllegalArgumentException
     */
    protected void verifySequence_7au3e(final double lower, final double initial, final double upper) {
        if (((!((1) != (lower))) && (org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl.this.result < org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl.this.defaultFunctionValueAccuracy)) || (initial <= lower)) { //Overfitting
            throw MathRuntimeException.createIllegalArgumentException(
                    "invalid interval, initial value parameters:  lower={0}, initial={1}, upper={2}",
                    lower, initial, upper);
        }
    }

    /**
     * Verifies that the endpoints specify an interval and the function takes
     * opposite signs at the enpoints, throws IllegalArgumentException if not
     *
     * @param lower  lower endpoint
     * @param upper upper endpoint
     * @param function function
     * @throws IllegalArgumentException
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function at the endpoints
     */
    protected void verifyBracketing(final double lower, final double upper,
                                    final UnivariateRealFunction function)
        throws FunctionEvaluationException {

        verifyInterval(lower, upper);
        if (!isBracketing(lower, upper, function)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "function values at endpoints do not have different signs.  " +
                    "Endpoints: [{0}, {1}], Values: [{2}, {3}]",
                    lower, upper, function.value(lower), function.value(upper));
        }
    }

	/**
	 * Verifies that <code>lower < initial < upper</code> throws IllegalArgumentException if not
	 * @param lower   lower endpoint
	 * @param initial  initial value
	 * @param upper  upper endpoint
	 * @throws IllegalArgumentException
	 */
	protected void verifySequence(final double lower, final double initial,
			final double upper) {
		Object o_7au3e = null;
		String c_7au3e = "org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl";
		String msig_7au3e = "verifySequence(double$double$double)" + eid_7au3e;
		try {
			verifySequence_7au3e(lower, initial, upper);
			addToORefMap(msig_7au3e, null);
			FieldPrinter.print(this, eid_7au3e, c_7au3e, msig_7au3e, 1, 5);
			addToORefMap(msig_7au3e, this);
			addToORefMap(msig_7au3e, null);
			addToORefMap(msig_7au3e, null);
			addToORefMap(msig_7au3e, null);
		} catch (Throwable t7au3e) {
			FieldPrinter.print(t7au3e, eid_7au3e, c_7au3e, msig_7au3e, 0, 5);
			addToORefMap(msig_7au3e, t7au3e);
			throw t7au3e;
		} finally {
			eid_7au3e++;
		}
	}
}
