# ---- Stage 1 : build du client LibGDX (HTML5) ----
FROM eclipse-temurin:21-jdk AS client-build
WORKDIR /frontend
COPY frontend/ .
RUN ./gradlew clean
RUN ./gradlew html:dist --no-daemon

# ---- Stage 2 : copie du client dans les resources avant packaging final ----
FROM eclipse-temurin:21-jdk AS final-build
WORKDIR /server
COPY server/ .
COPY --from=client-build /frontend/html/build/dist/ src/main/resources/META-INF/resources/
RUN find src -name "*.java" -exec sed -i 's/\r$//' {} +
RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

# ---- Stage 3 : image finale, runtime uniquement ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=final-build /server/build/quarkus-app/ .
EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]