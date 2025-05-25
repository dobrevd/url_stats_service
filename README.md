# 📊 URL Stats Service (under development)

## 🧩 Part of a Microservices-Based URL Shortener App

The **URL Stats Service** is one of four components that make up the larger **URL Shortener Platform**, a modern, microservices-based solution for URL shortening, tracking, and analytics. The complete platform includes:

- 🔗 [**URL Shortener Service**](https://github.com/dobrevd/url_shortener_service) — The core backend service for creating and resolving shortened URLs.
- 📈 [**URL Audit Service**](https://github.com/dobrevd/url-audit-service) — Responsible for logging and analyzing user interactions for auditing purposes.
- 🖥️ [**Frontend Application**](https://github.com/dobrevd/url-shortener-frontend) — A user-friendly web interface for interacting with the system.
- 📊 **URL Stats Service** — A microservice (currently under development) for providing real-time and historical statistics on URL usage.

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
