package br.com.uninter.raizesnordeste.api.controller;

import br.com.uninter.raizesnordeste.domain.entity.Unidade;
import br.com.uninter.raizesnordeste.domain.repository.UnidadeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades da Rede", description = "Endpoints para consulta de franquias e filiais ativas")
public class UnidadeController {

    private final UnidadeRepository unidadeRepository;

    @GetMapping
    @Operation(summary = "Listar unidades ativas", description = "Retorna todas as lojas da rede Raízes do Nordeste em operação.")
    public ResponseEntity<List<Unidade>> listarUnidades() {
        return ResponseEntity.ok(unidadeRepository.findByAtivaTrue());
    }
}
