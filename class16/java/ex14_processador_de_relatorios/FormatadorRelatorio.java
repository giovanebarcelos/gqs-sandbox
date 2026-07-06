package tdd20ex.ex14_processador_de_relatorios;

import java.util.Map;

interface FormatadorRelatorio {
    String formatarPDF(Map<String, Object> dados);
    String formatarHTML(Map<String, Object> dados);
}
