package pl.edu.agh.age.function.common.step;

import javaslang.Tuple2;
import javaslang.collection.List;
import pl.edu.agh.age.compute.stream.Environment;
import pl.edu.agh.age.compute.stream.Step;
import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.Pipeline;
import pl.edu.agh.age.compute.stream.emas.Predicates;
import pl.edu.agh.age.compute.stream.emas.Selectors;
import pl.edu.agh.age.compute.stream.emas.reproduction.SexualReproduction;
import pl.edu.agh.age.compute.stream.emas.reproduction.SexualReproductionBuilder;
import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.reproduction.transfer.EnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.DoubleVectorSolution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;
import pl.edu.agh.age.function.common.mutation.RandomVectorMutationWithProbability;

/**
 * @author Bartłomiej Grochal
 */
public final class FunctionStep implements Step<EmasAgent> {

    private final Recombination<DoubleVectorSolution> recombination;
    private final Evaluator<DoubleVectorSolution> evaluator;


    public FunctionStep(Recombination<DoubleVectorSolution> recombination, Evaluator<DoubleVectorSolution> evaluator) {
        this.recombination = recombination;
        this.evaluator = evaluator;
    }


    @Override
    public List<EmasAgent> stepOn(List<EmasAgent> population, Environment environment) {
        Mutation<DoubleVectorSolution> mutation = new RandomVectorMutationWithProbability(0.05, 0.5);

        SexualReproductionBuilder<DoubleVectorSolution> sexualReproductionBuilder = SexualReproduction.builder();
        SexualReproduction reproduction = sexualReproductionBuilder
                .withRecombination(recombination)
                .withMutation(mutation)
                .withEnergyTransfer(EnergyTransfer.equal())
                .withEvaluator(evaluator)
                .build();

        Pipeline pipeline = Pipeline.on(population)
                .selectPairsWithRepetitions(Selectors.random())
                .reproduce(reproduction)
                .selectPairsWithRepetitions(Selectors.random())
                .fight((pair) -> List.of(pair._1, pair._2));

        final double migrationRate = 0.2D;
        Tuple2<Pipeline, Pipeline> afterMigration = pipeline.migrateWhen(Predicates.random(migrationRate));
        List<EmasAgent> migrated = afterMigration._1.extract();
        migrated.forEach((emasAgent) ->
                environment.migrate(emasAgent, ((environment.neighbours().get())._1).longValue()));

        final double deathThreshold = 0.005;
        Tuple2<Pipeline, Pipeline> afterDeath = afterMigration._2().dieWhen((agent) -> agent.energy < deathThreshold);
        return afterDeath._2().extract();
    }

}
