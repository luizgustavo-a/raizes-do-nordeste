package br.com.uninter.raizesnordeste.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProdutoEstoqueResponseDTO implements Serializable {

    private Long produtoId;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private Integer quantidadeEstoque;
    private Boolean disponivel;

    public ProdutoEstoqueResponseDTO(Long produtoId, String nome, String descricao, BigDecimal preco,
                                     String categoria, Integer quantidadeEstoque) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.quantidadeEstoque = quantidadeEstoque;
        this.disponivel = quantidadeEstoque != null && quantidadeEstoque > 0;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
        this.disponivel = quantidadeEstoque != null && quantidadeEstoque > 0;
    }
}
