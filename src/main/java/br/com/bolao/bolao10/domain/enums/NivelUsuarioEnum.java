package br.com.bolao.bolao10.domain.enums;

/**
 * Enum que representa os níveis de progressão do usuário no Bolão,
 * baseados na pontuação total acumulada ao longo do campeonato.
 */
public enum NivelUsuarioEnum {

    SEM_NIVEL   ("Sem Nível",      0,   19,  0),
    AMADOR      ("Amador",         20,  39,  1),
    JUVENIL     ("Juvenil",        40,  119, 2),
    PROFISSIONAL("Profissional",   120, 144, 3),
    BETEIRO     ("Beteiro",        145, 169, 4),
    LENDA       ("Lenda",          170, Integer.MAX_VALUE, 5);

    private final String descricao;
    private final int pontuacaoMin;
    private final int pontuacaoMax;
    /** Código numérico usado para montar o nome do arquivo PNG no front-end (ex: 1 → "01.png"). */
    private final int codigo;

    NivelUsuarioEnum(String descricao, int pontuacaoMin, int pontuacaoMax, int codigo) {
        this.descricao    = descricao;
        this.pontuacaoMin = pontuacaoMin;
        this.pontuacaoMax = pontuacaoMax;
        this.codigo       = codigo;
    }

    public String getDescricao()    { return descricao; }
    public int getPontuacaoMin()    { return pontuacaoMin; }
    public int getPontuacaoMax()    { return pontuacaoMax; }
    public int getCodigo()          { return codigo; }

    /**
     * Calcula o nível do usuário com base na pontuação total.
     *
     * @param pontuacao pontuação acumulada do usuário (pode ser null)
     * @return o NivelUsuarioEnum correspondente à faixa de pontuação
     */
    public static NivelUsuarioEnum calcularNivel(Integer pontuacao) {
        if (pontuacao == null || pontuacao < 0) {
            return SEM_NIVEL;
        }
        for (NivelUsuarioEnum nivel : values()) {
            if (pontuacao >= nivel.pontuacaoMin && pontuacao <= nivel.pontuacaoMax) {
                return nivel;
            }
        }
        return LENDA;
    }
}
