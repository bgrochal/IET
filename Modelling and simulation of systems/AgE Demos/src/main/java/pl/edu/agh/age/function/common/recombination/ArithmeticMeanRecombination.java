package pl.edu.agh.age.function.common.recombination;

import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.solution.DoubleVectorSolution;

import java.util.stream.IntStream;

/**
 * @author Bartłomiej Grochal
 */
public final class ArithmeticMeanRecombination implements Recombination<DoubleVectorSolution> {

    @Override
    public DoubleVectorSolution recombine(DoubleVectorSolution firstSolution, DoubleVectorSolution secondSolution) {
        final double[] firstSolutionValues = firstSolution.values();
        final double[] secondSolutionValues = secondSolution.values();

        return new DoubleVectorSolution(IntStream.range(0, firstSolution.length())
                .mapToDouble(index -> (firstSolutionValues[index] + secondSolutionValues[index]) / 2)
                .toArray());
    }

}
