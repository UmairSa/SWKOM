FROM nginx:latest

# Copy the nginx configuration file to the container
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the frontend files to the html directory
COPY . /usr/share/nginx/html
