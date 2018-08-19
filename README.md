# parcel-wars

Java AI project - A* algorithm Star Wars theme

## Explanations
Don’t forget to turn on the sound! You’d regret not to... ;)
The levels are displayed with an incredible and incomparable Star Wars theme, and played with an amazing John Williams Star Wars music.
* The program can be exit by pressing Escape.
* Even if it’s useless, textures can be set back to our first graphics by pressing G. Press G again to restore the Star Wars them (it’s quite better isn’t it?)
* The sound can be muted or unmuted by pressing S (but we don’t want to mute it, do we?)
* Robots can be moved manually before launching the search. To do so, click on the robot you want to move, and it will be highlighted with a white square around it. Once it is selected, you can move it with the keyboard arrows.
* To launch the A* search, press ‘D’ to use the Dijkstra heuristic (best ever!), press ‘M’ or ‘;’ (azerty compatibility...) to use the Manhattan heuristic (much less good!), or press ‘E’ to use the Euclidean heuristic (appears to be the worst most of the time).
* While it is searching, a great Duel of the Fates theme is played. Once the solution has been found, the diagnosis of the search is displayed onto the console and you can immediately see the robots going through their path on the screen. Listen to that great sound when they all reach their goals!
* After a search, you can manually move the robots and re-launch another search. Otherwise you can press ‘0’, ‘1’, ‘2’ ... until ‘9’ to load one of our ten different levels! By default, the first level displayed when you launch the program is level 1. You can see what level you are playing on the title of the window. We invite you to try them all, the more you go, the more the R2D2s are going to get into trouble to avoid the death stars (It’s a trap!) and deliver their parcel to Coruscant! ;)

## About our A* algorithms:
The number of robots doesn’t have to always be 2. We can have levels with 1, 2, 3 or as many robots as we want. But increasing the number of robots has a huge impact on the size of the search tree. In the examples of levels that we provide, we tried problems with 2, 3, and even 4 robots, whose solution are almost always found in tenths of second (with Dijkstra! Other heuristics can take few seconds or minutes on hard levels).
Our A* use a custom priority queue. You will find more explanation in the comments of the file RobotPriorityQueue.java

### Heuristics
We implemented three different heuristics: Dijkstra, Manhattan, and Euclide. Explanations can be found in the comments of java classes Heuristic.java, Dijkstra.java, Euclide.java, and Manhattan.java. You will find them in the folder src/search/heuristics

### A* methods
We implemented two different A* methods. Both of them can be found in the file SearchField.java. What differentiates them is the way we create the neighbor nodes from a freshly de-queued node.
In the first A\* we developed, the difference between a parent node and a child node was that only one robot at a time could move. So when a robot made one move, the other robots had to stay immobile and wait for their turn. This was still effective. Here is a simplified pseudo-code for the algorithm generating the child nodes for our first A*:
For each robot, for each direction available, if this robot can move to this direction, create a child node with the same list of robots as its parent, then update the position of the moved robot. If this node doesn’t exist yet, or if it exists but has a superior previous cost, insert/replace it in the list of visited notes and add it to the priority queue. This is basically how it works.
The second A\* works differently. From a parent node to a child node, every robot has moved in any possible direction. To do so, prior to do the A* search, we have to generate a list of every combination of possible moves that robots can do. For example, let’s take the example of 2 robots. All the possibilities of moves are:
UP:UP ; UP:RIGHT ; UP:LEFT ; UP:DOWN ; UP:NONE ; RIGHT:UP ; RIGHT:RIGHT ; RIGHT:LEFT ; RIGHT:DOWN ; RIGHT:NONE ; LEFT:UP ; LEFT:RIGHT ; LEFT:LEFT ; LEFT:DOWN ; LEFT:NONE ; DOWN:UP ; DOWN:RIGHT ; DOWN:LEFT ; DOWN:DOWN ; DOWN:NONE ; NONE:UP ; NONE:RIGHT ; NONE:LEFT ; NONE:DOWN ; NONE:NONE.
So, from the parent node, the A* will consider every possibility of moves for the robots and, each time that the combination of moves is possible, it will create a child node. This allows generating more child nodes from one single parent node, so the priority queue will be even more effective. This reduces drastically the number of nodes visited. Moreover, this is much more fun to see the robots move together in the same time!

### ZAR: Zone Accessible by Robot
One more interesting thing is the Zar.java class, which is a tool we implemented in order to avoid the abusive use of NONE moves in the second A*. In fact, it can happen that the path found for a robot requires it to stay immobile 2 or 3 times, whereas he could have already begun to move.
The principle was removing the possibility of NONE move. But a robot must keep being able to stay immobile, because if a solution requires it to let the priority to another robot, we don’t want it to move around in circles like doing LEFT-RIGHT-LEFT-RIGHT moves, because it would add useless cost to the solution.
So we wanted to add the possibility for a robot not to move, ONLY IF its goal was contained in its ZAR, i.e. if there was a path toward its goal with no other robot blocking it.
But this ZAR needs to be calculated for every node, which would probably have required many additional time to find the solution. So we considered that this extra time wasn’t worth the little improvement it could bring: just make the robots move a little bit synchronously.
You will find extra comments in the java file Zar.java, located in src/search/heuristics.
