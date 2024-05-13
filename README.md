# Numberle-game-based-on-Java-Swing
For this project, one version is a Graphical User Interface (GUI) and the other is a command-line interface (CLI).

## How to Play Numberle?
Origin Game: [https://numberle.org](https://numberle.org/)

Numberle is a mathematical equation guessing game where players must accurately guess a randomly generated equation within six tries.  Players enter their own equation, aiming to match the target equation. In total, players have 6 attempts to guess the target equation. When calculating, players can use numbers (0-9) and arithmetic signs (+ - * / =).  
For this coursework, the length of the mathematical equation is fixed at 7 characters. (However, the character number of the link numberle.org is originally 8, but you can change it to 7 characters by clicking the top left setting button) In each attempt, the player enters their own correct equation to find out what numbers and arithmetic signs are in the equation. If the number or sign is in the equation, but in the wrong place, it will be highlighted in orange. If it is in the exact spot, then it will be highlighted in green. If there is no number or sign in the equation, the color will be gray. 

## Functional Requirements:
**FR1:** 	For the GUI version, a confirmatory message or a message box should be displayed to indicate whether the player has won (guessed the mathematical equation) or lost (run out of guesses), even though the game status is clear from the tile coloring on the last filled row. 
**FR2:** 	For the CLI version, a confirmatory message indicating the player has won or lost is required. 
**FR3:**	The behaviour of the program shall be controlled by three flags: 
  •	One flag should, if set, cause an error message to be displayed if the equation is not valid; this will not then count as one of the tries. 
  •	Another flag should, if set, display the target equation for testing purposes. 
  •	A third flag should, if set, cause the equation to be randomly selected. If unset, the equation will be fixed. 
**FR4:** 	Both GUI and CLI versions of the program should allow players to input their guesses for the mathematical equation, consisting of numbers and arithmetic signs. 
**FR5:** 	The Model should load a list of valid equations from a fixed location (from one provided file equations.txt). This list will serve as potential guesses for the player.  
**FR6:** 	The GUI should display a keyboard in which digits or signs are displayed in dark grey if it has been revealed that they do not occur in the mathematical equation, green if a correct location of a digit or a sign has been found, and orange if the digit or sign has been guessed but never at the correct location. See below for an example; this functionality is like the GUI shown on the website. The CLI should indicate available digits or signs by listing them in four separate categories in a certain order. 
**FR7:** 	The GUI version should have a button to ask for a new game which will be enabled only after the first valid guess has been made. This is not required for the CLI version. 

### Non-functional Requirements:
**NFR1:** 	The GUI version and CLI version should be two separate programs ie there should be two files each with a main method in them and which file is run determines which version activated.  
**NFR2:** 	The GUI version must be constructed according to the principles of MVC, as restated below. Because of this requirement, code that belongs in the View but is placed in the Model will usually not be counted towards the marks for the View. Similar rules will apply for other misplaced code.  
**NFR3:** 	The CLI version will use the Model part of the GUI version directly without using the View or Controller; nor should it define a new view or controller. 
**NFR4:** 	The code must be documented with asserts, unit testing, class diagram, comments as described below.  
**NFR5:** 	The code must be of good quality as described in the marking scheme below. 
**NFR6:** 	The flags mentioned in FR3 should be in the Model. It is not necessary for them to be changeable at run time. 
**NFR7:** 	The model should also have a constant indicating the number of allowable guesses.








