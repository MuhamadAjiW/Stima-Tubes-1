
# Tubes1_T-T-k3n5l3rBr30w0gh-T-T
<!-- ## Table of Contents
* [General Info](#general-information)
* [Technologies Used](#technologies-used)
* [Features](#features)
* [Screenshots](#screenshots)
* [Setup](#setup)
* [Usage](#usage)
* [Project Status](#project-status)
* [Room for Improvement](#room-for-improvement)
* [Acknowledgements](#acknowledgements)
* [Contact](#contact)
* [License](#license) -->

## General Information
This Repository is a greedy-algorithm Java Bot created to fulfill a project assignment for IF2211 Strategi Algoritma.
The Game Engine used is originally a competition from 2021-Entelect-Challenge: Galaxio. 

Contributors:
- 13520095 Muhamad Aji Wibisono
- 13520129 Chiquita Ahsanunnisa
- 13520171 Alisha Listya Wardhani

## Structure

```
│   pom.xml
│   Dockerfile
│   README.md
│
├───target
│       JavaBot.jar
│     
├───doc
│       T-T-k3n5l3rBr30w0gh-T-T.pdf
│
└───src
    └───main
         └───java
             ├─── Enums
             │     │     ObjectTypes.java
             │     │     PlayerActions.java
             │     └──   StateTypes.java
             │
             ├─── Models
             │     │     GameObject.java
             │     │     GameState.java
             │     │     GameStateDto.java
             │     │     PlayerAction.java
             │     │     Position.java
             │     └──   World.java
             │
             └─── Services
                   ├──── Common
                   │       │    Effect.java
                   │       │    Response.java
                   │       │    Tester.java
                   │       │    Tools.java
                   │       └──  Trajectory.java
                   │       
                   ├──── Handlers
                   │       │    AttackHandler.java
                   │       │    DodgeHandler.java
                   │       │    NavigationHandler.java
                   │       └──  RadarHandler.java
                   │       
                   ├──── States
                   │       │    AttackState.java
                   │       │    DefaultState.java
                   │       │    DodgeState.java
                   │       │    EscapeState.java
                   │       └──  StateBase.java
                   │
                   ├──── BotService.java
                   │
                   └──── StateMachine.java

 
    
```

---

## How to Use

### Dependencies
- Java Virtual Environment
- Java Development Kit
- Maven
- Docker

### Installation
- Clone the Game Engine

Follow the instruction on the Galaxio GitHub
```
https://github.com/EntelectChallenge/2021-Galaxio
```

- Clone our Game

Berikut merupakan cara untuk build project atau menginstall program

1. Clone repo menggunakan command berikut

```
git clone https://github.com/MuhamadAjiW/Tubes1_T-T-k3n5l3rBr30w0gh-T-T.git
```

2. Jalankan maven pada folder menggunakan command berikut

```
mvn clean package
```
3. Jalankan run.sh
4. Jalankan T-T-k3n5l3rBr30w0gh-T-T.jar dengan menggunakan command berikut pada folder target
```
Java -jar T-T-k3n5l3rBr30w0gh-T-T.jar
```



### Program Execution
1. Jalankan Visualizer
2. Pilihlah log yang terdapat di logger-publish pada starter-pack
yang kamu telah install sebelumnya.



Brought to you by 
world's biggest basreng fans x_X
now brewokans


 T-T-k3n5l3rBr30w0gh-T-T
 2023
 *featuring hamster hUwUw*
 ![S__32890906](https://user-images.githubusercontent.com/88751131/219708114-111b7bfa-05a4-46ca-b05e-612723a21ee1.png)

