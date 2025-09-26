package br.com.winter.projetoAWS01.usecase;

import br.com.winter.projetoAWS01.entity.Produto;
import br.com.winter.projetoAWS01.repository.IProdutoRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarUsecase {

    private final IProdutoRespository produtoRespository;

    @Autowired
    public BuscarUsecase(IProdutoRespository produtoRespository) {
        this.produtoRespository = produtoRespository;
    }

    public Page<Produto> buscarTodos(Pageable pageable) {
        return produtoRespository.findAll(pageable);
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRespository.findById(id);
    }

    public Optional<Produto> buscarPorNome(String nome) {
        return produtoRespository.findByNome(nome);
    }

    public Optional<Produto> buscarProCodigo(String codigo) {
        return produtoRespository.findByCodigo(codigo);
    }
}
