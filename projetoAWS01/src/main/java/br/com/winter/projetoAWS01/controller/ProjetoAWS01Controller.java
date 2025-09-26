package br.com.winter.projetoAWS01.controller;

import br.com.winter.projetoAWS01.entity.Produto;
import br.com.winter.projetoAWS01.enums.EventType;
import br.com.winter.projetoAWS01.service.ProdutoPublisher;
import br.com.winter.projetoAWS01.usecase.BuscarUsecase;
import br.com.winter.projetoAWS01.usecase.CriarUsecase;
import br.com.winter.projetoAWS01.usecase.DeletarUsecase;
import br.com.winter.projetoAWS01.usecase.EditarUsecase;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/produto")
public class ProjetoAWS01Controller {

    private static final Logger LOG = LoggerFactory.getLogger(ProjetoAWS01Controller.class);
    private final BuscarUsecase buscarUsecase;
    private final CriarUsecase criarUsecase;
    private final DeletarUsecase deletarUsecase;
    private final EditarUsecase editarUsecase;
    private final ProdutoPublisher produtoPublisher;

    @Autowired
    public ProjetoAWS01Controller(
            BuscarUsecase buscarUsecase,
            CriarUsecase criarUsecase,
            DeletarUsecase deletarUsecase,
            EditarUsecase editarUsecase,
            ProdutoPublisher produtoPublisher
    ) {
        this.buscarUsecase = buscarUsecase;
        this.criarUsecase = criarUsecase;
        this.deletarUsecase = deletarUsecase;
        this.editarUsecase = editarUsecase;
        this.produtoPublisher = produtoPublisher;
    }

    @GetMapping
    public ResponseEntity<Page<Produto>> buscarTodos(Pageable pageable) {
        Page<Produto> produtos = buscarUsecase.buscarTodos(pageable);
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        LOG.info("id: " + id);
        return buscarUsecase.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable String codigo) {
        LOG.info("codigo: " + codigo);
        return buscarUsecase.buscarProCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody @Valid Produto produto) {
        LOG.info("produto: " + produto);
        Produto criado = criarUsecase.criarProduto(produto);

        produtoPublisher.publishProdutoEvent(criado, EventType.PRODUCT_CREATED, "Winter1");
        URI uri = URI.create("/produto/id/" + criado.getId());
        return ResponseEntity.created(uri).body(criado); // 201 Created
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Produto> editarProduto(@PathVariable Long id, @RequestBody @Valid Produto produto) {
        LOG.info("id: " + id);
        LOG.info("produto: " + produto);
        Produto criado = produto;

        produtoPublisher.publishProdutoEvent(criado, EventType.PRODUCT_UPDATE, "Winter2");
        return Optional.ofNullable(editarUsecase.editarProduto(id, produto))
                .map(ResponseEntity::ok) // 200 OK
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deletarProdutoId(@PathVariable Long id) {
        LOG.info("id: " + id);
        Produto criado = buscarUsecase.buscarPorId(id).get();
        produtoPublisher.publishProdutoEvent(criado, EventType.PRODUCT_DELETED, "Winter3");
        boolean deletado = deletarUsecase.deletarPorId(id);

        if (deletado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @DeleteMapping("/codigo/{codigo}")
    public ResponseEntity<Void> deletarProdutoCodigo(@PathVariable String codigo) {
        LOG.info("codigo: " + codigo);
        Produto criado = buscarUsecase.buscarProCodigo(codigo).get();
        produtoPublisher.publishProdutoEvent(criado, EventType.PRODUCT_DELETED, "Winter3");
        boolean deletado = deletarUsecase.deletarPorCodigo(codigo);

        if (deletado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
