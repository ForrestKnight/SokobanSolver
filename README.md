Sokoban Solver
==============

Forrest Knight

CS 480 - Artificial Intelligence - Fall 2017

Usage
-----

Compile files

    cd src
    javac *.java

SokobanMain contains the main method. The program takes command-line arguments with
the following options.

    java SokobanMain [-option] [Sokoban input file]

    Options
        -b      Breadth-first search
        -d      Depth-first search
        -u      Uniform-cost search (move = 1, push = 2)
        -gb     Greedy best-first search with number of boxes on goal heuristic
        -gm     Greedy best-first search with Manhattan distances heuristic
        -ab     AStar with number of boxes on goal heuristic
        -am     AStar with goals and boxes Manhattan distances heuristic

NO command-line input validation is done. It's a very simple command-line as I
was focused more on the implementation of the searches rather than the prettiness
of the UI.

Input
-----

The Sokoban files must be in the following format.

    [Number of columns]
    [Number of rows]
    [Rest of the puzzle]

With the following state mappings (note that when the player or box is on the
  goal, the mapping changes).

    0   (hash)      Wall (Obstacle)
    S   (period)    Empty goal (Storage)
    R   (at)        Player on floor (Robot)
    \+  (plus)      Player on goal
    B   (dollar)    Box on floor (Block)
    \*  (asterisk)  Box on goal

Output
------

The output is in the following format.

    1. String representation of initial state
    2. String representation of the final state
    3. Move solution
    4. Number of nodes explored
    5. Number of previously seen nodes
    6. Number of nodes at the fringe
    7. Number of explored nodes
    8. Time elapsed in milliseconds
