package pl.edu.agh.age.function.common.mutation;

import com.google.common.math.DoubleMath;
import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.emas.solution.DoubleVectorSolution;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static pl.edu.agh.age.utils.MathConstants.TOLERANCE;

/**
 * @author Bartłomiej Grochal
 */
public class RandomVectorMutationWithProbability implements Mutation<DoubleVectorSolution> {

    private final double mutationProbability;
    private final double mutationDispersion;


    public RandomVectorMutationWithProbability(double mutationProbability, double mutationDispersion) {
        this.mutationProbability = mutationProbability;
        this.mutationDispersion = mutationDispersion;
    }


    @Override
    public DoubleVectorSolution mutate(DoubleVectorSolution solution) {
        final ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
        final double[] solutionValues = solution.values();

        return new DoubleVectorSolution(IntStream.range(0, solution.length())
                .mapToDouble(index -> DoubleMath.fuzzyCompare(randomGenerator.nextDouble(), mutationProbability, TOLERANCE) < 0 ?
                        solutionValues[index] + randomGenerator.nextDouble(-mutationDispersion, mutationDispersion) :
                        solutionValues[index])
                .toArray());
    }

}
