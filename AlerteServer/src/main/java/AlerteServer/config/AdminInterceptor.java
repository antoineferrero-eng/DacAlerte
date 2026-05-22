package AlerteServer.config;

import AlerteServer.entity.User;
import AlerteServer.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepteur personnalisé pour restreindre l'accès à certaines routes
 * d'administration.
 * Vérifie l'identité de l'utilisateur connecté via Google OAuth2 et son niveau
 * de privilège dans la base locale.
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    /**
     * Intercepte la requête HTTP avant qu'elle n'atteigne le contrôleur cible.
     * Récupère le principal de sécurité, extrait l'email depuis les claims du jeton
     * Google JWT,
     * cherche l'utilisateur dans la base locale et valide si son niveau
     * d'autorisation est suffisant (level == 2).
     *
     * @param request  la requête HTTP entrante
     * @param response la réponse HTTP sortante
     * @param handler  l'objet de gestion ciblé par la requête
     * @return true si l'accès est autorisé et que la requête peut continuer, false
     *         sinon (renvoie 403 Forbidden)
     * @throws Exception en cas d'erreur de traitement
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            String mail = jwt.getClaimAsString("email");
            User user = userRepository.findByMail(mail).orElse(null);
            if (user != null && user.getLevel() >= 2) {
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
