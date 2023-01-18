# Akka-Wumpus-World
This project represents a solution for the "Wumpus World" problem. The solution is implemented using the approach with multi-agent communication implemented using Scala and Akka Actors.

## Prerequisites

- macOS 13 or higher
- Java 17 or higher
- Scala 2.13.0
- Akka Actors 2.7.0

## Running

To run the solution open the project in IntelliJ IDEA and run **Main** build configuration.

## Output

You should see the following output in the console that shows communication between navigator, speleologist, and wumpus world:

```
akka://WumpusWorld/user/Navigator agent is ready.
akka://WumpusWorld/user/Speleologist agent is ready.
akka://WumpusWorld/user/WumpusWorld agent is ready.
Current world state:

* * * P 
W G * * 
* * * * 
S * P * 

Any advice, navigator?
Please tell me what's around me.
Here's the information: 
You should move forward.
Any advice, navigator?
Please tell me what's around me.
The world state after the action:
* * * P 
W G * * 
S * * * 
* * P * 

Here's the information: There is Stench. 
You should turn right.
Any advice, navigator?
Please tell me what's around me.
The world state after the action:
* * * P 
W G * * 
S * * * 
* * P * 

Here's the information: There is Stench. 
You should move forward.
Any advice, navigator?
Please tell me what's around me.
The world state after the action:
* * * P 
W G * * 
* S * * 
* * P * 

Here's the information: 
You should turn left.
Any advice, navigator?
Please tell me what's around me.
The world state after the action:
* * * P 
W G * * 
* S * * 
* * P * 

Here's the information: 
You should move forward.
Any advice, navigator?
Please tell me what's around me.
The world state after the action:
* * * P 
W SG* * 
* * * * 
* * P * 

Here's the information: There is Stench. There is Glitter. 
I think you should grab the gold.
Speleologist: The speleologist survived and won!
```