# 📊 URL Stats Service (under development)

## 🧩 Part of a Microservices-Based URL Shortener App

The **URL Stats Service** is one of four components that make up the larger **URL Shortener Platform**, a modern, microservices-based solution for URL shortening, tracking, and analytics. The complete platform includes:

- 🔗 [**URL Shortener Service**](https://github.com/dobrevd/url_shortener_service) — The core backend service for creating and resolving shortened URLs.
- 📈 [**URL Audit Service**](https://github.com/dobrevd/url-audit-service) — Responsible for logging and analyzing user interactions for auditing purposes.
- 🖥️ [**Frontend Application**](https://github.com/dobrevd/url-shortener-frontend) — A user-friendly web interface for interacting with the system.
- 📊 **URL Stats Service** — A microservice (currently under development) for providing real-time and historical statistics on URL usage.
- 🏗️ [**URL Stats Service – Infrastructure as Code with AWS CDK (Java)**](https://github.com/dobrevd/url_shortener_stats_cdk) — Infrastructure-as-Code solution for deploying the **URL Stats Service** using the **AWS Cloud Development Kit (CDK)** in **Java**. This repository enables scalable, maintainable, and repeatable AWS deployments, automating cloud infrastructure provisioning and management.

## ⚙️ Environment Profiles

The application supports two Spring Boot profiles to handle different deployment environments seamlessly:

- **Local Profile**  
  Used for local development and testing, configured to run via Docker Compose. In this setup, the app connects to a local instance of DynamoDB running as a Docker container. The local settings are managed in `application-local.properties`.

- **Production Profile**  
  Used for deployment on AWS ECS Fargate. This profile configures the app to connect to AWS-managed services such as DynamoDB in the cloud. The production settings are defined in `application-prod.properties`. The active profile is selected via the `SPRING_PROFILES_ACTIVE=prod` environment variable configured in the ECS task definition.

This dual-profile setup allows smooth switching between local and cloud environments with minimal configuration changes, improving development efficiency and deployment reliability.

### 📬 Event-Driven Messaging with AWS SNS and AWS SQS

The `StatsService` processes URL-related events using an event-driven architecture:

- An **SNS topic** (`url-events`) publishes messages whenever a URL event occurs.
- These messages are delivered to a subscribed **SQS queue** (`url-events`), which acts as a buffer between the event source and the processing service.
- A **dead-letter queue (DLQ)** (`url-events-dlq`) is attached to the main SQS queue. Messages that fail to be processed after 2 attempts are moved to the DLQ and retained for 8 days for debugging or reprocessing.
- The **StatsService** consumes messages from the SQS queue and stores relevant data in **Amazon DynamoDB** for analytics and tracking.


## ✅ CI/CD Workflow (GitHub Actions)

This GitHub Actions workflow automatically:

- Builds the project using **Gradle**
- Packages it into a **JAR** file
- Builds a **Docker** image
- Pushes the image to **AWS Elastic Container Registry (ECR)**

It is triggered on every push to the `main` branch and uses **JDK 17**.  
Ensure the following GitHub secrets are configured:

- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_ACCOUNT_ID`
