package br.com.winter.projetoAWS01.usecase;

import br.com.winter.projetoAWS01.entity.Produto;
import br.com.winter.projetoAWS01.repository.IProdutoRespository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EditarUsecase {

    private final IProdutoRespository produtoRespository;

    @Autowired
    public EditarUsecase(IProdutoRespository produtoRespository) {
        this.produtoRespository = produtoRespository;
    }

    public Produto editarProduto(Long id, @Valid Produto produto) {
        Optional<Produto> produtoExistenteOp = produtoRespository.findById(id);
        if (produtoExistenteOp.isPresent()) {
            Produto produtoExistente = produtoExistenteOp.get();
            produtoExistente.setNome(produto.getNome());
            produtoExistente.setPreco(produto.getPreco());
            produtoRespository.save(produto);
            return produtoExistente;
        }
        return null;
    }
}
