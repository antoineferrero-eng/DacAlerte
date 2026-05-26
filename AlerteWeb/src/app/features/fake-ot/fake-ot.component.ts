import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-fake-ot',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="padding: 50px; text-align: center;">
      <h1>Génération de Faux Ordres de Travail</h1>
      <p>Cette page sert à créer des OT bidons pour aujourd'hui.</p>
      
      <button 
        (click)="generateFakeOts()" 
        [disabled]="loading"
        style="padding: 15px 30px; font-size: 1.2rem; cursor: pointer; background-color: #007bff; color: white; border: none; border-radius: 5px;">
        {{ loading ? 'Génération en cours...' : 'Générer OTs' }}
      </button>

      <div *ngIf="message" style="margin-top: 20px; font-weight: bold; color: green;">
        {{ message }}
      </div>
      <div *ngIf="error" style="margin-top: 20px; font-weight: bold; color: red;">
        {{ error }}
      </div>
    </div>
  `
})
export class FakeOtComponent {
  private apiService = inject(ApiService);
  loading = false;
  message = '';
  error = '';

  generateFakeOts() {
    this.loading = true;
    this.message = '';
    this.error = '';

    this.apiService.createFakeOts().subscribe({
      next: () => {
        this.loading = false;
        this.message = 'Faux Ordres de Travail générés avec succès !';
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Erreur lors de la génération : ' + err.message;
      }
    });
  }
}
