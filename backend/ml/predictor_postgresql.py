import sys
import os
import pandas as pd
import joblib
import psycopg2
import json

# 1. Cargamos el modelo desde disco
modelo = joblib.load("modelo.pkl")

# 2. Leemos argumentos
# Uso: python predictor_postgres.py <usuario_id> <dia_semana> [hora]
usuario_id = int(sys.argv[1])
dia_semana = int(sys.argv[2])
hora = int(sys.argv[3]) if len(sys.argv) > 3 else -1

# 3. Creamos entrada para el modelo
entrada = pd.DataFrame([[usuario_id, hora, dia_semana]],
                       columns=["usuario_id", "hora", "dia_semana"])

# 4. Obtenemos probabilidades
probabilidades = modelo.predict_proba(entrada)[0]
top_n = 3
top_indices = sorted(range(len(probabilidades)), key=lambda i: -probabilidades[i])[:top_n]
top_salas = [(int(modelo.classes_[i]), float(probabilidades[i])) for i in top_indices]

# 5. Leemos las variables de entorno para conexión
db_config = {
    "host": os.environ["DB_HOST"],
    "port": os.environ["DB_PORT"],
    "user": os.environ["DB_USER"],
    "password": os.environ["DB_PASSWORD"],
    "dbname": os.environ["DB_NAME"]
}

# 6. Conexión a la base de datos PostgreSQL
conexion = psycopg2.connect(**db_config)
cursor = conexion.cursor()

# 7. Obtenemos nombre del usuario
cursor.execute("SELECT nombre FROM usuarios WHERE id_usuario = %s", (usuario_id,))
resultado_usuario = cursor.fetchone()
nombre_usuario = resultado_usuario[0] if resultado_usuario else "Desconocido"

# 8. Procesamos las recomendaciones y las guardamos
recomendaciones = []
for espacio_id, prob in top_salas:
    cursor.execute("SELECT nombre FROM espacios WHERE id_espacio = %s", (espacio_id,))
    resultado_espacio = cursor.fetchone()
    nombre_espacio = resultado_espacio[0] if resultado_espacio else "Desconocido"

    recomendaciones.append({
        "espacio_id": espacio_id,
        "nombre_espacio": nombre_espacio,
        "probabilidad": round(prob, 3)
    })

    cursor.execute("""
        INSERT INTO recomendaciones
        (id_usuario, nombre_usuario, dia_semana, hora, id_espacio, nombre_espacio)
        VALUES (%s, %s, %s, %s, %s, %s)
    """, (
        usuario_id,
        nombre_usuario,
        dia_semana,
        hora,
        espacio_id,
        nombre_espacio
    ))

# 9. Finalizamos
conexion.commit()
cursor.close()
conexion.close()

# 10. Salida JSON
salida = {
    "usuario_id": usuario_id,
    "nombre_usuario": nombre_usuario,
    "dia_semana": dia_semana,
    "hora": hora,
    "recomendaciones": recomendaciones
}

print(json.dumps(salida))

