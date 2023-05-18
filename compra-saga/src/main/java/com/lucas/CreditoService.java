package com.lucas;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Header;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CreditoService {

    private int creditoTotal;

    private Map<Long, Integer> pedidoValor = new HashMap<>();

    public CreditoService() {
        this.creditoTotal = 100;
    }

    public void doCredit(@Header("pedidoId") Long pedidoId, @Header("valor") int valor) {

        if (valor > this.creditoTotal) throw new IllegalStateException("Saldo insuficiente");

        creditoTotal -= valor;

        pedidoValor.put(pedidoId, valor);
    }

    public void undoCredit() {
        System.out.println("Pedido não foi realizado por falta de saldo");
    }
}
