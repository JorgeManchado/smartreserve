package com.gestion.reservas.service;

import com.gestion.reservas.dto.*;
import com.gestion.reservas.entity.Espacio;
import com.gestion.reservas.entity.Equipamiento;
import com.gestion.reservas.entity.EstadoEspacio;
import com.gestion.reservas.entity.TipoEspacio;
import com.gestion.reservas.repository.EquipamientoRepository;
import com.gestion.reservas.repository.EspacioRepository;
import com.gestion.reservas.repository.EstadoEspacioRepository;
import com.gestion.reservas.repository.TipoEspacioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class EspacioServiceImpl implements EspacioService {

    private final EspacioRepository espacioRepo;
    private final TipoEspacioRepository tipoRepo;
    private final EstadoEspacioRepository estadoRepo;
    private final EquipamientoRepository equipamientoRepo;

    @Value("${upload.dir}")
    private String uploadDir;


    @Override
    public List<EspacioResponseDTO> findAll() {
        return espacioRepo.findAllByOrderByIdEspacioDesc().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public EspacioResponseDTO findById(Long id) {
        Espacio espacio = espacioRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Espacio no encontrado"));
        return toDTO(espacio);
    }





    public EspacioResponseDTO create(String nombre, int capacidad, String ubicacion,
                                   String descripcion, Long idTipoEspacio, Long idEstado,
                                   List<Long> equipamientos, MultipartFile imagen) {
        Espacio espacio = new Espacio();
        espacio.setNombre(nombre);
        espacio.setCapacidad(capacidad);
        espacio.setUbicacion(ubicacion);
        espacio.setDescripcion(descripcion);

        TipoEspacio tipo = tipoRepo.findById(idTipoEspacio).orElseThrow();
        EstadoEspacio estado = estadoRepo.findById(idEstado).orElseThrow();

        espacio.setTipoEspacio(tipo);
        espacio.setEstado(estado);

        if (equipamientos != null && !equipamientos.isEmpty()) {
            List<Equipamiento> lista = equipamientoRepo.findAllById(equipamientos);
            espacio.setEquipamientos(lista);
        }

        if (imagen != null && !imagen.isEmpty()) {

            String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
            Path ruta = Paths.get("uploads", nombreArchivo);
            try {
                Files.createDirectories(ruta.getParent());
                imagen.transferTo(ruta);
                espacio.setImagen(nombreArchivo); // guarda solo el nombre
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen", e);
            }
        }

        Espacio guardado = espacioRepo.save(espacio);
        return toDTO(guardado);
    }


    public EspacioResponseDTO update(
            Long id,
            String nombre,
            int capacidad,
            String ubicacion,
            String descripcion,
            Long idTipoEspacio,
            Long idEstado,
            List<Long> equipamientos,
            MultipartFile imagen
    ) {

        Espacio espacio = espacioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Espacio no encontrado con ID: " + id));


        espacio.setNombre(nombre);
        espacio.setCapacidad(capacidad);
        espacio.setUbicacion(ubicacion);
        espacio.setDescripcion(descripcion);


        espacio.setTipoEspacio(tipoRepo.findById(idTipoEspacio)
                .orElseThrow(() -> new RuntimeException("Tipo de espacio no encontrado")));

        espacio.setEstado(estadoRepo.findById(idEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado")));

        List<Equipamiento> equipamientoList = equipamientoRepo.findAllById(equipamientos);
        espacio.setEquipamientos(equipamientoList);


        if (imagen != null && !imagen.isEmpty()) {
            String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
            Path ruta = Paths.get("uploads", nombreArchivo);
            try {
                Files.createDirectories(ruta.getParent()); // Asegura carpeta
                imagen.transferTo(ruta); // Guarda archivo
                espacio.setImagen(nombreArchivo); // Solo el nombre se guarda en la entidad
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen", e);
            }
        }


        espacioRepo.save(espacio);


        return toDTO(espacio);
    }


    @Override
    public void delete(Long id) {
        Optional<Espacio> optionalEspacio = espacioRepo.findById(id);
        if (optionalEspacio.isPresent()) {
            Espacio espacio = optionalEspacio.get();


            String nombreImagen = espacio.getImagen();
            if (nombreImagen != null && !nombreImagen.isBlank()) {
                Path rutaImagen = Paths.get(uploadDir).resolve(nombreImagen);
                try {
                    Files.deleteIfExists(rutaImagen);
                    System.out.println("Imagen eliminada: " + rutaImagen);
                } catch (IOException e) {
                    System.err.println("Error al eliminar imagen: " + e.getMessage());
                }
            }


            espacioRepo.deleteById(id);
        }
    }

    private Espacio toEntity(EspacioRequestDTO dto) {
        return Espacio.builder()
                .nombre(dto.getNombre())
                .capacidad(dto.getCapacidad())
                .ubicacion(dto.getUbicacion())
                .descripcion(dto.getDescripcion())
                .imagen(dto.getImagen())
                .tipoEspacio(tipoRepo.findById(dto.getIdTipoEspacio())
                        .orElseThrow(() -> new IllegalArgumentException("Tipo espacio no válido")))
                .estado(estadoRepo.findById(dto.getIdEstado())
                        .orElseThrow(() -> new IllegalArgumentException("Estado no válido")))
                .equipamientos(
                        dto.getEquipamientos().stream()
                                .map(id -> equipamientoRepo.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException("Equipamiento no válido")))
                                .collect(Collectors.toList()))
                .build();
    }



    public EspacioResponseDTO toDTO(Espacio espacio) {
        EspacioResponseDTO dto = new EspacioResponseDTO();

        dto.setIdEspacio(espacio.getIdEspacio());
        dto.setNombre(espacio.getNombre());
        dto.setCapacidad(espacio.getCapacidad());
        dto.setUbicacion(espacio.getUbicacion());
        dto.setDescripcion(espacio.getDescripcion());
        dto.setImagen(espacio.getImagen());

        TipoEspacioDTO tipoDto = new TipoEspacioDTO();
        tipoDto.setIdTipoEspacio(espacio.getTipoEspacio().getIdTipoEspacio());
        tipoDto.setDescripcion(espacio.getTipoEspacio().getDescripcion());
        dto.setTipo(tipoDto);

        EstadoEspacioDTO estadoDto = new EstadoEspacioDTO();
        estadoDto.setIdEstado(espacio.getEstado().getIdEstado());
        estadoDto.setDescripcion(espacio.getEstado().getDescripcion());
        dto.setEstado(estadoDto);

        List<EquipamientoDTO> equipamientoDTOs = espacio.getEquipamientos().stream().map(eq -> {
            EquipamientoDTO e = new EquipamientoDTO();
            e.setIdEquipamiento(eq.getIdEquipamiento());
            e.setDescripcion(eq.getDescripcion());
            return e;
        }).toList();
        dto.setEquipamientos(equipamientoDTOs);

        return dto;
    }

    public void updateEstado(Long idEspacio, Long idEstado) {
        Espacio espacio = espacioRepo.findById(idEspacio)
                .orElseThrow(() -> new NoSuchElementException("Espacio no encontrado"));

        EstadoEspacio nuevoEstado = estadoRepo.findById(idEstado)
                .orElseThrow(() -> new NoSuchElementException("Estado no encontrado"));

        espacio.setEstado(nuevoEstado);
        espacioRepo.save(espacio);
    }


}

