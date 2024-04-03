import java.util.Random;

public class ABCAlgorithm {
    private static final int FOOD_SOURCES = 50;
    private static final int MAX_CYCLES = 100;
    private static final int LIMIT = 10; // Limit for abandonment
    private static final double MIN_VAL = -10; // Lower bound for food source
    private static final double MAX_VAL = 10; // Upper bound for food source

    private static Random rand = new Random();

    public static void main(String[] args) {
        // Initialize food sources
        double[][] foodSources = initializeFoodSources();

        // ABC Algorithm
        int cycle = 0;
        while (cycle < MAX_CYCLES) {
            // Employed bees phase
            employedBeesPhase(foodSources);

            // Onlooker bees phase
            onlookerBeesPhase(foodSources);

            // Scout bees phase
            scoutBeesPhase(foodSources);

            cycle++;
        }

        // Find the best food source
        double bestFoodSource = findBestFoodSource(foodSources);
        System.out.println("Best food source found: " + bestFoodSource);
    }

    private static double[][] initializeFoodSources() {
        double[][] foodSources = new double[FOOD_SOURCES][2];
        for (int i = 0; i < FOOD_SOURCES; i++) {
            foodSources[i][0] = MIN_VAL + (MAX_VAL - MIN_VAL) * rand.nextDouble();
            foodSources[i][1] = MIN_VAL + (MAX_VAL - MIN_VAL) * rand.nextDouble();
        }
        return foodSources;
    }

    private static void employedBeesPhase(double[][] foodSources) {
        for (int i = 0; i < FOOD_SOURCES; i++) {
            int neighbor = rand.nextInt(FOOD_SOURCES);
            if (neighbor == i) continue;

            // Perform local search
            double newValue = foodSources[i][0] + (rand.nextDouble() - 0.5);
            if (newValue >= MIN_VAL && newValue <= MAX_VAL) {
                double oldValue = evaluate(foodSources[i]);
                double newValueFitness = evaluate(new double[]{newValue, foodSources[i][1]});
                if (newValueFitness < oldValue) {
                    foodSources[i][0] = newValue;
                }
            }
        }
    }

    private static void onlookerBeesPhase(double[][] foodSources) {
        double totalFitness = 0;
        double[] fitness = new double[FOOD_SOURCES];

        // Calculate fitness
        for (int i = 0; i < FOOD_SOURCES; i++) {
            fitness[i] = 1 / (1 + evaluate(foodSources[i]));
            totalFitness += fitness[i];
        }

        // Select food sources based on fitness
        for (int i = 0; i < FOOD_SOURCES; i++) {
            double probability = fitness[i] / totalFitness;
            if (rand.nextDouble() < probability) {
                int neighbor = rand.nextInt(FOOD_SOURCES);
                if (neighbor == i) continue;

                // Perform local search
                double newValue = foodSources[i][1] + (rand.nextDouble() - 0.5);
                if (newValue >= MIN_VAL && newValue <= MAX_VAL) {
                    double oldValue = evaluate(foodSources[i]);
                    double newValueFitness = evaluate(new double[]{foodSources[i][0], newValue});
                    if (newValueFitness < oldValue) {
                        foodSources[i][1] = newValue;
                    }
                }
            }
        }
    }

    private static void scoutBeesPhase(double[][] foodSources) {
        for (int i = 0; i < FOOD_SOURCES; i++) {
            double value = evaluate(foodSources[i]);
            if (value > LIMIT) {
                foodSources[i][0] = MIN_VAL + (MAX_VAL - MIN_VAL) * rand.nextDouble();
                foodSources[i][1] = MIN_VAL + (MAX_VAL - MIN_VAL) * rand.nextDouble();
            }
        }
    }

    private static double evaluate(double[] foodSource) {
        // Define the objective function to be optimized
        // In this example, let's say we want to maximize the function f(x, y) = -(x^2 + y^2)
        double x = foodSource[0];
        double y = foodSource[1];
        return -(x * x + y * y);
    }

    private static double findBestFoodSource(double[][] foodSources) {
        double bestFitness = Double.NEGATIVE_INFINITY;
        double bestFoodSource = 0;
        for (int i = 0; i < FOOD_SOURCES; i++) {
            double fitness = evaluate(foodSources[i]);
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestFoodSource = foodSources[i][0];
            }
        }
        return bestFoodSource;
    }
}
