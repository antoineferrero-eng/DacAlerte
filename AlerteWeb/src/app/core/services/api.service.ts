import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { shareReplay, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private http = inject(HttpClient);
  private readonly API_URL = environment.apiUrl || '';
  private bulletinsCache = new Map<string, Observable<any[]>>();
  private geoJsonCache$: Observable<any> | null = null;

  getDepartmentsGeoJson(): Observable<any> {
    if (!this.geoJsonCache$) {
      this.geoJsonCache$ = this.http.get('/dataPoly.json').pipe(
        shareReplay(1)
      );
    }
    return this.geoJsonCache$;
  }

  getData(): Observable<any> {
    return this.http.get(`${this.API_URL}/departements`);
  }

  getBulletinByDepartement(num: string | number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/bulletins?dep=${num}`);
  }

  getBulletinsByDate(date: string): Observable<any[]> {
    if (!this.bulletinsCache.has(date)) {
      const request = this.http.get<any[]>(`${this.API_URL}/bulletins?date=${date}`).pipe(
        catchError(err => {
          return of([]);
        }),
        shareReplay(1)
      );
      this.bulletinsCache.set(date, request);
    }
    return this.bulletinsCache.get(date)!;
  }

  getFullConfig(): Observable<any> {
    return this.http.get(`${this.API_URL}/config`);
  }

  getActiveLevels(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/config/alert-levels`);
  }
  setActiveLevels(levels: string[]): Observable<any> {
    return this.http.post(`${this.API_URL}/config/alert-levels`, levels);
  }

  getActiveTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API_URL}/config/alert-types`);
  }
  setActiveTypes(types: string[]): Observable<any> {
    return this.http.post(`${this.API_URL}/config/alert-types`, types);
  }

  getMailCron(): Observable<{ mailCron: string }> {
    return this.http.get<{ mailCron: string }>(`${this.API_URL}/config/mail-time`);
  }
  setMailCron(cron: string): Observable<any> {
    return this.http.post(`${this.API_URL}/config/mail-time`, { cron });
  }

  getUpdateCron(): Observable<{ updateCron: string }> {
    return this.http.get<{ updateCron: string }>(`${this.API_URL}/config/update-time`);
  }
  setUpdateCron(cron: string): Observable<any> {
    return this.http.post(`${this.API_URL}/config/update-time`, { cron });
  }

  getUsers(): Observable<import('../models/user.dto').UserDTO[]> {
    return this.http.get<import('../models/user.dto').UserDTO[]>(`${this.API_URL}/users`);
  }

  updateUser(id: number, level: number, region: string | null): Observable<import('../models/user.dto').UserDTO> {
    return this.http.patch<import('../models/user.dto').UserDTO>(`${this.API_URL}/users/${id}`, { level, region });
  }

  getRessources(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/ressources`);
  }

  getManageRessources(level?: number, depts?: string[]): Observable<any[]> {
    let url = `${this.API_URL}/ressources/manage`;
    const params: string[] = [];
    if (level !== undefined) {
      params.push(`level=${level}`);
    }
    if (depts && depts.length > 0) {
      params.push(`depts=${depts.join(',')}`);
    }
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }
    return this.http.get<any[]>(url);
  }

  getOts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/ordre-de-travails`);
  }
  createFakeOts(): Observable<any> {
    return this.http.post(`${this.API_URL}/ordre-de-travails/fake`, {});
  }

  getSites(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/sites`);
  }
  sendResourceEmail(dkCode: string): Observable<any> {
    return this.http.post(`${this.API_URL}/ressources/${dkCode}/email`, {});
  }
  sendResourceMessage(dkCode: string): Observable<any> {
    return this.http.post(`${this.API_URL}/ressources/${dkCode}/message`, {});
  }
}