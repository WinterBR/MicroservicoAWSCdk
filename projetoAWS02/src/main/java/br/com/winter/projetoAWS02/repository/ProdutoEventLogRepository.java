package br.com.winter.projetoAWS02.repository;

import br.com.winter.projetoAWS02.model.ProdutoEventLog;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoEventLogRepository {

    private final DynamoDbTable<ProdutoEventLog> table;

    public ProdutoEventLogRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("produto-events", TableSchema.fromBean(ProdutoEventLog.class));
    }

    public void save(ProdutoEventLog eventLog) {
        table.putItem(eventLog);
    }

    public ProdutoEventLog findById(String pk, String sk) {
        return table.getItem(r -> r.key(k -> k.partitionValue(pk).sortValue(sk)));
    }

    public List<ProdutoEventLog> findAll() {
        List<ProdutoEventLog> results = new ArrayList<>();
        table.scan().items().forEach(results::add);
        return results;
    }

    // Buscar todos os eventos por PK (partition key)
    public List<ProdutoEventLog> findAllByPk(String pk) {
        List<ProdutoEventLog> results = new ArrayList<>();
        Key key = Key.builder().partitionValue(pk).build();

        table.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)))
                .items()
                .forEach(results::add);

        return results;
    }

    // Buscar todos os eventos por PK e prefixo de SK (sort key começa com...)
    public List<ProdutoEventLog> findAllByPkAndSkStartsWith(String pk, String skPrefix) {
        List<ProdutoEventLog> results = new ArrayList<>();

        table.query(r -> r.queryConditional(
                        QueryConditional.sortBeginsWith(
                                Key.builder().partitionValue(pk).sortValue(skPrefix).build()
                        )))
                .items()
                .forEach(results::add);

        return results;
    }
}
