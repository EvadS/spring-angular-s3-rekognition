import {Component, OnDestroy, OnInit} from '@angular/core';
import {FileService} from '../core/services/file.service';
import {takeUntil, tap} from 'rxjs/operators';
import {Subject} from 'rxjs';
import {FileResponse} from '../core/models/file.model';

@Component({
  selector: 'app-image-manager',
  templateUrl: './image-manager.component.html',
  styleUrls: ['./image-manager.component.scss']
})
export class ImageManagerComponent implements OnInit, OnDestroy {

  readonly acceptFileTypes = 'image/png, image/jpeg';

  currentFile: File;

  images: FileResponse[] = [];

  private destroy$: Subject<void> = new Subject<void>();

  private searchText: string = '';

  constructor(private fileService: FileService) {
  }

  ngOnInit(): void {
    this.searchImages();
  }


  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onFileInputChanged(event: any): void {
    this.currentFile = event.target.files[0];
  }

  uploadFile(): void {
    if (this.currentFile) {
      const fileForUploading = this.currentFile;

      const formData = new FormData();
      formData.append('file', fileForUploading);
      formData.append('title', this.currentFile.name);
      this.fileService.uploadFile(formData)
        .pipe(
          tap(() => {
            if (this.currentFile === fileForUploading) {
              this.currentFile = null;
            }
          }),
          takeUntil(this.destroy$),
        ).subscribe(() => this.searchImages())
    }
  }

  onSearchTextChanged($event: any): void {
    this.searchText = $event.target.value;
  }

  searchImages(): void {
    this.fileService.getFilesByTag(this.searchText)
      .pipe(takeUntil(this.destroy$))
      .subscribe(images => this.images = images);
  }
}
