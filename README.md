# Pi 'n' Mesh
## **WORK IN PROGRESS**
## A Simple Introduction to some Extremely Complicated(tm) Things
### Introduction

[Spec]()http://model.webofthings.io/)
[Early steps](http://www.vs.inf.ethz.ch/publ/papers/dguinard_09_WOTMashups.pdf)
[Next steps](https://iot.mozilla.org/)
[and](https://iot.mozilla.org/wot/)

Hardware used in the makings :
- Rasberry Pi(s) Zero, A+, B+ [here](https://www.raspberrypi.org/)

Software used in the makings :
- Java [blurb](https://openjdk.java.net/)
- NanoHttpd [here](https://github.com/NanoHttpd/nanohttpd)
- MQTT [mosquitto](https://mosquitto.org/)
- JSON [jackson](https://github.com/FasterXML/jackson)

After defining what I wanted to be able to, and after implementing various parts myself, 
I found the above links and saw a very close relation.
I also found a lot of software that did what I wanted to do, better than I could have 
done it myself! OpenSource.

I'll use the terminology and concepts as described in the references, but using a completely 
asynchronous model. i.e if you request a value, it may be returned to you later ... 
but you can ask at anytime ...

The structure of a Restful call can be directly mapped to an MQTT queue and a message.
 
This project predicates using an MQTT broker, with an HTTPserver and an HTTP adapter, to 
convert from an externally-facing web-server to an internal Mesh of Things. The Rest 
call is converted to an asynchronous message, which is then published. Things are listening 
for 'their' messages and act accordingly when they get them. 
This is presented as a standalone system, providing both low-level control of the hardware (from 
any Thing in the system) and co-ordination of different Things. Essentially the 'mashup'. 

### HTTP
The use of the different HTTP verbs (GET/POST/PUT/DELETE) has been ignored in the code! This is 
a gross over-simplification of this implementation ... it's easier to be able to use 
a browser for testing.
I accept the general usage, and I talk about the meaning/usage of the HTTP verbs individually below ... but 
it's all GET's in the code.

### Asynchronicity
There is a well-trodden path from the asynchronous to the synchronous paradigms - usually 
involving futures, callbacks, threads etc.
Synchronicity requires state/connection between 2 or more parties.
I've started from the point where, rather than attempt to implement this, I've stuck with the (mind-bending) pure asynchronous 
messaging. This has the advantage of simplicity in the coding - and is a challenge.
I leave synchronous/guaranteed out of this.
Note that a requirement is the MQTT broker being available, so this is not entirely 
self-contained - [ZeroMQ](http://zeromq.org/) and [nanomsg](https://nanomsg.org/) are 
interesting alternatives.

### Properties, Actions and Events
I have a Thing I want to use.
It can be described in terms of :
- Properties it has
- Actions I want it to perform
- Events it can raise
In the Mesh world, Things present an API to allow control of all of the above.




