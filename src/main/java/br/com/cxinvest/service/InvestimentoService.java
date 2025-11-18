package br.com.cxinvest.service;

import br.com.cxinvest.dto.cliente.HistoricoInvestimentoResponse;
import br.com.cxinvest.entity.Investimento;
import br.com.cxinvest.repository.InvestimentoRepository;
import br.com.cxinvest.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class InvestimentoService {

    @Inject
    InvestimentoRepository repository;

    @Inject
    ClienteRepository clienteRepository;

    /**
     * Retorna histórico de investimentos de um cliente paginado e ordenado por data.
     * Valida existência do cliente e lança NotFoundException (404) quando não encontrado.
     * @param clienteId id do cliente
     * @param page número da página (0-based)
     * @param size tamanho da página
     * @param asc true para ordem crescente por data, false para decrescente
     * @return lista de HistoricoInvestimentoResponse
     */
    public List<HistoricoInvestimentoResponse> listarHistoricoInvestimentosCliente(Long clienteId, int page, int size, boolean asc) {
        if (Objects.isNull(clienteId)) {
            throw new NotFoundException("Cliente não encontrado: null");
        }
        var clienteOpt = clienteRepository.findByIdOptional(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new NotFoundException("Cliente não encontrado: " + clienteId);
        }

        List<Investimento> lista = repository.listarPorCliente(clienteId, page, size, asc);
        return lista.stream().map(i -> {
            // obter data de forma compatível: preferir campo OffsetDateTime, fallback para getter Instant
            String data;
            if (i.dataInvestimento != null) {
                data = i.dataInvestimento.toString();
            } else {
                Instant inst = null;
                try {
                    inst = i.getDataInvestimento();
                } catch (NoSuchMethodError e) {
                    inst = null; // compatibilidade binária: se não existir getter, ignora
                }
                data = inst != null ? inst.toString() : null;
            }
            return new HistoricoInvestimentoResponse(
                    i.id,
                    i.produto != null ? i.produto.tipo : null,
                    i.valorInvestido,
                    i.rentabilidade,
                    data
            );
        }).toList();
    }

    // método existente mantido para compatibilidade
    public List<HistoricoInvestimentoResponse> historicoInvestimentos(Long clienteId, int page, int size, boolean asc) {
        return listarHistoricoInvestimentosCliente(clienteId, page, size, asc);
    }
}
