package com.doctorpc.apirest.apirest.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doctorpc.apirest.apirest.Entities.Ventas;
import com.doctorpc.apirest.apirest.Repository.VentasRepository;

@Service
public class VentasService {

    @Autowired
    VentasRepository ventasRepository;

    public List<Ventas> obtenerVentasPorMesYear(int mes, int anio){
        return ventasRepository.findByMesYear(mes, anio);
        
    }

}
