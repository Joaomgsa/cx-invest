package br.com.cxinvest.service;

import br.com.cxinvest.dto.simulacao.SimulacaoHistoricoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoRequest;
import br.com.cxinvest.dto.simulacao.SimulacaoResponse;

import java.util.List;


public interface SimulacaoService {

    SimulacaoResponse simular(SimulacaoRequest simulacaoRequest);

    SimulacaoHistoricoResponse historico(SimulacaoRequest simulacaoRequest);

    List<SimulacaoProdutoDiaResponse> produtoDia();


}



