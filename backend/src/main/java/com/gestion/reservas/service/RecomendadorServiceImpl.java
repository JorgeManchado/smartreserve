package com.gestion.reservas.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.reservas.dto.RecomendacionResponseDTO;
import com.gestion.reservas.service.RecomendadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecomendadorServiceImpl implements RecomendadorService {

    private final Environment env;

    @Override
    public RecomendacionResponseDTO obtenerRecomendaciones(Long usuarioId, int diaSemana, int hora) throws Exception {
        String scriptPath = env.getProperty("python.script.predictor");
        String pythonExecutable = env.getProperty("python.executable", "python");

        System.out.println("Working dir: " + System.getProperty("user.dir"));

        ProcessBuilder pb = new ProcessBuilder(
                pythonExecutable, scriptPath,
                String.valueOf(usuarioId),
                String.valueOf(diaSemana),
                String.valueOf(hora)
        );


        pb.directory(new File(System.getProperty("user.dir")));

        Map<String, String> envs = pb.environment();
        envs.put("DB_HOST", env.getProperty("python.db.host"));
        envs.put("DB_PORT", env.getProperty("python.db.port"));
        envs.put("DB_NAME", env.getProperty("python.db.name"));
        envs.put("DB_USER", env.getProperty("python.db.user"));
        envs.put("DB_PASSWORD", env.getProperty("python.db.password"));

        Process process = pb.start();

        // Salida estándar (stdout)
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }


        // Salida de errores (stderr)
        BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream())
        );
        StringBuilder errorOutput = new StringBuilder();
        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
            errorOutput.append(errorLine).append("\n");
        }


        // Esperamos a que termine el proceso
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            System.out.println("Error en predictor.py:");
            System.out.println(errorOutput.toString());
            throw new RuntimeException("Error al ejecutar predictor.py:\n" + errorOutput);
        }

         // Parseamos el JSON de salida del script a objeto Java
        ObjectMapper mapper = new ObjectMapper();
         return mapper.readValue(output.toString().trim(), RecomendacionResponseDTO.class);
    }

    @Override
    public String entrenarModelo() throws Exception {
        String scriptPath = env.getProperty("python.script.modelo");
        String pythonExecutable = env.getProperty("python.executable", "python");

        System.out.println("Working dir: " + System.getProperty("user.dir"));

        // Creamos el proceso
        ProcessBuilder pb = new ProcessBuilder(pythonExecutable, scriptPath);
        pb.directory(new File(System.getProperty("user.dir")));

        Map<String, String> envs = pb.environment();
        envs.put("DB_HOST", env.getProperty("python.db.host"));
        envs.put("DB_PORT", env.getProperty("python.db.port"));
        envs.put("DB_NAME", env.getProperty("python.db.name"));
        envs.put("DB_USER", env.getProperty("python.db.user"));
        envs.put("DB_PASSWORD", env.getProperty("python.db.password"));

        Process process = pb.start();

        // Lectura de la salida estándar (stdout)
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Lectura de errores (stderr)
        BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
            errorOutput.append(errorLine).append("\n");
        }

        // Esperamos a que termine
        int exitCode = process.waitFor();

        // Mostramos errores por si falla
        if (exitCode != 0) {
            System.out.println(errorOutput.toString());
            throw new RuntimeException("Error al ejecutar modelo_recomendador.py:\n" + errorOutput);
        }

        return output.toString().trim();
    }

    @Scheduled(cron = "0 */2 * * * *") // cada 2 minutos pruebas
    //@Scheduled(cron = "0 0 2 * * *") // 2:00:00 todos los días
    public void reentrenarAutomáticamente() {
        try {
            System.out.println("[SCHEDULED] Entrenamiento automático iniciado");
            this.entrenarModelo();
            System.out.println("[SCHEDULED] Entrenamiento automático completado");
        } catch (Exception e) {
            System.err.println("[SCHEDULED] Fallo al reentrenar modelo:");
            e.printStackTrace();
        }
    }

}
