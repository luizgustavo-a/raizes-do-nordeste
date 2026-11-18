package br.com.uninter.raizesnordeste.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProdutoEstoqueResponseDTO implements Serializable {

    private Long produtoId;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private Integer quantidadeEstoque;
    private Boolean disponivel;

    public ProdutoEstoqueResponseDTO() {
    }

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

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
        this.disponivel = quantidadeEstoque != null && quantidadeEstoque > 0;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }
}
