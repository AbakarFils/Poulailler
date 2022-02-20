import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'directeur',
        data: { pageTitle: 'serverApp.directeur.home.title' },
        loadChildren: () => import('./directeur/directeur.module').then(m => m.DirecteurModule),
      },
      {
        path: 'employe',
        data: { pageTitle: 'serverApp.employe.home.title' },
        loadChildren: () => import('./employe/employe.module').then(m => m.EmployeModule),
      },
      {
        path: 'variable',
        data: { pageTitle: 'serverApp.variable.home.title' },
        loadChildren: () => import('./variable/variable.module').then(m => m.VariableModule),
      },
      {
        path: 'temperature',
        data: { pageTitle: 'serverApp.temperature.home.title' },
        loadChildren: () => import('./temperature/temperature.module').then(m => m.TemperatureModule),
      },
      {
        path: 'humidite',
        data: { pageTitle: 'serverApp.humidite.home.title' },
        loadChildren: () => import('./humidite/humidite.module').then(m => m.HumiditeModule),
      },
      {
        path: 'co-2',
        data: { pageTitle: 'serverApp.co2.home.title' },
        loadChildren: () => import('./co-2/co-2.module').then(m => m.Co2Module),
      },
      {
        path: 'oeuf',
        data: { pageTitle: 'serverApp.oeuf.home.title' },
        loadChildren: () => import('./oeuf/oeuf.module').then(m => m.OeufModule),
      },
      {
        path: 'nh-3',
        data: { pageTitle: 'serverApp.nH3.home.title' },
        loadChildren: () => import('./nh-3/nh-3.module').then(m => m.NH3Module),
      },
      {
        path: 'equipement',
        data: { pageTitle: 'serverApp.equipement.home.title' },
        loadChildren: () => import('./equipement/equipement.module').then(m => m.EquipementModule),
      },
      {
        path: 'lampe',
        data: { pageTitle: 'serverApp.lampe.home.title' },
        loadChildren: () => import('./lampe/lampe.module').then(m => m.LampeModule),
      },
      {
        path: 'ventilateur',
        data: { pageTitle: 'serverApp.ventilateur.home.title' },
        loadChildren: () => import('./ventilateur/ventilateur.module').then(m => m.VentilateurModule),
      },
      {
        path: 'serrure',
        data: { pageTitle: 'serverApp.serrure.home.title' },
        loadChildren: () => import('./serrure/serrure.module').then(m => m.SerrureModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
