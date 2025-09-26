package br.com.winter.projetoAWS02.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoEvent {
    private long produtoId;
    private String code;
    private String username;
}
