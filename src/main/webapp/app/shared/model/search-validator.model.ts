import { StatusCode } from "app/shared/model/enumerations/status-code.model";

export interface ISearchValidator {
  name?: string | null;
  searchText?: string | null;
  walletAddress?: string | null;
  verifierAddress?: string | null;
  statusCode?: StatusCode | null;
  createdByUserId?: string | null;
  page: number,
  size: number,
  sort: string

}

export const defaultValue: Readonly<ISearchValidator> = {
  page: 0,
  size: 10,
  sort: '',
};
