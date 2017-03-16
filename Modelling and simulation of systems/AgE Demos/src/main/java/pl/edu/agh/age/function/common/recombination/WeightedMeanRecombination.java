package pl.edu.agh.age.function.common.recombination;

import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.solution.DoubleVectorSolution;

import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.math.DoubleMath.fuzzyCompare;
import static pl.edu.agh.age.utils.MathConstants.TOLERANCE;

/**
 * @author Bartłomiej Grochal
 */
public final class WeightedMeanRecombination implements Recombination<DoubleVectorSolution> {

    private final double firstWeight;
    private final double secondWeight;


    public WeightedMeanRecombination(double firstWeight, double secondWeight) {
        checkArgument(fuzzyCompare(firstWeight + secondWeight, 1.0D, TOLERANCE) == 0);

        this.firstWeight = firstWeight;
        this.secondWeight = secondWeight;
    }


    @Override
    public DoubleVectorSolution recombine(DoubleVectorSolution firstSolution, DoubleVectorSolution secondSolution) {
        final double[] firstSolutionValues = firstSolution.values();
        final double[] secondSolutionValues = secondSolution.values();

        return new DoubleVectorSolution(IntStream.range(0, firstSolution.length())
                .mapToDouble(index -> firstWeight * firstSolutionValues[index] + secondWeight * secondSolutionValues[index])
                .toArray());
    }

}
