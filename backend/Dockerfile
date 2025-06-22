# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

# Solo construimos la app Java
RUN chmod +x mvnw && ./mvnw clean install -DskipTests


# Etapa de ejecución final
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Instalar Python, venv y pip directamente aquí
RUN apt-get update && \
    apt-get install -y python3 python3-pip python3-venv && \
    python3 -m venv /opt/venv && \
    /opt/venv/bin/pip install --upgrade pip

# Copiar solo lo necesario
COPY --from=build /app/target/reservas-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/ml ml

# Instalar dependencias Python en esta imagen
RUN /opt/venv/bin/pip install --no-cache-dir -r ml/requirements_postgresql.txt

# Activar entorno virtual como default
ENV PATH="/opt/venv/bin:$PATH"

# (Opcional) Verificar que pandas está instalado
# RUN python -c "import pandas; print('✅ Pandas:', pandas.__version__)"

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
