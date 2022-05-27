import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CoreModule} from './modules/core/core.module';
import {CommonModule} from '@angular/common';
import {ImageManagerComponent} from "./modules/image-manager/image-manager.component";


@NgModule({
  declarations: [
    AppComponent,
    ImageManagerComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    CoreModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
