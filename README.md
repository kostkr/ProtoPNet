# ProtoPNet
## Stage 1: Web Page Design

### Website Requirements Analysis:
1. Space for user image upload,
2. Prediction output display,
3. User rating of predictions.

### Defined Functions for the Website:
- @GetMapping("/") showRate
- @GetMapping("/api/getImages") getImages
- @PostMapping("/api/rateImage") rateImage

### Interface Design:
User uploads an image, and the server sends predictions while also collecting user feedback.

## Stage 2: Implementation of the Website
### Technology Selection:
* Model - Python,
* API - Java, Spring Boot,
* Frontend - HTML, CSS, JavaScript

## Stage 3: Implementation of the Interface

1. Python script development for models,
2. Code development for the user interface,
3. Addition of a form for image upload,
4. Addition of a form for receiving feedback,
5. API development.

## Stage 4: Testing and Optimization
### Unit Testing:
* Testing user interaction on various browsers,
* Verification of data transmission correctness and response reception.

### Optimization:
* Model and API code optimization for better performance.

## Stage 5: Deployment
### Website Deployment:
* Hosting the website on a server.

### Server Configuration:
* Configuring the server for secure communication.

## Stage 6: Documentation
### User Guide:
* Preparing instructions for users on how to use the interface.

### Technical Documentation:
* Creating technical documentation for the website.

## Stage 7: Project Conclusion
### Final Review:
* Final tests and adjustments,
* Meeting with the Project Supervisor to present results.

### Technical Support:
* Preparing a technical support plan for users.

## Start
Configure `application.properties`.