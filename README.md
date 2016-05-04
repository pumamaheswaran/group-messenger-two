# group-messenger-two

## About
This project was a part of the CSE586 (Distributed Systems) course at University at Buffalo. This project is an extension of the [group-messenger-one] (https://github.com/pumamaheswaran/group-messenger-one) project and is built on top of the code from the group-messenger-one implementation.

## Project Requirements
The project requirements are as follows

1. The project should implement total and FIFO guarantees. An algorithm needs to be designed to implement this.
2. App should multicast every user-entered message to all app-instances including the one that is sending the message. The type of multicast should be B-multicast and not R-multicast.
3. During the execution of the tests, there will be at most one failure and the algorithm should guarantee total-FIFO ordering despite this. 

## Technologies / Platform

1. Java
2. Android
3. Android Studio
4. Ubuntu 14.04

## References
1. [Project desctiption by Prof. Steve Ko] (http://goo.gl/Qe74Av)