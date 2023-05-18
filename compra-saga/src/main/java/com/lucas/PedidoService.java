package com.lucas;


import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Header;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class PedidoService {

    private Set<Long> pedidos = new HashSet<>();

    public void doOrder(@Header("id") Long id) {
        pedidos.add(id);
    }

    public void undoOrder(@Header("id") Long id) {
        pedidos.remove(id);
    }
}
