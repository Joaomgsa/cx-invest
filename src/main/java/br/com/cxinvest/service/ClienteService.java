package br.com.cxinvest.service;

import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.repository.ClienteRepository;
import br.com.cxinvest.repository.PerfilRepository;
import br.com.cxinvest.entity.Enum.FrequenciaInvestimento;
import br.com.cxinvest.entity.Enum.PreferenciaInvestimento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

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

    public List<Cliente> listarTodos() {
        return repository.listAllClientes();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findByIdOptional(id);
    }

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
                        .orElseThrow(() -> new NotFoundException("Perfil não encontrado: " + p.id)))
                .orElseGet(() -> perfilService.definirPerfilCliente(total, frequencia, preferencia));


        repository.persistCliente(cliente);


        clientePerfilService.aplicarDecisaoDePerfil(cliente, cliente.perfilInvestimento, "Criação de cliente", null);

        return cliente;
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente existente = repository.findById(id);
        if (existente == null) {
            throw new NotFoundException("Cliente não encontrado: " + id);
        }

        // Atualiza apenas campos presentes (evita múltiplos ifs)
        Optional.ofNullable(clienteAtualizado.nome).ifPresent(n -> existente.nome = n);
        Optional.ofNullable(clienteAtualizado.email).ifPresent(e -> existente.email = e);
        Optional.ofNullable(clienteAtualizado.totalInvestido).ifPresent(t -> existente.totalInvestido = t);
        Optional.ofNullable(clienteAtualizado.frequenciaInvestimento).ifPresent(f -> existente.frequenciaInvestimento = f);
        Optional.ofNullable(clienteAtualizado.preferenciaInvestimento).ifPresent(p -> existente.preferenciaInvestimento = p);


        var novoPerfil = Optional.ofNullable(clienteAtualizado.perfilInvestimento)
                .map(p -> Optional.ofNullable(p.id)
                        .flatMap(perfilRepository::findByIdOptional)
                        .orElseThrow(() -> new NotFoundException("Perfil não encontrado: " + p.id)))
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

    @Transactional
    public void remover(Long id) {
        Cliente existente = repository.findById(id);
        if (existente == null) {
            throw new NotFoundException("Cliente não encontrado: " + id);
        }
        repository.removeById(id);
    }
}
