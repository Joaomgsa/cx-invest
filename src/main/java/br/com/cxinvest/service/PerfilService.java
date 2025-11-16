package br.com.cxinvest.service;

import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.repository.PerfilRepository;
import br.com.cxinvest.entity.Enum.FrequenciaInvestimento;
import br.com.cxinvest.entity.Enum.PreferenciaInvestimento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@ApplicationScoped
public class PerfilService {

    @Inject
    PerfilRepository repository;

    public List<Perfil> listarTodos() {
        return repository.listAllPerfis();
    }

    public Optional<Perfil> buscarPorId(Long id) {
        return repository.findByIdOptional(id);
    }

    @Transactional
    public Perfil criar(Perfil perfil) {
        repository.persistPerfil(perfil);
        return perfil;
    }

    @Transactional
    public Perfil atualizar(Long id, Perfil perfilAtualizado) {
        Optional<Perfil> existenteOpt = repository.findByIdOptional(id);
        if (existenteOpt.isEmpty()) {
            throw new NotFoundException("Perfil não encontrado: " + id);
        }
        Perfil existente = existenteOpt.get();
        existente.nome = perfilAtualizado.nome;
        existente.pontuacao = perfilAtualizado.pontuacao;
        existente.descricao = perfilAtualizado.descricao;
        repository.persist(existente);
        return existente;
    }

    @Transactional
    public void remover(Long id) {
        Optional<Perfil> existenteOpt = repository.findByIdOptional(id);
        if (existenteOpt.isEmpty()) {
            throw new NotFoundException("Perfil não encontrado: " + id);
        }
        repository.removeById(id);
    }


    public Perfil definirPerfilCliente(BigDecimal totalInvestido, FrequenciaInvestimento frequencia, PreferenciaInvestimento preferencia) {
        final var tv = (totalInvestido == null) ? BigDecimal.ZERO : totalInvestido;

        // calcular peso do totalInvestido usando lista ordenada de thresholds
        record Threshold(BigDecimal limite, int weight) {}
        var thresholds = List.of(
                new Threshold(new BigDecimal("3000"), 2),
                new Threshold(new BigDecimal("1000"), 1)
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
        return perfilOpt.orElseThrow(() -> new NotFoundException("Perfil não encontrado: " + perfilNome));
    }
}
