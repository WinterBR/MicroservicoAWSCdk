package br.com.winter.projetoAWS01.usecase;

import br.com.winter.projetoAWS01.entity.Produto;
import br.com.winter.projetoAWS01.repository.IProdutoRespository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CriarUsecase {

    private final IProdutoRespository produtoRespository;

    @Autowired
    public CriarUsecase(IProdutoRespository produtoRespository) {
        this.produtoRespository = produtoRespository;
    }

    public Produto criarProduto(@Valid Produto produto) {
        return produtoRespository.save(produto);
    }
}
