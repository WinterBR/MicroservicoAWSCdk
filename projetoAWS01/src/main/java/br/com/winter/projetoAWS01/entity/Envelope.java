package br.com.winter.projetoAWS01.entity;

import br.com.winter.projetoAWS01.enums.EventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Envelope {

    private EventType eventType;
    private String data;
}
