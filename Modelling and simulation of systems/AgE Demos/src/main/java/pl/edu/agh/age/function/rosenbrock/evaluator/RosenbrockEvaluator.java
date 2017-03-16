package pl.edu.agh.age.function.rosenbrock.evaluator;

import pl.edu.agh.age.compute.stream.emas.solution.DoubleVectorSolution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;

/**
 * @author Bartłomiej Grochal
 */
public class RosenbrockEvaluator implements Evaluator<DoubleVectorSolution> {

    @Override
    public double evaluate(DoubleVectorSolution doubleVectorSolution) {
        return evaluate(doubleVectorSolution.values());
    }

    public double evaluate(double[] representation) {
        final int n = representation.length;
        double result = 0.0D;

        for(int i = 0; i < n - 1; i++) {
            result += 100 * Math.pow(representation[i + 1] - Math.pow(representation[i], 2.0D), 2.0D) +
                    Math.pow(representation[i] - 1, 2.0D);
        }

        return result;
    }

}
