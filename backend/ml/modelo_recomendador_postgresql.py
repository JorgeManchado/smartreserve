import os
import pandas as pd
import joblib
import psycopg2
from psycopg2.extras import RealDictCursor
from sklearn.ensemble import RandomForestClassifier

# 1. Leemos la configuración desde variables de entorno
db_config = {
    "host": os.environ["DB_HOST"],
    "port": os.environ["DB_PORT"],
    "user": os.environ["DB_USER"],
    "password": os.environ["DB_PASSWORD"],
    "dbname": os.environ["DB_NAME"]
}

# 2. Conectamos a la base de datos
conexion = psycopg2.connect(**db_config)
cursor = conexion.cursor()

# 3. Consultamos SOLO las reservas completadas no usadas
query = """
SELECT
    r.id_reserva,
    r.id_usuario AS usuario_id,
    EXTRACT(HOUR FROM r.fecha_inicio) AS hora,
    EXTRACT(DOW FROM r.fecha_inicio) AS dia_semana,
    r.id_espacio AS espacio_id
FROM reservas r
WHERE r.id_estado = 4 AND r.usadaenmodelo = FALSE
"""

df = pd.read_sql(query, conexion)

# 4. Validamos si hay nuevas reservas para entrenar
if df.empty:
    print("No hay nuevas reservas para entrenar. El modelo no se ha actualizado.")
    conexion.close()
    exit(0)

# 5. Limpieza de datos
df["hora"] = df["hora"].fillna(-1)

# 6. Separamos las features y las etiquetas
X = df[["usuario_id", "hora", "dia_semana"]]
y = df["espacio_id"]

# 7. Entrenamos el modelo
modelo = RandomForestClassifier(n_estimators=100, random_state=42)
modelo.fit(X, y)

# 8. Guardamos el modelo
joblib.dump(modelo, "modelo.pkl")

# 9. Marcamos las reservas como ya utilizadas
ids_usados = df["id_reserva"].tolist()
marca_query = f"""
UPDATE reservas
SET usadaenmodelo = TRUE
WHERE id_reserva IN ({','.join(['%s'] * len(ids_usados))})
"""
cursor.execute(marca_query, ids_usados)

# 10. Cerramos la conexión
conexion.commit()
cursor.close()
conexion.close()

print("Modelo entrenado con nuevas reservas y guardado como 'modelo.pkl'")
