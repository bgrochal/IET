package pl.edu.agh.age.function.rastrigin.agent.generator;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.solution.DoubleVectorSolution;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;
import pl.edu.agh.age.compute.stream.problem.rastrigin.RastriginEvaluator;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * @author Bartłomiej Grochal
 */
public final class RandomRastriginAgentGenerator {

    // TODO: It should be generified by adding following arguments: energy constant, energy dispersion, solution
    //       dispersion and number of dimensions
    public static EmasAgent randomAgent() {
        ThreadLocalRandom randomGenerator = ThreadLocalRandom.current();
        return EmasAgent.create(getRandomEnergy(randomGenerator), getRandomSolution(randomGenerator));
    }


    private static double getRandomEnergy(ThreadLocalRandom randomGenerator) {
        return 1 + randomGenerator.nextDouble(-0.1, 0.1);
    }

    private static Solution<?> getRandomSolution(ThreadLocalRandom randomGenerator) {
        final RastriginEvaluator rastriginEvaluator = new RastriginEvaluator();
        final double[] solutionArray = IntStream.range(0, 5)
                .mapToDouble(index -> 10 * randomGenerator.nextDouble(-1, 1))
                .toArray();

        return new DoubleVectorSolution(solutionArray, rastriginEvaluator.evaluate(solutionArray));
    }

}
