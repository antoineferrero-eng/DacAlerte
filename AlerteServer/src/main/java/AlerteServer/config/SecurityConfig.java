package AlerteServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité de l'application AlerteServer.
 * Active la sécurité web et configure les autorisations de requêtes ainsi que
 * la validation des jetons Google JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité pour l'application.
     * Configure :
     * - La désactivation de la protection CSRF (les APIs REST sans état n'en ont
     * généralement pas besoin)
     * - L'activation de CORS (Cross-Origin Resource Sharing) en se basant sur les
     * règles définies dans WebClientConfig
     * - Les autorisations d'accès aux routes (la configuration et les utilisateurs
     * sont sécurisés, le reste est public)
     * - Le rôle de serveur de ressources OAuth2 pour valider automatiquement les
     * jetons JWT fournis par Google
     *
     * @param http l'objet HttpSecurity permettant de configurer la sécurité web
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/config/**", "/users/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
