export interface IValidator {
  id?: string;
  name?: string | null;
  walletAddress?: string;
  price?: number;
  statusCode?: string;
  description?: string | null;
  parameters?: string;
}

export const defaultValue: Readonly<IValidator> = {
};
