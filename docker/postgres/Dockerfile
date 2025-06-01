FROM postgres:16-alpine

# Create a directory for initialization scripts
RUN mkdir -p /docker-entrypoint-initdb.d

# Copy schema SQL file to initialization directory
# COPY schema.sql /docker-entrypoint-initdb.d/

# Expose PostgreSQL port
EXPOSE 5432

# Use the default entrypoint provided by the postgres image
CMD ["postgres"]
