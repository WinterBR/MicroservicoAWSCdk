package br.com.winter.projetoAWS01.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlResponse {
    private String url;
    private long expirationTime;

}