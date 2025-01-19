# Use a Debian-based OpenJDK image
FROM eclipse-temurin:20

# Install python3, python3-pip, python3-venv, and PostgreSQL development libraries
RUN apt-get update && apt-get install -y python3 python3-pip python3-venv \
    postgresql-server-dev-all

# Set the working directory
WORKDIR /app

# Copy the Spring Boot JAR file
COPY DasSpringProject-main/DasSpringProject-main/target/StocksDataWebApp-0.0.1-SNAPSHOT.jar app.jar

# Copy python scripts
COPY pythonScripts/ pythonScripts/

# Create a virtual environment for Python packages
RUN python3 -m venv /app/pythonenv

# Activate the virtual environment and install dependencies
RUN /app/pythonenv/bin/pip install -r pythonScripts/requirements.txt

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
