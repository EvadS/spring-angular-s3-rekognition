import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ImageManagerComponent} from "./modules/image-manager/image-manager.component";

const routes: Routes = [
  {
    path: '**',
    redirectTo: 'images'
  },
  {
    path: 'images',
    component: ImageManagerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
