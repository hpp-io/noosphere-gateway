import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected, createSlice } from '@reduxjs/toolkit';

import { IUser, defaultValue } from 'app/shared/model/user.model';
import { EntityState, IQueryParams, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IUpdateWallet } from 'app/shared/model/update-wallet.model';

export interface UserState extends EntityState<IUser> {
  walletAddress: string;
  apiKey: string;
  keystoreFile: Blob;
  fileName: string;
}

const initialState: UserState = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
  walletAddress: null,
  apiKey: null,
  keystoreFile: null as Blob | null,
  fileName: '',
};

const apiUrl = 'api/users';

// Async Actions

export const getUsers = createAsyncThunk('userManagement/fetch_users', async ({page, size, sort}: IQueryParams) => {
  const requestUrl = `${ apiUrl }${ sort ? `?page=${ page }&size=${ size }&sort=${ sort }` : '' }`;
  return axios.get<IUser[]>(requestUrl);
});

export const getMyWalletAddress = createAsyncThunk('userManagement/fetch_user_wallet', async () => {
  const requestUrl = `${ apiUrl }/mine/wallet`;
  return axios.get<string>(requestUrl);
});

export const createMyWalletAddress = createAsyncThunk('userManagement/create_user_wallet', async (entity: IUpdateWallet, thunkAPI) => {
  const requestUrl = `${ apiUrl }/mine/wallet`;
  return axios.post<string>(requestUrl, entity);
});

export const updateMyWalletAddress = createAsyncThunk('userManagement/update_user_wallet', async (entity: IUpdateWallet, thunkAPI) => {
  const requestUrl = `${ apiUrl }/mine/wallet`;
  return axios.put<string>(requestUrl, entity);
});
export const createMyApiKey = createAsyncThunk('userManagement/create_user_api_key', async (entity) => {
  const requestUrl = `${ apiUrl }/mine/api-key`;
  const requestBody = {};
  return axios.post<string>(requestUrl, requestBody);
});

export const getMyApiKey = createAsyncThunk('userManagement/fetch_user_api_key', async () => {
  const requestUrl = `${ apiUrl }/mine/api-key`;
  return axios.get<string>(requestUrl);
});

export const updateUser = createAsyncThunk('userManagement/update_user',
    async (entity: IUser, thunkAPI) => {
      const requestUrl = `${ apiUrl }/${ entity.id }`;
      const requestBody = {
        name: entity.name,
        ownerAddress: entity.ownerAddress,
        langKey: entity.langKey,
        activated: entity.activated,
      };
      return axios.put<IUser>(requestUrl, requestBody);
    });

export const createMyKeystore = createAsyncThunk(
    'userManagement/createMyKeystore',
    async (data: { keyAlias: string; password: string; privateKey: string; isWallet: boolean, createHppWallet?: boolean, walletAddress?: string }) => {
      const requestUrl = `${ apiUrl }/mine/keystore`;
      const response = await axios.post(
          requestUrl, data,
          {
            responseType: 'blob',
          }
      );
      return {
        file: response.data,
        fileName: `${data.keyAlias}.p12`,
      };
    },
    {
      serializeError: serializeAxiosError,
    }
);

export const UserManagementSlice = createSlice({
  name: 'userManagement',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
    .addCase(createMyKeystore.fulfilled, (state, action) => {
      state.loading = false;
      state.keystoreFile = action.payload.file;
      state.fileName = action.payload.fileName;
    })
    .addCase(getUsers.pending, (state, action) => {
      state.updateSuccess = false;
      state.loading = true;
      state.updating = false;
      state.errorMessage = null;
    })
    .addCase(getUsers.rejected, (state, action) => {
      state.errorMessage = action.error.message;
      state.loading = false;
    })
    .addCase(getUsers.fulfilled, (state, action) => {
      state.entities = action.payload.data;
    })
    .addMatcher(isFulfilled(getMyWalletAddress), (state, action) => {
      state.loading = false;
      state['walletAddress'] = action.payload.data;
    })
    .addMatcher(isFulfilled(createMyWalletAddress, updateMyWalletAddress), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = true;
      state['walletAddress'] = action.payload.data;
    })
    .addMatcher(isPending(getMyWalletAddress), (state, action) => {
      state.updateSuccess = false;
      state.loading = true;
      state.updating = false;
      state.errorMessage = null;
    })
    .addMatcher(isPending(createMyWalletAddress, updateMyWalletAddress), (state, action) => {
      state.updateSuccess = false;
      state.loading = true;
      state.updating = true;
      state.errorMessage = null;
    })
    .addMatcher(isRejected(createMyWalletAddress), (state, action) => {
      state.updateSuccess = false;
      state.loading = false;
      state.updating = false;
      state.errorMessage = null;
    })
    .addMatcher(isFulfilled(getMyApiKey), (state, action) => {
      state.loading = false;
      state['apiKey'] = action.payload.data;
    })
    .addMatcher(isFulfilled(createMyApiKey), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = true;
      state['apiKey'] = action.payload.data;
    })
    .addMatcher(isPending(getMyApiKey), (state, action) => {
      state.updateSuccess = false;
      state.loading = true;
      state.updating = false;
      state.errorMessage = null;
    })
    .addMatcher(isPending(createMyApiKey), (state, action) => {
      state.updateSuccess = false;
      state.loading = true;
      state.updating = true;
      state.errorMessage = null;
    })
    .addMatcher(isRejected(createMyApiKey), (state, action) => {
      state.updateSuccess = false;
      state.loading = false;
      state.updating = false;
      state.errorMessage = null;
    })
    .addMatcher(isFulfilled(updateUser), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = true;
      state.entity = action.payload.data;
    })
    .addMatcher(isPending(updateUser), (state, action) => {
      state.updateSuccess = false;
      state.loading = true;
      state.updating = false;
      state.errorMessage = null;
    })
    .addMatcher(isRejected(updateUser), (state, action) => {
      state.updateSuccess = false;
      state.loading = false;
      state.updating = false;
      state.errorMessage = null;
    })
    .addMatcher(isPending(createMyKeystore), state => {
      state.errorMessage = null;
      state.loading = true;
      state.keystoreFile = null;
      state.fileName = '';
    })
    .addMatcher(isRejected(createMyKeystore), (state, action) => {
      state.loading = false;
      state.errorMessage = action.error.message;
    })
    ;
  },
});

export const {reset} = UserManagementSlice.actions;

// Reducer
export default UserManagementSlice.reducer;
