package br.com.cxinvest.service;

import br.com.cxinvest.dto.simulacao.SimulacaoHistoricoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoRequest;
import br.com.cxinvest.dto.simulacao.SimulacaoResponse;

import java.util.List;


public interface SimulacaoService {

    SimulacaoResponse simular(SimulacaoRequest simulacaoRequest);

    // Busca histórico paginado de simulações (sem filtro por cliente)
    List<SimulacaoHistoricoResponse> historico(int page, int size);

    // Retorna agregações por produto por dia com paginação
    List<SimulacaoProdutoDiaResponse> produtoDia(int page, int size);


}
