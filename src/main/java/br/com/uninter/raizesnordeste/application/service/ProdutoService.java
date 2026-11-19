package br.com.uninter.raizesnordeste.application.service;

import br.com.uninter.raizesnordeste.application.dto.ProdutoEstoqueResponseDTO;
import br.com.uninter.raizesnordeste.domain.entity.EstoqueUnidade;
import br.com.uninter.raizesnordeste.domain.entity.Produto;
import br.com.uninter.raizesnordeste.domain.repository.EstoqueUnidadeRepository;
import br.com.uninter.raizesnordeste.domain.repository.ProdutoRepository;
import br.com.uninter.raizesnordeste.domain.repository.UnidadeRepository;
import br.com.uninter.raizesnordeste.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueUnidadeRepository estoqueUnidadeRepository;
    private final UnidadeRepository unidadeRepository;

    @Transactional(readOnly = true)
    public List<Produto> listarTodosAtivos() {
        return produtoRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProdutoEstoqueResponseDTO> consultarCardapioPorUnidade(Long unidadeId) {
        if (!unidadeRepository.existsById(unidadeId)) {
            throw new ResourceNotFoundException("Unidade não encontrada com o ID: " + unidadeId);
        }

        List<EstoqueUnidade> estoques = estoqueUnidadeRepository.findByUnidadeId(unidadeId);

        return estoques.stream()
                .map(e -> new ProdutoEstoqueResponseDTO(
                        e.getProduto().getId(),
                        e.getProduto().getNome(),
                        e.getProduto().getDescricao(),
                        e.getProduto().getPreco(),
                        e.getProduto().getCategoria(),
                        e.getQuantidadeDisponivel()
                ))
                .collect(Collectors.toList());
    }
}
