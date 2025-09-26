package br.com.winter.projetoAWS01.usecase;

import br.com.winter.projetoAWS01.entity.Produto;
import br.com.winter.projetoAWS01.repository.IProdutoRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeletarUsecase {

    private final IProdutoRespository produtoRespository;

    @Autowired
    public DeletarUsecase(IProdutoRespository produtoRespository) {
        this.produtoRespository = produtoRespository;
    }

    public boolean deletarPorId(Long id) {
        if (produtoRespository.existsById(id)) {
            produtoRespository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean deletarPorCodigo(String codigo) {
        Optional<Produto> produtoExistenteOp = produtoRespository.findByCodigo(codigo);
        if (produtoExistenteOp.isPresent()) {
            produtoRespository.delete(produtoExistenteOp.get());
            return true;
        }
        return false;
    }
}
