export interface ISearchContainer {
  name?: string | null;
  price?: string | null;
  searchText?: string | null;
  statusCode?: string | null;
  createdByUserId?: string | null;
  page: number,
  size: number,
  sort: string

}

export const defaultValue: Readonly<ISearchContainer> = {
  page: 0,
  size: 10,
  sort: '',
};
