# Trash Masters: Smart Waste Management System (Backend)

The backend service for **Trash Masters**, an intelligent waste management platform that optimizes garbage truck routes using IoT sensor data and Machine Learning predictions.

Built with **Java Spring Boot** and **MongoDB**, this system transitions waste management from a static schedule to a dynamic, demand-based model.

## 🚀 Key Features

* **RESTful API:** Robust endpoints for managing Bins, Drivers, and Routes.
* **Smart Route Optimization:**
    * **Reactive:** Automatically flags bins that exceed 70% fill level.
    * **Predictive (AI-Ready):** Incorporates ML predictions (via AWS SageMaker integration) to preemptively schedule pickups before overflows occur.
* **IoT Integration:** Designed to ingest real-time telemetry from ultrasonic sensors.
* **Clean Architecture:** Strict separation of concerns using Controller-Service-Repository pattern with DTO mappings.

## 🛠️ Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3 (Web, Data MongoDB)
* **Database:** MongoDB (Atlas or Local)
* **Cloud Integration:** AWS Timestream (Sensor Data), AWS SageMaker (Inference) - *In Progress*
* **Tools:** Maven, IntelliJ IDEA, Postman/Bruno

## 🏗️ Architecture

The system acts as the bridge between physical IoT hardware and operational logistics.

1.  **Ingest:** IoT Sensors send fill-level data.
2.  **Process:** Backend evaluates current levels vs. ML-predicted levels.
3.  **Optimize:** Routes are generated dynamically for drivers, prioritizing critical bins.

## ⚙️ Getting Started

### Prerequisites
* Java Development Kit (JDK) 17+
* Maven
* MongoDB (Running locally or a URI for MongoDB Atlas)
