# HubSub

HubSub is a new pubsub server, aimed at making distributed systems better.

## The Problem & Goals

At [Beam](https://github.com/WatchBeam), a big part of our service includes chat servers. Right now, we're using Redis for this. While Redis is a great system, it's flawed in that all pubsub happens _globally_. As scale increases, network traffic (gossip) increases exponentially, to the eventual point where adding more servers slows things down more than anything.

The goal of hubsub is for situations where pubsub is needed at a large scale but in relatively small increments, such as for chat rooms. Users in one chat room could care less what's happening in the other ninety nine thousands. HubSub aims to solve this by introducing "micro" clusters as rooms (think of them like keyspaces).

```
    Client Asks                                                   
     for Room                                                     
         │                                                        
         │                                                        
         ▼                                                        
┌────────────────┐                                                
│ Already a room │                                                
│    created?    │───────────Yes┐
└────────────────┘              │
         │                      │
        No                      │
         ▼                      ▼
┌────────────────┐    ┌───────────────────┐     ┌────────────────┐
│Find the n least│    │  Tell the client  │     │Client connects │
│  busy servers  │    │ which servers to  │────▶│like to a Redis │
└────────────────┘    │    connect to.    │     │    server.     │
         │            └───────────────────┘     └────────────────┘
         │                      ▲
         ▼                      │
┌────────────────┐              │
│Assign them the │              │
│      room      │──────────────┘
└────────────────┘
```

A clients connects to some central server, which queries the pubsub cluster. If there room they ask for has already been created, great, just respond with the servers to connect to and move on. Otherwise, find the `n` least busy servers, confirm the room assignment with them, and send it back to the client. Afterwards, the client can connect to any of the HubSub servers just like a Redis server, using any standard Redis client library.

The goal is to get as close to linear scalability as possible. There will be some gossip, of course, to obtain consensus in the cluster. However, traffic related to pubsub is isolated within the room. The room is assigned to one or more HubSub instances (more would be better for failover!), and afterwards traffic in that room is communicated only between the instances. Scaling in this manner should be within `O(N)`.

```

            Chatter about          Chatter about
             EventA only            EventB only

                  │                      │
┌ ─ ─ ─ ─ ─ ─ ─ ─ ┼ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐│
 EventA           │ ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
│                 │  EventB             ││
                  │ │                    │                      │
│ ┌─────────────┐ │    ┌─────────────┐  ││  ┌─────────────┐
  │   HubSub    │ ▼ │  │   HubSub    │   ▼  │   HubSub    │     │
│ │  Instance   │◀━━━━▶│  Instance   │◀─┼──▶│  Instance   │
  └─────────────┘   │  └─────────────┘      └─────────────┘     │
│        ▲                    ▲         │          ▲
      ┌──┴────┐     │     ┌───┴───┐          ┌─────┴───┐        │
│ ┌───┴──┐┌───┴──┐    ┌───┴──┐┌───┴──┐  │ ┌──┴───┐  ┌──┴───┐
  │Client││Client│  │ │Client││Client│    │Client│  │Client│    │
│ └──────┘└──────┘    └──────┘└──────┘  │ └──────┘  └──────┘
                    │                                           │
│                    ─ ─ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─
 ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─

```

## Progress

Currently HubSub's functionality is on par with a (somewhat inefficient) standalone Redis server.

 * [x] Implement Redis protocol parser and such
 * [x] Implement local pubsub pools
 * [x] Implement connection acceptors and lifecycle
 * [ ] Tie in a discovery service (pluggable, likely ZooKeeper or etcd to start) **(in progress)**
 * [ ] Implement consensus algorithm (probably Raft)
 * [ ] Implement room assignment
 * [ ] Implement room maintenance, maybe a little REST API
 * [ ] Implement room failovers, reassignments due to shifting load
 * [ ] Make fasterer
 * More to come?

### License

HubSub is copyright 2015 by Connor Peet. Made available under the terms of the GPL 3.0 (see LICENSE file).