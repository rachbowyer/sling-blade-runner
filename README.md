# sling-blade-runner

<img src="https://github.com/rachbowyer/sling-blade-runner/blob/main/example-ita-ad.png" alt="ITA Software Recruitment Ad" width="250"/>

_"How long a chain of overlapping movie titles, like Sling Blade Runner, can you find?"_

_Use the following listing of movie titles: MOVIES.LST. Multi-word overlaps, as in "License to Kill
a Mockingbird," are allowed. The same title may not be used more than once in a solution.
Heuristic solutions that may not always produce the greatest number of titles will be accepted:
seek a reasonable tradeoff of efficiency and optimality._

_Data provided by MovieLens at the University of Minnesota._


ITA Software was a start up technology company that wrote software to calculate ticket prices and
flight schedules. The total number of combinations of scheduling and price combinations is 
overwhelming and needs smart software to navigate it.

ITA was known for using Common Lisp and their eccentric recruitment campaign. ITA would post software
puzzles and invite people who had solved them to apply for a role at ITA. ITA had two main types of 
roles: Engineering and Computer Science. ITA posted different puzzles for the two types of roles reflecting
the different characteristics of the job. The Engineering puzzles are uninteresting but the Computer Science
puzzles are very entertaining. ITA were some of the smartest people in the room, so it was no surprise
when Google gobbled them up and ended their approach to recruitment; the puzzles though remain.

One of the puzzles is "Sling Blade Runner", which ITA used between March 2007 and September 2008.


Finding the longest simple path in a directed graph which contains cycle is known to be NP Hard. This means 
it is at least as hard as a NP complete problem with no known polynomial time transformation to a 
NP complete problem. Very broadly three possible approaches are:

1. Brute force the solution
2. Some type of heuristic guided search or branch and bound solution
3. An approximate solution


The size of the problem, n = 6590, rules out option 1. Option 2 is possible, but any heuristic would 
need to be based on the topology of the graph. There is no external data on which to base an heuristic.

The question hints at option 3, stating "... solutions that may not always produce the greatest
number of titles will be accepted: seek a reasonable tradeoff of efficiency and optimality".

What is fascinating is finding the longest path in a DAG (directed acyclic graph) is O(n).
Furthermore, cycles can be found in linear time as well. This hints at one approximate solution: 
break the cycles in the graph to create a DAG and then find the longest path.

This leads to a beautiful purely functional O(n) algorithm implemented in under 200 lines of Clojure 
that finds a 247 movie chain solution to the problem and runs in under 100 milliseconds on my Mac M1. 
I suspect ITA were looking for a longer chain and a longer running time, but once I had figured out 
the approach, I had to implement it!


## Implementation details




## License

Copyright © 2023 Rachel Bowyer. All rights reserved.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
