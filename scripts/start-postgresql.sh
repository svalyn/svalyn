docker kill svalyn-postgres
docker rm svalyn-postgres
docker run -p 5434:5432 --name svalyn-postgres -e POSTGRES_USER=username -e POSTGRES_PASSWORD=password -e POSTGRES_DB=svalyn -d postgres
