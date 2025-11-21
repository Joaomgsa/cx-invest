package br.com.cxinvest.service;

import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.repository.PerfilRepository;
import br.com.cxinvest.entity.Enum.FrequenciaInvestimento;
import br.com.cxinvest.entity.Enum.PreferenciaInvestimento;
import br.com.cxinvest.exception.ApiException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

/**
 * Serviço responsável por gerenciar perfis de investimento.
 *
 * Responsabilidades:
 * - operações CRUD básicas sobre a entidade Perfil
 * - definir o perfil de um cliente com base em regras de negócio
 *
 * Observações:
 * - em cenários reais recomenda-se implementar soft-delete (TODO na classe)
 *   para não perder histórico vinculado a produtos/cliente.
 */
@ApplicationScoped
public class PerfilService {

    private static final Logger LOG = Logger.getLogger(PerfilService.class);

    //TODO: adicionar soft delete para nao perder histórico de perfis vinculados a produtos e clientes
    @Inject
    PerfilRepository repository;

    /**
     * Retorna todos os perfis persistidos.
     * @return lista de perfis (pode ser vazia)
     */
    public List<Perfil> listarTodos() {
        return repository.listAllPerfis();
    }

    /**
     * Busca um perfil por identificador.
     * @param id identificador do perfil
     * @return Optional contendo o perfil caso exista
     */
    public Optional<Perfil> buscarPorId(Long id) {
        return repository.findByIdOptional(id);
    }

    /**
     * Persiste um novo perfil no banco.
     * @param perfil objeto Perfil a ser criado
     * @return o perfil persistido (com id gerado)
     */
    @Transactional
    public Perfil criar(Perfil perfil) {
        repository.persistPerfil(perfil);
        return perfil;
    }

    /**
     * Atualiza os dados de um perfil existente.
     * @param id identificador do perfil a ser atualizado
     * @param perfilAtualizado objeto contendo os novos valores
     * @return o perfil atualizado
     * @throws ApiException se o perfil não existir
     */
    @Transactional
    public Perfil atualizar(Long id, Perfil perfilAtualizado) {
        Optional<Perfil> existenteOpt = repository.findByIdOptional(id);
        if (existenteOpt.isEmpty()) {
            throw new ApiException(404, "Perfil não encontrado: " + id);
        }
        Perfil existente = existenteOpt.get();
        existente.nome = perfilAtualizado.nome;
        existente.pontuacao = perfilAtualizado.pontuacao;
        existente.descricao = perfilAtualizado.descricao;
        repository.persist(existente);
        return existente;
    }

    /**
     * Remove um perfil.
     * @param id identificador do perfil a remover
     * @throws ApiException se o perfil não existir
     */
    @Transactional
    public void remover(Long id) {
        Optional<Perfil> existenteOpt = repository.findByIdOptional(id);
        if (existenteOpt.isEmpty()) {
            throw new ApiException(404, "Perfil não encontrado: " + id);
        }
        repository.removeById(id);
    }

    /**
     * Calcula e retorna um Perfil adequado para um cliente com base no
     * total investido, frequência de investimento e preferência.
     *
     * Regras (resumidas):
     * - totalInvestido é avaliado contra thresholds (5k, 50k) para atribuir weight
     * - soma dos pesos (total + frequência + preferência) resulta em um score
     * - score <=1 -> CONSERVADOR, <=3 -> MODERADO, >3 -> AGRESSIVO
     *
     * @param totalInvestido valor total investido pelo cliente (pode ser null)
     * @param frequencia frequência de investimento (pode ser null, usa MEDIA)
     * @param preferencia preferência de investimento (pode ser null, usa LIQUIDEZ)
     * @return Perfil encontrado correspondente às regras
     * @throws ApiException se não houver perfil cadastrado com o nome calculado
     */
    public Perfil definirPerfilCliente(BigDecimal totalInvestido, FrequenciaInvestimento frequencia, PreferenciaInvestimento preferencia) {
        final var tv = (totalInvestido == null) ? BigDecimal.ZERO : totalInvestido;


        record Threshold(BigDecimal limite, int weight) {}
        var thresholds = List.of(
                new Threshold(new BigDecimal("500000"), 2),
                new Threshold(new BigDecimal("50000"), 1)
        );

        int totalInvestidoWeight = thresholds.stream()
                .filter(t -> tv.compareTo(t.limite()) >= 0)
                .mapToInt(Threshold::weight)
                .findFirst()
                .orElse(0);

        int freqWeight = (frequencia == null ? FrequenciaInvestimento.MEDIA : frequencia).weight();
        int prefWeight = (preferencia == null ? PreferenciaInvestimento.LIQUIDEZ : preferencia).weight();

        int score = totalInvestidoWeight + freqWeight + prefWeight;

        String perfilNome = score <= 1 ? "CONSERVADOR" : score <= 3 ? "MODERADO" : "AGRESSIVO";

        Optional<Perfil> perfilOpt = repository.find("nome", perfilNome).firstResultOptional();
        return perfilOpt.orElseThrow(() -> new ApiException(404, "Perfil não encontrado: " + perfilNome));
    }
}
