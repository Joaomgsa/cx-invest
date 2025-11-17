package br.com.cxinvest.service;

import br.com.cxinvest.dto.simulacao.SimulacaoHistoricoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoRequest;
import br.com.cxinvest.dto.simulacao.SimulacaoResponse;
import br.com.cxinvest.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class SimulacaoServiceImpl implements SimulacaoService {


    //TODO : Implementar a lógica real do SimulacaoService (preencher DTOs e regras de negócio).
    @Inject
    SimulacaoRepository repository;

    @Override
    public SimulacaoResponse simular(SimulacaoRequest simulacaoRequest) {
        // chama repositório apenas para utilizar consultas; lógica de simulação não implementada
        if (simulacaoRequest != null && simulacaoRequest.tipoProduto() != null) {
            repository.findProdutosByTipo(simulacaoRequest.tipoProduto());
        }
        return null;
    }

    @Override
    public SimulacaoHistoricoResponse historico(SimulacaoRequest simulacaoRequest) {
        if (simulacaoRequest != null) {
            repository.buscarHistoricoPerfilDoCliente(simulacaoRequest.clienteId());
        }
        return null;
    }

    @Override
    public List<SimulacaoProdutoDiaResponse> produtoDia() {
        List<SimulacaoProdutoDiaResponse> produtoDiaInfo = repository.produtoDiaInfo();
        return produtoDiaInfo;
    }
}
