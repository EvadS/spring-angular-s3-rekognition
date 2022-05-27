import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private httpClient: HttpClient) {}


  post<B, R>(path: string, body: B): Observable<R> {
    return this.httpClient.post<R>(this.concatPath(path), body)
  }

  private concatPath(path: string): string {
    return `${environment.apiUrl}/${path}`;
  }
}
