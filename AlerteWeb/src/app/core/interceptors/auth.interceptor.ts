import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const idToken = localStorage.getItem('id_token');
  let url = req.url;

  if (url.startsWith('/') && !url.endsWith('.json') && !url.startsWith('/assets/') && environment.apiUrl) {
    url = `${environment.apiUrl}${url}`;
  }

  let authReq = req.clone({ url });

  if (idToken) {
    authReq = authReq.clone({
      headers: authReq.headers.set('Authorization', `Bearer ${idToken}`)
    });
  }

  return next(authReq);
};
