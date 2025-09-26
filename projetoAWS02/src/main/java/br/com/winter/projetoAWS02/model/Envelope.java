package br.com.winter.projetoAWS02.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.winter.projetoAWS02.enums.EventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Envelope {

    @JsonProperty("eventType")
    private EventType eventType;

    @JsonProperty("messageId")
    private String messageId;

    @JsonProperty("data")
    private String data;
}