import { Injectable } from '@angular/core';
import {ApiService} from './general-api.service';
import {Observable, of} from 'rxjs';
import {FileResponse, JoinColumnProp, SearchQuery} from '../models/file.model';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor(private apiService: ApiService) { }

  uploadFile(file: FormData): Observable<FileResponse> {
    return this.apiService.post('image', file);
  }

  getFilesByTag(tagName: string): Observable<FileResponse[]> {


    const query: SearchQuery = {
      pageNumber: 0,
      pageSize: 20,
      sortOrder: {
        ascendingOrder: [
          "title"
        ]
      },
    };

    if(tagName) {
      query.joinColumnProps = [{
            joinColumnName : "labels",
            searchFilter : {
              operator: "LIKE",
              property: "name",
              value: tagName
            }
        }];
    }

    return this.apiService.post('image/search', query)
  }
}

