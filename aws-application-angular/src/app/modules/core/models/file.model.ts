export interface FileResponse {
  id: number,
  title: string,
  imagePath: string,
  previewImagePath: string,
  imageFileName: string
}


export interface SearchFilter {
  operator: string;
  property: string;
  value: string;
}

export interface SortOrder {
  ascendingOrder: string[];
}

export interface SearchFilter {
  operator: string;
  property: string;
  value: string;
}

export interface JoinColumnProp {
  joinColumnName: string;
  searchFilter: SearchFilter;
}

export interface SearchQuery {
  pageNumber: number;
  pageSize: number;
  sortOrder: SortOrder;
  joinColumnProps?: JoinColumnProp[];
}


