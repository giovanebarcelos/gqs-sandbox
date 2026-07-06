package tdd20ex.ex14_processador_de_relatorios;

import java.util.Map;

public class ProcessadorRelatorios {
    private ColetorDados coletor;
    private FormatadorRelatorio formatador;
    private ServicoEmail servicoEmail;

    public ProcessadorRelatorios(ColetorDados coletor, FormatadorRelatorio formatador, ServicoEmail servicoEmail) {
        this.coletor = coletor;
        this.formatador = formatador;
        this.servicoEmail = servicoEmail;
    }

    public boolean gerarEEnviarRelatorio(String periodo, String destinatario, String formato) {
        try {
            Map<String, Object> dados = coletor.coletarDados(periodo);

            if (dados.isEmpty()) {
                return false;
            }

            String conteudo = formatarRelatorio(dados, formato);
            return servicoEmail.enviarRelatorio(destinatario, conteudo, formato);

        } catch (Exception e) {
            return false;
        }
    }

    protected String formatarRelatorio(Map<String, Object> dados, String formato) {
        if ("PDF".equalsIgnoreCase(formato)) {
            return formatador.formatarPDF(dados);
        } else {
            return formatador.formatarHTML(dados);
        }
    }
}
