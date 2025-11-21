package br.com.cxinvest.service;

import br.com.cxinvest.dto.perfil.PerfilRiscoResponse;
import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.exception.ApiException;
import br.com.cxinvest.repository.ClienteRepository;
import br.com.cxinvest.repository.PerfilRepository;
import br.com.cxinvest.entity.Enum.FrequenciaInvestimento;
import br.com.cxinvest.entity.Enum.PreferenciaInvestimento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.math.BigDecimal;

/**
 * Serviço responsável pelas operações CRUD de Cliente e pela integração
 * com a lógica de definição e registro de perfis de investimento.
 * Boas práticas aplicadas:
 * - métodos transacionais para operações que alteram estado
 * - uso de Optional para atualização seletiva de campos
 * - delegação da lógica de definição de perfil ao PerfilService
 */
@ApplicationScoped
public class ClienteService {

    @Inject
    ClienteRepository repository;

    @Inject
    PerfilRepository perfilRepository;

    @Inject
    PerfilService perfilService;

    @Inject
    ClientePerfilService clientePerfilService;

    @Inject
    InvestimentoService investimentoService;

    /**
     * Lista todos os clientes.
     * @return lista de clientes (pode ser vazia)
     */
    public List<Cliente> listarTodos() {
        return repository.listAllClientes();
    }

    /**
     * Busca um cliente por id.
     * @param id identificador do cliente
     * @return Optional contendo o cliente caso exista
     */
    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findByIdOptional(id);
    }

    /**
     * Cria um cliente. Se o perfil não for informado, o perfil será
     * calculado automaticamente com base nos dados financeiros e atribuído.
     * Também registra um histórico de decisão de perfil.
     *
     * @param cliente entidade cliente (campos opcionais serão tratados com valores padrão)
     * @return o cliente persistido com "id" e perfil atualizados
     */
    @Transactional
    public Cliente criar(Cliente cliente) {

        var total = Optional.ofNullable(cliente.totalInvestido).orElse(java.math.BigDecimal.ZERO);
        var frequencia = Optional.ofNullable(cliente.frequenciaInvestimento).orElse(FrequenciaInvestimento.MEDIA);
        var preferencia = Optional.ofNullable(cliente.preferenciaInvestimento).orElse(PreferenciaInvestimento.LIQUIDEZ);

        cliente.totalInvestido = total;
        cliente.frequenciaInvestimento = frequencia;
        cliente.preferenciaInvestimento = preferencia;

        // resolver/validar perfil: se perfil informado, buscar no repo; senão calcular.
        cliente.perfilInvestimento = Optional.ofNullable(cliente.perfilInvestimento)
                .map(p -> Optional.ofNullable(p.id)
                        .flatMap(perfilRepository::findByIdOptional)
                        .orElseThrow(() -> new ApiException(404, "Perfil não encontrado: " + p.id)))
                .orElseGet(() -> perfilService.definirPerfilCliente(total, frequencia, preferencia));


        repository.persistCliente(cliente);


        clientePerfilService.aplicarDecisaoDePerfil(cliente, cliente.perfilInvestimento, "Criação de cliente", null);

        return cliente;
    }

    //TODO: Informar que o cliente pode passar o parametro totalInvestido como 0 e o sistema buscará o total investido na tabela de investimento - Documentar isso na API

    /**
     * Atualiza os dados de um cliente existente. Se o perfil for alterado,
     * registra um histórico de decisão de perfil.
     *
     * @param id identificador do cliente a ser atualizado
     * @param clienteAtualizado entidade cliente com dados atualizados
     * @return cliente atualizado
     */
    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente existente = repository.findById(id);
        if (existente == null) {
            throw new ApiException(404, "Cliente não encontrado: " + id);
        }


        // Atualiza apenas campos presentes
        Optional.ofNullable(clienteAtualizado.nome).ifPresent(n -> existente.nome = n);
        Optional.ofNullable(clienteAtualizado.email).ifPresent(e -> existente.email = e);
        // if totalInvestido is present and equals ZERO, fetch real total from investments
        Optional.ofNullable(clienteAtualizado.totalInvestido).ifPresent(t -> {
            if (BigDecimal.ZERO.compareTo(t) == 0) {
                // buscar total investido a partir da tabela de investimentos
                BigDecimal total = investimentoService.totalInvestidoPorCliente(id);
                existente.totalInvestido = total != null ? total : BigDecimal.ZERO;
            } else {
                existente.totalInvestido = t;
            }
        });
        Optional.ofNullable(clienteAtualizado.frequenciaInvestimento).ifPresent(f -> existente.frequenciaInvestimento = f);
        Optional.ofNullable(clienteAtualizado.preferenciaInvestimento).ifPresent(p -> existente.preferenciaInvestimento = p);


        var novoPerfil = Optional.ofNullable(clienteAtualizado.perfilInvestimento)
                .map(p -> Optional.ofNullable(p.id)
                        .flatMap(perfilRepository::findByIdOptional)
                        .orElseThrow(() -> new ApiException(404, "Perfil não encontrado: " + p.id)))
                .orElseGet(() -> perfilService.definirPerfilCliente(existente.totalInvestido, existente.frequenciaInvestimento, existente.preferenciaInvestimento));

        var existentePerfilId = Optional.ofNullable(existente.perfilInvestimento).map(p -> p.id).orElse(null);
        var novoPerfilId = Optional.ofNullable(novoPerfil).map(p -> p.id).orElse(null);

        // Se o perfil mudou, registrar histórico e atualizar; caso contrário apenas persistir
        if (!Objects.equals(existentePerfilId, novoPerfilId)) {
            clientePerfilService.aplicarDecisaoDePerfil(existente, novoPerfil, "Atualização de perfil via API", null);
        } else {
            repository.persist(existente);
        }

        return existente;
    }

    /**
     * Remove um cliente pelo "id". O cliente deve existir.
     *
     * @param id identificador do cliente a ser removido
     */
    @Transactional
    public void remover(Long id) {
        Cliente existente = repository.findById(id);
        if (existente == null) {
            throw new ApiException(404, "Cliente não encontrado: " + id);
        }
        // soft delete: marcar como inativo
        existente.status = 'I';
        repository.persist(existente);
    }


    /**
     * Obtém o perfil de risco de um cliente pelo seu ID.
     * @param clienteId identificador do cliente
     * @return PerfilRiscoResponse com as informações do perfil de risco
     */

    public PerfilRiscoResponse perfilRiscoCliente(Long clienteId){
        return repository.buscarPerfilRisco(clienteId)
                .orElseThrow(() -> new ApiException(404, "Cliente não encontrado: " + clienteId));
    }
}
