package br.com.winter.projetoAWS01.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "codigo", unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "nome")
    private String nome;

    @NotNull
    @Column(name = "preco")
    private Double preco;

}
