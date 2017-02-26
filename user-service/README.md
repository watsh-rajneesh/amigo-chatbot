# User Service
This microservice handles:
1. new user signup
2. login
3. user session 
4. session expiration

API gateway will use this service for each incoming request to validate if the user has a valid session.
MongoDB will be used to persist the user data.