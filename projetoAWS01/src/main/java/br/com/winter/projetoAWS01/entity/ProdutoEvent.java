package br.com.winter.projetoAWS01.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoEvent {

    private Long produtoId;
    private String code;
    private String username;
}
