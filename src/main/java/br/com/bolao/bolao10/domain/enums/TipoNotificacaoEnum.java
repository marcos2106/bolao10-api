package br.com.bolao.bolao10.domain.enums;

/**
 * Enum que representa os tipos de eventos globais de notificação no Bolão.
 */
public enum TipoNotificacaoEnum {

    SUBIU_NIVEL("Entrou em novo nível", "fas fa-arrow-up", "#6ea204", "#d4edda"),  // Verde
    NOVO_LIDER_RANKING("Novo líder do ranking", "fas fa-crown", "#e0a800", "#fff3cd"), // Dourado
    NOVO_BADGE("Ganhou um selo de qualidade", "fas fa-shield-alt", "#007bff", "#cce5ff"), // Azul
    PARTIDA_FINALIZADA("Partida encerrada", "far fa-futbol", "#17a2b8", "#d1ecf1"),   // Ciano
    APOSTA_FINALIZADA("Aposta processada", "fas fa-receipt", "#6c757d", "#e2e3e5"),  // Cinza
    MUDANCA_ARTILHARIA("Mudança na artilharia", "fas fa-star", "#fd7e14", "#f8d7da"); // Laranja

    private final String descricao;
    private final String iconeClasse;
    private final String corBorda;
    private final String corFundo;

    TipoNotificacaoEnum(String descricao, String iconeClasse, String corBorda, String corFundo) {
        this.descricao = descricao;
        this.iconeClasse = iconeClasse;
        this.corBorda = corBorda;
        this.corFundo = corFundo;
    }

    public String getDescricao() { return descricao; }
    public String getIconeClasse() { return iconeClasse; }
    public String getCorBorda() { return corBorda; }
    public String getCorFundo() { return corFundo; }
}
