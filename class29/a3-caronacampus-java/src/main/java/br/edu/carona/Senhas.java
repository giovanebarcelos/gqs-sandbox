package br.edu.carona;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2 com sal fixo: suficiente para a demonstração (ver riscos no relatório), não para produção. */
public final class Senhas {
    private Senhas() {}

    public static String hash(String senha) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), "carona-campus".getBytes(StandardCharsets.UTF_8), 50_000, 256);
            byte[] bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return HexFormat.of().formatHex(bytes);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Comparação em tempo constante. */
    public static boolean confere(String senha, String hashGuardado) {
        return MessageDigest.isEqual(hash(senha).getBytes(StandardCharsets.UTF_8), hashGuardado.getBytes(StandardCharsets.UTF_8));
    }
}
