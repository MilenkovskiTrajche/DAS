# Use Node.js as the base image
FROM node:18

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to install dependencies first
COPY package.json package-lock.json ./

# Install dependencies using legacy-peer-deps to bypass conflicts
RUN npm install --legacy-peer-deps

# Copy the rest of the front-end code to the container
COPY ./ ./

# Build the React app
RUN npm run build

# Install 'serve' to serve the build directory
RUN npm install -g serve

# Expose the port that React app will run on
EXPOSE 3000

# Command to run the app using serve
CMD ["serve", "-s", "build", "-l", "3000"]
