export interface IUpdateWallet {
  ownerAddress?: string;
}

export const defaultValue: Readonly<IUpdateWallet> = {
  ownerAddress: ''
};
