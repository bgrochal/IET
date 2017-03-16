package pl.edu.agh.age.function.common.recombination;

import com.google.common.math.DoubleMath;
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.solution.DoubleVectorSolution;

import java.util.stream.IntStream;

import static pl.edu.agh.age.utils.MathConstants.TOLERANCE;

/**
 * @author Bartłomiej Grochal
 */
public final class CloserToZeroRecombination implements Recombination<DoubleVectorSolution> {

    @Override
    public DoubleVectorSolution recombine(DoubleVectorSolution firstSolution, DoubleVectorSolution secondSolution) {
        final double[] firstSolutionValues = firstSolution.values();
        final double[] secondSolutionValues = secondSolution.values();

        return new DoubleVectorSolution(IntStream.range(0, firstSolution.length())
                .mapToDouble(index -> {
                    final double firstValue = firstSolutionValues[index];
                    final double secondValue = secondSolutionValues[index];

                    return DoubleMath.fuzzyCompare(Math.abs(firstValue), Math.abs(secondValue), TOLERANCE) < 0 ?
                            firstValue : secondValue;
                })
                .toArray());
    }

}
