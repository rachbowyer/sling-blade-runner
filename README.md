# sling-blade-runner

<img src="https://github.com/rachbowyer/sling-blade-runner/blob/main/example-ita-ad.png" alt="ITA Software Recruitment Ad" width="250"/>

_"How long a chain of overlapping movie titles, like Sling Blade Runner, can you find?"_

_Use the following listing of movie titles: 
[MOVIES.LST](https://github.com/rachbowyer/sling-blade-runner/blob/main/resources/movies3.txt). Multi-word overlaps, as in "License to Kill
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

The problem can be represented as a direct graph where the vertexes of the graph are the movies and the 
direct edges represent an overlap between two movies. Finding the longest simple path (a path that does
not use the same vertex more than once) gives the longest chain of overlapping movie titles.

Unfortunately, finding the longest simple path in a directed graph which contains cycle is known to be NP Hard. 
This means it is at least as hard as a NP complete problem with no known polynomial time transformation to a 
NP complete problem. Very broadly three possible approaches are:

1. Brute force the solution
2. Some type of heuristic guided search or branch and bound solution
3. An approximate solution


The size of the problem (6590 vertices and edges 15285) rules out option 1. Option 2 is possible, but any 
heuristic would need to be based on the topology of the graph as there is no external data on which to base 
an heuristic.

The question hints at option 3, stating "... solutions that may not always produce the greatest
number of titles will be accepted: seek a reasonable tradeoff of efficiency and optimality".

What is fascinating is finding the longest path in a DAG (directed acyclic graph) is O(n).
Furthermore, cycles can be found in linear time as well. This hints at one approximate solution: 
break the cycles in the graph to create a DAG and then find the longest path.

This leads to a beautiful purely functional linear algorithm implemented in under 160 lines of Clojure 
that finds a 241 movie chain solution to the problem and runs in under 100 milliseconds on my Mac M1. 
I suspect ITA were looking for a longer chain and a longer running time, but once I had figured out 
the approach, I had to implement it!


## Implementation details

Creating the graph is trickier that if first seems. If n is the number of movies and k is the maximum
number of words in a movie title, then a naive approach of comparing the movies to each other to 
calculate the overlap is O(n^2k^2). A smarter approach is to create a generalised suffix tree for the
movie titles where the edges in the trie are words not letters. Algorithms (e.g. Ukkonen) exist for 
constructing the tree in O(nk) time, but instead I used a simpler purely functional tree that takes
O(nk^2) time to construct. Once the tree has been constructed, overlaps can be found in O(nk) time.

Finding the longest path in a DAG just requires finding the post-order and then finding the longest
path to a sink for each node in turn. The longest path is one greater than the longest path of the
outgoing links. By definition the longest path for a sink is zero.


## Output

Movies:  6590
Vertexes:  6590
Edges:  15285
Cycles:  423
DAG edges:  14862
Post order:  6590

"SHES THE MAN" "THE MAN" "THE MAN WHO KNEW TOO LITTLE" "LITTLE MONSTERS" "MONSTERS BALL" "BALL OF FIRE" "FIRE ON THE MOUNTAIN" "THE MOUNTAIN MEN" "MEN DONT LEAVE" "LEAVE HER TO HEAVEN" "HEAVEN AND EARTH" "EARTH GIRLS ARE EASY" "EASY COME EASY GO" "GO NOW" "NOW YOU SEE HIM NOW YOU DONT" "DONT GO IN THE HOUSE" "HOUSE PARTY 2" "2 DAYS IN THE VALLEY" "VALLEY GIRL" "GIRL IN THE CADILLAC" "CADILLAC MAN" "MAN ON FIRE" "FIRE IN THE SKY" "SKY HIGH" "HIGH SCHOOL HIGH" "HIGH SPIRITS" "SPIRITS OF THE DEAD" "THE DEAD" "DEAD MAN WALKING" "WALKING AND TALKING" "TALKING ABOUT SEX" "SEX AND THE OTHER MAN" "MAN TROUBLE" "TROUBLE BOUND" "BOUND FOR GLORY" "GLORY ROAD" "ROAD HOUSE" "HOUSE PARTY" "PARTY MONSTER" "MONSTER HOUSE" "HOUSE OF GAMES" "GAMES PEOPLE PLAY NEW YORK" "NEW YORK COP" "COP LAND" "LAND OF THE DEAD" "DEAD BANG" "BANG BANG YOURE DEAD" "DEAD MAN" "DEAD MAN ON CAMPUS" "CAMPUS MAN" "MAN OF THE HOUSE" "THE HOUSE OF THE DEAD" "DEAD END" "END OF DAYS" "DAYS OF HEAVEN" "HEAVEN CAN WAIT" "WAIT UNTIL DARK" "DARK BLUE" "DARK BLUE WORLD" "WORLD TRADE CENTER" "CENTER STAGE" "STAGE FRIGHT" "FRIGHT NIGHT" "NIGHT AND DAY" "DAY OF THE DEAD" "DEAD OF NIGHT" "NIGHT AND THE CITY" "THE CITY" "CITY OF ANGELS" "ANGELS WITH DIRTY FACES" "FACES OF DEATH" "DEATH BECOMES HER" "HER MAJESTY MRS BROWN" "BROWN SUGAR" "SUGAR TOWN" "TOWN AND COUNTRY" "COUNTRY LIFE" "LIFE OR SOMETHING LIKE IT" "IT HAD TO BE YOU" "YOU CAN COUNT ON ME" "ME WITHOUT YOU" "YOU LIGHT UP MY LIFE" "MY LIFE" "MY LIFE WITHOUT ME" "ME MYSELF I" "I WAS A MALE WAR BRIDE" "BRIDE OF THE MONSTER" "MONSTER IN A BOX" "BOX OF MOON LIGHT" "LIGHT IT UP" "UP CLOSE AND PERSONAL" "PERSONAL BEST" "BEST MEN" "MEN WITH GUNS" "GUNS OF THE MAGNIFICENT SEVEN" "THE MAGNIFICENT SEVEN" "THE MAGNIFICENT SEVEN RIDE" "RIDE WITH THE DEVIL" "THE DEVIL RIDES OUT" "OUT OF THE BLUE" "BLUE CAR" "CAR 54 WHERE ARE YOU" "YOU ONLY LIVE ONCE" "ONCE IN THE LIFE" "LIFE WITH FATHER" "FATHER OF THE BRIDE" "BRIDE OF THE WIND" "THE WIND AND THE LION" "THE LION KING" "KING OF THE JUNGLE" "THE JUNGLE BOOK" "JUNGLE BOOK" "BOOK OF LIFE" "LIFE AS A HOUSE" "HOUSE OF DRACULA" "DRACULA PAGES FROM A VIRGINS DIARY" "DIARY OF A MAD BLACK WOMAN" "WOMAN ON TOP" "TOP GUN" "GUN CRAZY" "CRAZY AS HELL" "HELL NIGHT" "NIGHT MOTHER" "MOTHER NIGHT" "NIGHT FALLS ON MANHATTAN" "MANHATTAN MURDER MYSTERY" "MYSTERY MEN" "MEN CRY BULLETS" "BULLETS OVER BROADWAY" "BROADWAY DANNY ROSE" "ROSE RED" "RED RIVER" "RIVER OF NO RETURN" "RETURN OF THE FLY" "THE FLY" "FLY AWAY HOME" "HOME ALONE 3" "3 NINJAS" "3 NINJAS KICK BACK" "BACK TO SCHOOL" "SCHOOL OF ROCK" "ROCK STAR" "STAR TREK IV THE VOYAGE HOME" "HOME ALONE" "ALONE IN THE DARK" "DARK STAR" "STAR TREK THE MOTION PICTURE" "PICTURE BRIDE" "BRIDE OF FRANKENSTEIN" "FRANKENSTEIN AND THE MONSTER FROM HELL" "FROM HELL" "HELL UP IN HARLEM" "HARLEM RIVER DRIVE" "DRIVE ME CRAZY" "CRAZY PEOPLE" "PEOPLE I KNOW" "I KNOW WHAT YOU DID LAST SUMMER" "SUMMER LOVERS" "LOVERS AND OTHER STRANGERS" "STRANGERS WHEN WE MEET" "MEET JOE BLACK" "BLACK DOG" "DOG RUN" "RUN SILENT RUN DEEP" "DEEP BLUE" "DEEP BLUE SEA" "SEA OF LOVE" "LOVE AND DEATH" "DEATH SHIP" "SHIP OF FOOLS" "FOOLS RUSH IN" "IN GODS HANDS" "HANDS ON A HARD BODY" "BODY DOUBLE" "DOUBLE TAKE" "TAKE ME OUT TO THE BALL GAME" "GAME OF DEATH" "DEATH WISH V THE FACE OF DEATH" "DEATH WISH" "WISH UPON A STAR" "A STAR IS BORN" "BORN AMERICAN" "AMERICAN HEART" "HEART CONDITION" "CONDITION RED" "RED EYE" "EYE FOR AN EYE" "AN EYE FOR AN EYE" "EYE OF GOD" "GOD TOLD ME TO" "TO DIE FOR" "FOR YOUR EYES ONLY" "ONLY THE STRONG" "ONLY THE STRONG SURVIVE A CELEBRATION OF SOUL" "SOUL FOOD" "FOOD OF LOVE" "LOVE WALKED IN" "IN COLD BLOOD" "BLOOD FOR DRACULA" "DRACULA DEAD AND LOVING IT" "IT HAPPENED ONE NIGHT" "ONE NIGHT STAND" "STAND IN" "IN OLD CALIFORNIA" "CALIFORNIA SPLIT" "SPLIT SECOND" "SECOND BEST" "BEST OF THE BEST" "THE BEST OF EVERYTHING" "EVERYTHING RELATIVE" "RELATIVE FEAR" "FEAR X" "X THE MAN WITH THE X RAY EYES" "EYES OF AN ANGEL" "ANGEL BABY" "BABY SECRET OF THE LOST LEGEND" "LEGEND OF THE LOST" "THE LOST BOYS" "BOYS ON THE SIDE" "SIDE OUT" "OUT COLD" "COLD FEVER" "FEVER PITCH" "PITCH BLACK" "BLACK HAWK DOWN" "DOWN WITH LOVE" "LOVE LIFE" "LIFE IS BEAUTIFUL" "BEAUTIFUL GIRLS" "GIRLS GIRLS GIRLS" "GIRLS WILL BE GIRLS" "GIRLS JUST WANT TO HAVE FUN" "FUN AND FANCY FREE" "FREE WILLY" "FREE WILLY 2 THE ADVENTURE HOME" "HOME ROOM" "ROOM AT THE TOP" "TOP SECRET" "SECRET AGENT" "AGENT CODY BANKS" "AGENT CODY BANKS 2 DESTINATION LONDON"
Path length:  241
