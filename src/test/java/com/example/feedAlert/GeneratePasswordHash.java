package com.example.feedAlert;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário para gerar hashes BCrypt de senhas.
 * 
 * Uso:
 *   - Execute como aplicação Java: Run as Java Application
 *   - Ou via Maven: mvn exec:java -Dexec.mainClass="com.example.feedAlert.GeneratePasswordHash"
 * 
 * Útil para:
 *   - Gerar hashes para inserir no banco de dados
 *   - Criar novos usuários
 *   - Resetar senhas
 */
public class GeneratePasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Altere a senha aqui conforme necessário
        String password = args.length > 0 ? args[0] : "admin123";
        
        String hash = encoder.encode(password);
        
        System.out.println("═══════════════════════════════════════════");
        System.out.println("  GERADOR DE HASH BCrypt");
        System.out.println("═══════════════════════════════════════════");
        System.out.println("Senha: " + password);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println("\nVerificação:");
        System.out.println("  Hash válido: " + encoder.matches(password, hash));
        System.out.println("═══════════════════════════════════════════");
        System.out.println("\nPara usar em SQL:");
        System.out.println("  UPDATE tb_user SET password = '" + hash + "' WHERE email = 'seu-email@example.com';");
    }
}
