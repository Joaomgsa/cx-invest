package br.com.cxinvest.service;

import br.com.cxinvest.dto.InvestimentoResponse;
import br.com.cxinvest.entity.Investimento;
import br.com.cxinvest.repository.InvestimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class InvestimentoService {

    @Inject
    InvestimentoRepository investimentoRepository;

    @Inject
    TelemetriaService telemetriaService;

    public List<InvestimentoResponse> listarInvestimentos(Long clienteId) {
        long startTime = System.currentTimeMillis();
        
        try {
            List<Investimento> investimentos = investimentoRepository.findByClienteId(clienteId);
            
            return investimentos.stream()
                    .map(this::toInvestimentoResponse)
                    .collect(Collectors.toList());
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            telemetriaService.registrar("investimentos", duration);
        }
    }

    private InvestimentoResponse toInvestimentoResponse(Investimento investimento) {
        InvestimentoResponse response = new InvestimentoResponse();
        response.setId(investimento.getId());
        response.setTipo(investimento.getTipo());
        response.setValor(investimento.getValor());
        response.setRentabilidade(investimento.getRentabilidade());
        response.setData(investimento.getData());
        return response;
    }
}
