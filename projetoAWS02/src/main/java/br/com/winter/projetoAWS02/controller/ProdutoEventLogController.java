package br.com.winter.projetoAWS02.controller;

import br.com.winter.projetoAWS02.model.ProdutoEventLogDto;
import br.com.winter.projetoAWS02.repository.ProdutoEventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class ProdutoEventLogController {

    private ProdutoEventLogRepository produtoEventLogRepository;

    @Autowired
    public ProdutoEventLogController(ProdutoEventLogRepository produtoEventLogRepository) {
        this.produtoEventLogRepository = produtoEventLogRepository;
    }

    @GetMapping("/events")
    public List<ProdutoEventLogDto> getAllEvents() {
        return StreamSupport
                .stream(produtoEventLogRepository.findAll().spliterator(), false)
                .map(ProdutoEventLogDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}")
    public List<ProdutoEventLogDto> findByCode(@PathVariable String code) {
        return produtoEventLogRepository.findAllByPk(code)
                .stream()
                .map(ProdutoEventLogDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}/{event}")
    public List<ProdutoEventLogDto> findByCodeAndEventType(@PathVariable String code,
                                                           @PathVariable String event) {
        return produtoEventLogRepository.findAllByPkAndSkStartsWith(code, event)
                .stream()
                .map(ProdutoEventLogDto::new)
                .collect(Collectors.toList());
    }
}

















