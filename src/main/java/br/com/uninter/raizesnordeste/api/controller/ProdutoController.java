package br.com.uninter.raizesnordeste.api.controller;

import br.com.uninter.raizesnordeste.application.dto.ProdutoEstoqueResponseDTO;
import br.com.uninter.raizesnordeste.application.service.ProdutoService;
import br.com.uninter.raizesnordeste.domain.entity.Produto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Cardápio e Produtos", description = "Endpoints de catálogo geral e consulta de estoque por filial")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @Operation(summary = "Listar produtos ativos", description = "Retorna o catálogo geral de itens da rede.")
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodosAtivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Obtém detalhes de um produto específico.")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping("/unidade/{unidadeId}")
    @Operation(summary = "Consultar cardápio da unidade com saldo de estoque",
            description = "Retorna os produtos disponíveis na filial especificada com suas respectivas quantidades em estoque.")
    public ResponseEntity<List<ProdutoEstoqueResponseDTO>> consultarCardapioPorUnidade(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(produtoService.consultarCardapioPorUnidade(unidadeId));
    }
}
