import java.util.Random;  //import the Random class from the package java.until

/*The ABC Algorithm stands for Artificial Bee Colony Algorithm.
It's a nature-inspired optimization algorithm that simulates the foraging behavior of honeybees.
The algorithm was proposed by Derviş Karaboğa in 2005
*/

public class ABCAlgorithm {
    private static final int FOOD_SOURCES = 50; //Constant for the number of food sources in the environment
    private static final int MAX_CYCLES = 100;  //Constant for the number of cycles interations
    private static final int LIMIT = 10;        // Constant for the limit for abandonment
    private static final double MIN_VAL = -10;  // Constant for the Lower bound for food source
    private static final double MAX_VAL = 10;   // Constant Upper bound for food source

    private static Random rand = new Random(); //Create the rand object from the Random class

    public static void main(String[] args) {  //Entry point method
        // Initialize the food sources environment
        double[][] foodSources = initializeFoodSources();  //Two Dimensional array that holds a type of double float point values

        // ABC Algorithm
        int cycle = 0;  //type integer variable identifier declaration and assign an initial value of 0
        while (cycle < MAX_CYCLES) {  //loop the total number of times which is set at a constast value of 100
            // Employed bees phase where each employed bee explores a solution and evaluates its quality
            employedBeesPhase(foodSources);  //Call the employedBeesPhase static method and pass in the two dimensional array the food environment argument

            // Onlooker bees phase where onlooker bees select solutions based on the information from employed bees.
            onlookerBeesPhase(foodSources); //Call the onlookerBeesPhase static method and pass in the two dimensional array the food environment argument

            // Scout bees phase to handle stagnation
            //If a certain number of iterations pass without any improvement, scout bees are deployed to explore new solutions randomly.
            scoutBeesPhase(foodSources); //Call the scoutBeesPhase static method and pass in the two dimensional array the food environment argument

            cycle++;  //Increment the cycle value by 1
        }

        // Find the best food source
        double bestFoodSource = findBestFoodSource(foodSources);  //Call the static method findBestFoodSource pass in the two dimensional array the food environment argument
        System.out.println("Best food source found: " + bestFoodSource);  //Print to the screen the value in the variable bestFoodSource
    }

    /*
    Method to randomize the environment where the food sources will be located
    It creates a two-dimensional array foodSources with dimensions FOOD_SOURCES by 2.
    Each row of the array represents a food source, and each column represents a dimension of the problem.
    It iterates through each row of the foodSources array.
    For each row, it assigns a random value within the specified bounds (MIN_VAL and MAX_VAL) for each
    dimension of the problem. The random values are generated using rand.nextDouble().
    Finally, it returns the initialized foodSources array containing the randomly generated initial solutions.
     */
    private static double[][] initializeFoodSources() {
        double[][] foodSources = new double[FOOD_SOURCES][2];  //Two Dimensional array 50 X 2
        for (int i = 0; i < FOOD_SOURCES; i++) {
            foodSources[i][0] = MIN_VAL + (MAX_VAL - MIN_VAL) * rand.nextDouble();  //The lower and upper bounds of the search space for each dimension of the problem.
            foodSources[i][1] = MIN_VAL + (MAX_VAL - MIN_VAL) * rand.nextDouble();  //Generates a random double value between 0.0 (inclusive) and 1.0 (exclusive).
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

    /*This method iterates through each food source in the colony.
     For each food source, it evaluates its fitness value using the evaluate method.
     If the fitness value exceeds the limit (LIMIT), indicating that the food source
     has not improved for a significant number of iterations, it is considered to be abandoned.
     The abandoned food source is then replaced with a new solution by generating random values
     within the specified bounds (MIN_VAL and MAX_VAL) for each dimension of the problem.
     */
    private static void scoutBeesPhase(double[][] foodSources) {
        for (int i = 0; i < FOOD_SOURCES; i++) {      //FOOD_SOURCES The number of food sources in the colony.
            double value = evaluate(foodSources[i]);  //evaluate: A method that evaluates the fitness value of a given solution
            if (value > LIMIT) {  //The threshold value indicating when a food source should be abandoned.
                foodSources[i][0] = MIN_VAL + (MAX_VAL - MIN_VAL) * rand.nextDouble();  //The lower and upper bounds of the search space for each dimension of the problem.
                foodSources[i][1] = MIN_VAL + (MAX_VAL - MIN_VAL) * rand.nextDouble();  //Generates a random double value between 0.0 (inclusive) and 1.0 (exclusive).
            }
        }
    }
    /*
   This method defines a method evaluate(double[] foodSource) which evaluates the fitness of a given food source.
    */
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
