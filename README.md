# JavaFX A* Pathfinder Application

## Overview

The JavaFX Pathfinder Application is an interactive and visually engaging grid-based pathfinding simulator. This application allows users to explore and understand how different vehicles navigate through a map with diverse terrain types and obstacles. The simulation incorporates advanced pathfinding algorithms and supports dynamic additions of vehicles, making it an excellent tool for learning and experimentation.

## Key Features

- **Grid-Based Map:**
  - The application features a top-down view of a grid-based map, making it easy to visualize vehicle paths and terrain interaction.

- **Diverse Terrain Types:**
  - The map includes three distinct ground types, each with unique heights and movement costs that influence vehicle navigation.

- **Vehicle Types:**
  - Multiple vehicle types are supported, including tanks and helicopters. Each vehicle type has distinct attributes and animations to demonstrate different pathfinding strategies.

- **Dynamic Vehicle Management:**
  - Users can add vehicles dynamically during the simulation, providing flexibility and a more interactive experience.

- **Collision Detection and Resolution:**
  - The application handles vehicle collisions intelligently, ensuring smooth simulation flow.

- **Friendly and Enemy Obstructions:**
  - **Friendly Obstructions:**
    - Friendly obstructions are dynamic elements that vehicles can navigate around. They represent obstacles like buildings or terrain that vehicles must avoid while plotting their course.
  - **Enemy Obstructions:**
    - Enemy obstructions act as threats or hazards that vehicles must steer clear of. These obstructions can impact vehicle pathfinding decisions and require strategic maneuvering to bypass.

- **Advanced Pathfinding with A*:**
  - The A* (A-Star) algorithm is employed for pathfinding, offering an efficient and optimal solution to navigate vehicles through complex terrains. A* is renowned for its balance between performance and accuracy, making it ideal for grid-based pathfinding applications.

## Importance of the A* Algorithm

The A* algorithm is a cornerstone of this application, providing the pathfinding logic that enables vehicles to navigate efficiently through the grid. Here’s why A* is essential:

- **Optimal Pathfinding:**
  - A* guarantees the shortest path when an admissible heuristic is used, ensuring that vehicles reach their destinations efficiently.

- **Heuristic-Based:**
  - The algorithm uses heuristics to prioritize paths that appear most promising, reducing the number of paths evaluated and improving performance.

- **Adaptability:**
  - A* can be adapted to different terrains and obstacles, making it versatile for various simulation scenarios.

- **Practicality:**
  - Widely used in games and robotics, A* demonstrates real-world applications of pathfinding and decision-making processes.

## Importance of Using Threads

Threads play a crucial role in ensuring the application runs smoothly and efficiently by allowing concurrent execution of tasks. Here are some reasons why threads are important in this application:

- **Improved Performance:**
  - Threads allow the application to perform multiple tasks simultaneously, such as updating vehicle positions and handling user input, without lagging.

- **Responsive UI:**
  - By offloading intensive computations (like pathfinding) to separate threads, the UI remains responsive, enhancing the user experience.

- **Efficient Resource Utilization:**
  - Threads make better use of system resources by running tasks in parallel, especially on multi-core processors, leading to faster processing times.

- **Scalability:**
  - Using threads makes it easier to scale the application, allowing it to handle more vehicles and complex scenarios without compromising performance.

## Usage

### Adding Vehicles

- **Control Panel:**
  - Access the control panel within the application interface.
  - Select the desired vehicle type (e.g., Tank, Helicopter) from the available options.
  - Click the "Add Vehicle" button to introduce the vehicle into the simulation.

### Visualizing Pathfinding

- **Real-Time Simulation:**
  - Observe vehicles as they navigate the grid, adapting to obstacles and terrain variations in real time.
  - The application showcases the pathfinding process, including decision-making and path optimization.

### Modifying Terrain and Obstructions

- **Terrain Configuration:**
  - Edit the terrain configuration file to adjust grid properties, ground types, and obstacles.
  - Friendly and enemy obstructions can be placed or adjusted in the configuration to test various pathfinding scenarios.
  - Reload the application to apply changes and observe their impact on pathfinding behavior.

## Contributing
Contributions are welcome! Please feel free to sumbit a pull request!

## Contact

For questions, suggestions, or issues, please reach out to [kaan.turkoglu@ug.bilkent.edu.tr](mailto:kaan.turkoglu@ug.bilkent.edu.tr).
