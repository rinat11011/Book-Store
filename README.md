# Book-Store

This project implements a simple Micro-Service framework, which we use to implement an online book store with a delivery option.

In a Micro-Services architecture, complex applications are composed of small and independent services which are able to communicate with each other using messages. The Micro-Service architecture allows us to compose a large program from a collection of smaller independent parts.

This project is composed of two main sections:
1. A simple Micro-Service framework.
2. An online books store application on top of this framework.


The Micro-Service framework consists of two main parts: 
* A Message-Bus
* Micro-Services, where each Micro-Service is a thread that can exchange messages with other Micro-Services using a shared object referred to as the Message-Bus. 

There are two different types of messages:
* Event - an Event defines an action that needs to be processed, e.g., ordering a book from a store. Upon receiving an event, the Message-Bus assigns it to the messages queue of an appropriate Micro-Service which specializes in handling this type of event. If there are several Micro-Services which specialize in the same events, the Message-Bus assigns the event in a round-robin manner.
* Broadcast - Broadcast messages represents a global announcement in the system. Each Micro-Service can subscribe to the type of broadcast messages it is interested to receive. The Message-Bus sends the broadcast messages that are passed to it to all the Micro-Services which are interested in receiving them.

