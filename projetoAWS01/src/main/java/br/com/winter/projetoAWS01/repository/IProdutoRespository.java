package br.com.winter.projetoAWS01.repository;

import br.com.winter.projetoAWS01.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProdutoRespository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByNome(String nome);
    Optional<Produto> findByCodigo(String codigo);
}
