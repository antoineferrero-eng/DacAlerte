import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home';
import { adminGuard } from './core/guards/admin.guard';
import { manageGuard } from './core/guards/manage.guard';

export const routes: Routes = [
  { path: 'home', redirectTo: 'home/all', pathMatch: 'full' },
  { path: 'home/:typeId', component: HomeComponent },
  {
    path: 'admin',
    loadComponent: () => import('./features/admin/admin').then(m => m.AdminComponent),
    canActivate: [adminGuard]
  },
  {
    path: 'manage',
    loadComponent: () => import('./features/manage/manage').then(m => m.ManageComponent),
    canActivate: [manageGuard]
  },
  {
    path: 'fake-ot',
    loadComponent: () => import('./features/fake-ot/fake-ot.component').then(m => m.FakeOtComponent)
  },
  { path: '', redirectTo: 'home/all', pathMatch: 'full' }
];