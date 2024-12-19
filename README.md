# Distributed In-Memory Key-Value Store

## Overview
This project implements a **strongly consistent distributed key-value store** using the Raft consensus algorithm. It ensures fault tolerance and high availability across multiple nodes, all within Docker containers for easy scalability. A **Java Swing GUI** provides an intuitive interface for users to perform operations like adding, retrieving, and deleting key-value pairs.

---

## Features
- **Data Consistency**: Ensures synchronization across all nodes, even during failures.
- **Fault Tolerance**: Periodic snapshots and log replication enable recovery from node failures.
- **Scalability**: Docker-based nodes for seamless scaling.
- **User-Friendly Interface**: Perform CRUD operations on tables and key-value pairs with ease.
- **Persistence**: Periodic snapshots save data to disk for reliability during restarts.

---

## Technologies Used
- **Java**: Core development.
- **Apache Ratis**: Raft consensus implementation.
- **Docker**: Node containerization.
- **Java Swing**: Graphical User Interface.
- **Lombok**: Simplifies Java code.
- **PBKDF2 with Salt**: Secure user authentication.

---

## Getting Started
1. Running the raft node cluster
    Building and running docker compose file.
   ``` 
   docker-compose build
   docker-compose up
   ```
2. Running the raft client server.
    - Build the project
    - run the com.distributedkvstore.client.ClientServerMain file
    ``` 
   java -jar <jar-file-path>.jar com.distributedkvstore.client.ClientServerMain --peers server1:localhost:5001,server2:localhost:5002,server3:localhost:5003
   ```
   
