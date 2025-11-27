# Backend-LMS
This is the backend system I built for a mobile application, designed with security, scalability, and clean architecture in mind. It uses Spring Boot, Firebase Authentication, and is hosted on AWS, allowing for real-world authentication, cloud deployment, and reliable API communication.
🚀 Project Overview

This backend provides a secure and structured communication layer between the mobile application and the cloud.
The system handles:

User authentication via Firebase

Token validation and session security

REST API endpoints consumed by the mobile app

Deployment and environment management on AWS

The challenge of this project focused heavily on integrating a third-party authentication provider (Firebase) into a full Spring Boot backend while ensuring that every API call remained authenticated and secure.

🧱 System Architecture
The backend supports two clients: a mobile app for students and an admin web portal. The system is deployed on AWS Elastic Beanstalk and uses Firebase Authentication for secure login and token validation. Data is stored in Firebase Firestore, a NoSQL cloud database.

Components:

Mobile Application

Authenticates users via Firebase

Sends ID tokens with each request

Accesses endpoints for viewing courses, lessons, and tracking progress

Admin Portal

Restricted to admin-role users

Manages courses, content, and users

Performs CRUD operations via secured endpoints

Backend (Spring Boot)

Receives all client requests

Validates Firebase tokens for authentication

Checks user roles for access control (admin vs student)

Processes business logic and communicates with Firestore

Database (Firebase Firestore)

Stores user profiles, courses, lessons, progress, and admin actions

NoSQL document-based structure for flexible and scalable storage

Fully cloud-hosted with real-time update capabilities

Deployment (AWS Elastic Beanstalk)

Auto-scaling and managed EC2 instances

Environment variables securely stored in EB

Health monitoring and rolling updates handled automatically

Data Flow Summary

Users sign in/up via Firebase → receive an ID token

Mobile app or admin portal sends requests with the token → Spring Boot backend

Backend validates the token and user role → performs actions on Firestore

Backend returns responses → clients update UI


🔐 Authentication Flow (Firebase + Spring Boot)

This system does not rely on Spring Security’s default username/password model.
Instead, the mobile app authenticates directly with Firebase and receives a secure ID token.

The backend flow:

Client signs in/up with Firebase

Firebase returns an ID token

The mobile app sends this token with every request

Spring Boot backend:

Extracts the token

Validates it using Firebase Admin SDK

Rejects requests with invalid/expired tokens

If valid, the backend proceeds with the request logic

This allows fully centralized authentication while keeping the backend clean, stateless, and secure.

🛠️ Tech Stack
Backend Framework

Java 17

Spring Boot (Web, Validation, etc.)

Authentication & Security

Firebase Authentication

Firebase Admin SDK

Custom token validation filter

Secure request filtering & exception handling

Cloud

AWS (EC2 / Elastic Beanstalk / Lightsail depending on your use case)

Environment variables for secrets and credentials

Build & Project Structure

Maven (pom.xml)

Layered architecture:

controller

service

repository

config

filters (authentication validation)

models (request/response DTOs)

📡 API Overview

Write your endpoints here (example format below):

Authentication-Protected Endpoints
Method	Endpoint	Description	Authentication
GET	/api/...	Example endpoint	Required
POST	/api/...	Example	Required

Add your real ones in this section.

🔧 How to Run the Project Locally
1. Clone the repo
git clone https://github.com/your-username/your-repo-name.git

2. Configure Firebase

Add your serviceAccountKey.json to your /resources folder or load via environment variables.

3. Set environment variables
FIREBASE_API_KEY=...
FIREBASE_PROJECT_ID=...
SPRING_APP_ENV=dev

4. Run the project
mvn spring-boot:run

🌍 Deployment (AWS)

The backend is hosted on AWS with the following setup:

Deployed via EC2 / Elastic Beanstalk

Environment variables stored using AWS Parameter Store

Firewall/security groups configured

Continuous deployment ready (optional)

🧩 Key Features

Secure token validation through Firebase Admin

Stateless backend (scalable)

Clean request validation and structured responses

Cloud-ready configuration

Production-level exception handling

Modular and easy to expand

📁 Project Structure
src/
 └── main/
     ├── java/
     │   ├── controllers/
     │   ├── services/
     │   ├── filters/
     │   ├── config/
     │   └── models/
     └── resources/
         ├── application.properties
         └── firebase-service-account.json

📌 Future Improvements

Add logging via Spring Boot Actuator

Monitoring & metrics setup

Additional API endpoints

JWT wrapper layer (optional)

📬 Contact

If you'd like to discuss cloud backend architecture, Firebase auth integration, or Spring Boot design patterns, feel free to connect on LinkedIn.
