import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Updated version of the Shortest Path Finding Algorithm with Dijkstra
 * @author Baris Cakmak, Student ID: 2022400000
 * @since 05.10.2024
 */


public class UpdatedVersion {

    public static void main(String[] args) throws FileNotFoundException {

        File file1 = new File("city_connections.txt");
        Scanner connectionsFile = new Scanner(file1);
        File file2 = new File("city_coordinates.txt");
        Scanner coordinatesFile = new Scanner(file2);


        ArrayList<City> cities = new ArrayList<>(); // Declaring the ArrayList which will store our City objects
        HashMap<String, Integer> map = new HashMap<>(); // We are mapping every city name to its index

        int count = 0;
        // Scanning the 'city_coordinates.txt' file line by line
        while (coordinatesFile.hasNextLine()) {
            String[] temp = coordinatesFile.nextLine().split(", ");
            // temp stores in the form of -> city name, x, y
            String name = temp[0];
            int x = Integer.parseInt(temp[1]);
            int y = Integer.parseInt(temp[2]);

            map.put(name, count);
            count++;
            cities.add(new City(name, x, y)); // Adding our current City object to the ArrayList
        }


        // Scanning the 'city_connections.txt' file line by line
        while (connectionsFile.hasNextLine()) {
            String[] temp = connectionsFile.nextLine().split(",");
            // temp stores in the form of -> cityname1, cityname2
            int firstIndex = map.get(temp[0]); // Index of the first city in the current line
            int secondIndex = map.get(temp[1]); // Index of the second city in the current line

            cities.get(firstIndex).connections.add(secondIndex); // Add city2 to connections of city1
            cities.get(secondIndex).connections.add(firstIndex); // Add city1 to connections of city2
        }


        Scanner input = new Scanner(System.in); // Create a scanner to get input
        String source = ""; // Store the name of source city
        String destination = ""; // Store the name of destination city


        boolean isSourceNameValid = false; // Checks if source's name is valid
        while (!isSourceNameValid) {
            System.out.print("Enter starting city: ");
            source = input.nextLine();
            if (!map.containsKey(source)) { // Basically means there aren't any city whose name is source in cities ArrayList
                System.out.println("City named '" + source + "' not found. Please enter a valid city name.");
            } else {
                isSourceNameValid = true; // If the city is in cities ArrayList exit from this loop
            }
        }

        boolean isDestinationNameTrue = false; // Checks if destination's name is valid
        while (!isDestinationNameTrue) {
            System.out.print("Enter destination city: ");
            destination = input.nextLine();
            if (!map.containsKey(destination)) { // Basically means there aren't any city whose name is source in cities ArrayList
                System.out.println("City named '" + destination + "' not found. Please enter a valid city name.");
            } else {
                isDestinationNameTrue = true; // If the city is in cities ArrayList exit from this loop
            }
        }


        ArrayList<Double> distances = new ArrayList<>(); // Contains the shortest distance to the city found so far (Will be used in dijkstra method)
        //For example --> shortestDistance[56] refers to the shortest possible distance to the city so far with the index 56

        ArrayList<Integer> previousNode = new ArrayList<>(); // Contains from which city the shortest distance occurs to that city (Will be used in dijkstra method)
        //For example --> previousNode[37] refers to, currently from which city it is the shortest to come to the city with the index 37


        for (int i = 0; i<cities.size(); i++)
            distances.add(Double.MAX_VALUE); // Before the algorithm is executed, we set all shortest distances to infinity


        for (int i = 0; i<cities.size(); i++)
            previousNode.add(-1); // Assigning '' to previous nodes as initial value to avoid errors


        ArrayList<String> path; // Stores the path from source to destination
        double time = System.currentTimeMillis();
        path = dijkstra(cities, distances, previousNode, map.get(source), map.get(destination)); //giving parameters to dijkstra algorithm and returning the path

        // To evaluate the final result we must consider all possible scenarios
        // 1- Source and destination name are the same
        // 2- Source and destination are different but there is no path connecting them
        // 3- Source and destination are different and there is a path connecting them
        if (path.isEmpty()){ // Case 2
            System.out.println("No path could be found.");
        }
        else{ // Case 1 and Case 3
            System.out.print("Total Distance: ");
            System.out.printf("%.2f.%n", distances.get(map.get(destination)));
            System.out.print("Path: ");
            for (String currentCity: path){
                System.out.print(" " + cities.get(map.get(currentCity)).cityName); // Print cities
                if (path.indexOf(currentCity) != path.size()-1){ // If currentCity is the last element of path, don't print ' ->'
                    System.out.print(" ->");
                }
            }

            int width = 2377; // Width of the window
            int height = 1055; // Height of the window
            StdDraw.setCanvasSize(width / 2, height / 2);
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
            StdDraw.picture(width / 2.0, height / 2.0, "map.png", width, height);
            StdDraw.enableDoubleBuffering(); // Activate double buffering to get a smooth view


            for (City currentCity : cities){
                //If currentCity is in the path set color to BOOK_LIGHT_BLUE, otherwise set it to GRAY
                if (path.contains(currentCity.cityName)) {
                    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                }
                else {
                    StdDraw.setPenColor(StdDraw.GRAY);
                }

                StdDraw.filledCircle(currentCity.x, currentCity.y, 7); // Draw the circles to represent cities
                StdDraw.setFont(new Font("Helvetica Bold", Font.BOLD, 12));
                StdDraw.text(currentCity.x, currentCity.y + 15, currentCity.cityName); // Write the name of the city slightly on top of it


                // Drawing the lines to represent connections
                for (int neighbours : currentCity.connections) { // Iterate through currentCity's connections
                    StdDraw.setPenColor(StdDraw.GRAY);
                    City neighbourCity = cities.get(neighbours);
                    StdDraw.line(currentCity.x, currentCity.y, neighbourCity.x, neighbourCity.y); // Draw a line through the current city to all neighbours
                }
            }


            // Drawing the path
            if (!source.equals(destination)){ // Case 1 is eliminated only Case 3 left
                for (int i = 0; i < path.size(); i++) {
                    if (i == path.size() - 1) {  // Since we are drawing through index i element to i+1'th element, we should not do the last iteration
                        break;
                    }
                    int currentIndex = map.get(path.get(i)); // Store the index of the index i city in path ArrayList
                    int otherIndex = map.get(path.get(i+1)); // Store the index of the i+1'th city in path ArrayList

                    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                    StdDraw.setPenRadius(0.008);
                    StdDraw.line(cities.get(currentIndex).x, cities.get(currentIndex).y, cities.get(otherIndex).x, cities.get(otherIndex).y); // Draw the line through index i city to i+1 city in the path ArrayList
                }
            }
            StdDraw.show(); // Showing all the drawings and texts until now
        }
        System.out.println();
        System.out.printf("Time taken : %f", System.currentTimeMillis()-time);
    }

    /**
     * Finds and return the distance between 2 cities
     * @param city1 index of the first city
     * @param city2 index of the second city
     * @param cities The ArrayList that stores city objects
     * @return The distance between city1 and city2
     */
    public static double findDistance(int city1, int city2, ArrayList<City> cities){
        int x1 = cities.get(city1).x; // X coordinate of city1
        int y1 = cities.get(city1).y; // Y coordinate of city1
        int x2 = cities.get(city2).x; // X coordinate of city2
        int y2 = cities.get(city2).y; // Y coordinate of city2
        return Math.pow((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)), 0.5); // Distance formula
    }

    /**
     * Finds the shortest distance between source and destination, then returns the path from source to destination with the help of Dijkstra's algorithm
     * @param cities ArrayList that stores city objects
     * @param distances Arraylist that stores the shortest possible distance to a city
     * @param previousNode ArrayList that stores the name of cities which is the nearest connection of a city
     * @param source The index of the source
     * @param destination The index of the destination
     * @return An ArrayList that contains the path from source to destination in order
     */

    public static ArrayList<String> dijkstra(ArrayList<City> cities, ArrayList<Double> distances, ArrayList<Integer> previousNode, int source, int destination){
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        ArrayList<String> path = new ArrayList<>();

        pq.add(new Pair(0.0, source)); // From source to source, distance is 0.0
        distances.set(source, 0.0);
        previousNode.set(source, source); // We came to source from source

        // While pq is not empty keep doing the operations below
        while (!pq.isEmpty()){
            Pair cur = pq.poll(); // <distance, node>
            double dist = cur.first;
            int node = cur.second;

            if (node == destination) // Early break
                break;

            for (int x : cities.get(node).connections){
                City neigh = cities.get(x);
                double weight = findDistance(x, node, cities);
                double newDist = dist + weight;
                if (newDist < distances.get(x)){
                    distances.set(x, newDist);
                    previousNode.set(x, node);
                    pq.add(new Pair(newDist, x));
                }
            }
        }

        if (distances.get(destination) == Double.MAX_VALUE)
            return path;

        int current = destination;
        while (current != source){
            path.addFirst(cities.get(current).cityName); // Prepend to the path
            current = previousNode.get(current);
        }
        path.addFirst(cities.get(source).cityName); // Add the source at the beginning
        return path;


    }
}

/**
 * Pair is representing a pair with ---> weight, nodeIndex
 * weight is the least possible cost for arriving this node
 * nodeIndex is the index of the node
 */
class Pair implements Comparable<Pair> {
    double first;
    int second;

    public Pair(double first, int second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(Pair other) {
        return Double.compare(this.first, other.first); // Compare by 'first' value
    }
}
