package com.lucas;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.CamelSagaService;
import org.apache.camel.saga.InMemorySagaService;

@ApplicationScoped
public class SagaRoute extends RouteBuilder {

    @Inject
    private PedidoService pedidoService;

    @Inject
    private CreditoService creditoService;

    @Override
    public void configure() throws Exception {
        CamelSagaService sagaService = new InMemorySagaService();
        this.getContext().addService(sagaService);

        //Saga
        this.from("direct:saga").saga().propagation(SagaPropagation.REQUIRES_NEW).log("Iniciando transação")
                .to("direct:doOrder").log("Criando novo pedido")
                .to("direct:doCredit").log("Reservando o crédito")
                .to("direct:finaliza").log("Feito");

        //Pedido Service
        this.from("direct:doOrder").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:undoOrder")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(pedidoService, "doOrder").log("Pedido ${body} criado");
        this.from("direct:undoOrder").transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(pedidoService, "undoOrder").log("Pedido ${body} cancelado");

        //Credito Service
        this.from("direct:doCredit").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:undoCredit")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditoService, "doCredit").log("Credito do pedido ${header.pedidoId} no valor de ${header.valor} reservado para a saga ${body}");
        this.from("direct:undoCredit").transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditoService, "undoCredit").log("O pedido ${body} não foi realizado por falta de saldo");

        //Finaliza
        this.from("direct:finaliza").saga().propagation(SagaPropagation.MANDATORY)
                .choice()
                .end();
    }
}
